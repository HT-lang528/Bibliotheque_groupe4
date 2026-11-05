package fr.bibliotheque.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import fr.bibliotheque.dao.IDAOAuteur;
import fr.bibliotheque.dao.IDAOCollection;
import fr.bibliotheque.dao.IDAOEditeur;
import fr.bibliotheque.dao.IDAOLivre;
import fr.bibliotheque.model.Auteur;
import fr.bibliotheque.model.Collection;
import fr.bibliotheque.model.Editeur;
import fr.bibliotheque.model.Livre;

// Le service est réel, ses accès à la base sont simulés.
@ExtendWith(MockitoExtension.class)
class LivreServiceTest {
    @Mock private IDAOLivre daoLivre;
    @Mock private IDAOAuteur daoAuteur;
    @Mock private IDAOEditeur daoEditeur;
    @Mock private IDAOCollection daoCollection;
    @InjectMocks private LivreService livreService;

    private Livre livreAvecRelations() {
        Livre livre = new Livre("Le Petit Prince", "Un voyage", 1943);
        Auteur auteur = new Auteur();
        auteur.setId(1);
        Editeur editeur = new Editeur();
        editeur.setId(2);
        livre.setAuteur(auteur);
        livre.setEditeur(editeur);
        return livre;
    }

    private void simulerRelationsConnues() {
        Auteur auteur = new Auteur(1, "Française", "de Saint-Exupéry", "Antoine");
        Editeur editeur = new Editeur(2, "Gallimard", "France");
        when(daoAuteur.findById(1)).thenReturn(Optional.of(auteur));
        when(daoEditeur.findById(2)).thenReturn(Optional.of(editeur));
    }

    @Test
    void findAllRetourneLesLivres() {
        Livre livre = livreAvecRelations();
        when(daoLivre.findAll()).thenReturn(List.of(livre));
        assertEquals(List.of(livre), livreService.findAll());
    }

    @Test
    void findByIdRetourneLeLivreExistant() {
        Livre livre = livreAvecRelations();
        livre.setId(1);
        when(daoLivre.findById(1)).thenReturn(Optional.of(livre));
        assertEquals(1, livreService.findById(1).getId());
    }

    @Test
    void findByIdSignaleUnLivreIntrouvable() {
        when(daoLivre.findById(99)).thenReturn(Optional.empty());
        IllegalArgumentException erreur = assertThrows(IllegalArgumentException.class,
                () -> livreService.findById(99));
        assertEquals("Livre introuvable : 99", erreur.getMessage());
    }

    @Test
    void createIgnoreLIdFourniEtRechargeLesRelations() {
        Livre livre = livreAvecRelations();
        livre.setId(99);
        livre.setCollection(new Collection(3, null));
        simulerRelationsConnues();
        when(daoCollection.findById(3)).thenReturn(Optional.of(new Collection(3, "Folio")));
        when(daoLivre.save(any(Livre.class))).thenAnswer(invocation -> {
            Livre aEnregistrer = invocation.getArgument(0);
            assertNull(aEnregistrer.getId());
            aEnregistrer.setId(10);
            return aEnregistrer;
        });

        Livre resultat = livreService.create(livre);

        assertEquals(10, resultat.getId());
        assertEquals("de Saint-Exupéry", resultat.getAuteur().getNom());
        assertEquals("Gallimard", resultat.getEditeur().getNom());
        assertEquals("Folio", resultat.getCollection().getNom());
    }

    @Test
    void createAccepteUneCollectionAbsente() {
        Livre livre = livreAvecRelations();
        simulerRelationsConnues();
        when(daoLivre.save(livre)).thenReturn(livre);
        assertNull(livreService.create(livre).getCollection());
        verifyNoInteractions(daoCollection);
    }

    @Test
    void updateConserveLIdEtMetAJourLesRelations() {
        Livre existant = livreAvecRelations();
        existant.setId(5);
        existant.setCollection(new Collection(3, "Folio"));
        Livre modifications = livreAvecRelations();
        modifications.setId(99);
        modifications.setTitre("Titre corrigé");
        modifications.setResume("Résumé corrigé");
        modifications.setAnnee(1944);
        when(daoLivre.findById(5)).thenReturn(Optional.of(existant));
        simulerRelationsConnues();
        when(daoLivre.save(existant)).thenReturn(existant);

        Livre resultat = livreService.update(5, modifications);

        assertEquals(5, resultat.getId());
        assertEquals("Titre corrigé", resultat.getTitre());
        assertEquals("Résumé corrigé", resultat.getResume());
        assertEquals(1944, resultat.getAnnee());
        assertEquals("Gallimard", resultat.getEditeur().getNom());
        assertNull(resultat.getCollection(), "La collection facultative peut être retirée");
    }

    @Test
    void updateNeCreePasUnLivreAbsent() {
        when(daoLivre.findById(99)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> livreService.update(99, livreAvecRelations()));
        verify(daoLivre, never()).save(any());
        verifyNoInteractions(daoAuteur, daoEditeur, daoCollection);
    }

    @Test
    void createRefuseUnAuteurInconnu() {
        when(daoAuteur.findById(1)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> livreService.create(livreAvecRelations()));
        verify(daoLivre, never()).save(any());
    }

    @Test
    void createRefuseUnEditeurInconnu() {
        when(daoAuteur.findById(1)).thenReturn(Optional.of(new Auteur()));
        when(daoEditeur.findById(2)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> livreService.create(livreAvecRelations()));
        verify(daoLivre, never()).save(any());
    }

    @Test
    void createRefuseUneCollectionInconnue() {
        Livre livre = livreAvecRelations();
        livre.setCollection(new Collection(99, null));
        simulerRelationsConnues();
        when(daoCollection.findById(99)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> livreService.create(livre));
        verify(daoLivre, never()).save(any());
    }

    @Test
    void createRefuseUnAuteurSansIdentifiant() {
        Livre livre = livreAvecRelations();
        livre.getAuteur().setId(null);
        ResponseStatusException erreur = assertThrows(ResponseStatusException.class,
                () -> livreService.create(livre));
        assertEquals(HttpStatus.BAD_REQUEST, erreur.getStatusCode());
        verifyNoInteractions(daoLivre, daoAuteur, daoEditeur, daoCollection);
    }

    @Test
    void createRefuseUnEditeurAbsent() {
        Livre livre = livreAvecRelations();
        livre.setEditeur(null);
        assertThrows(ResponseStatusException.class, () -> livreService.create(livre));
        verifyNoInteractions(daoLivre, daoAuteur, daoEditeur, daoCollection);
    }

    @Test
    void createRefuseUneCollectionSansIdentifiant() {
        Livre livre = livreAvecRelations();
        livre.setCollection(new Collection());
        simulerRelationsConnues();
        ResponseStatusException erreur = assertThrows(ResponseStatusException.class,
                () -> livreService.create(livre));
        assertEquals(HttpStatus.BAD_REQUEST, erreur.getStatusCode());
        verify(daoLivre, never()).save(any());
    }

    @Test
    void deleteSupprimeSeulementLeLivre() {
        Livre livre = livreAvecRelations();
        when(daoLivre.findById(1)).thenReturn(Optional.of(livre));
        livreService.delete(1);
        verify(daoLivre).delete(livre);
        verify(daoLivre).flush();
        verifyNoInteractions(daoAuteur, daoEditeur, daoCollection);
    }

    @Test
    void deleteSignaleUnLivreIntrouvableSansSupprimer() {
        when(daoLivre.findById(99)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> livreService.delete(99));
        verify(daoLivre, never()).delete(any(Livre.class));
        verify(daoLivre, never()).flush();
    }
}
