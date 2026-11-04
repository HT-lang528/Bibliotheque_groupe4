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

import fr.bibliotheque.entity.Livre;
import fr.bibliotheque.repository.LivreRepository;

// Test unitaire : aucun démarrage de Spring ni connexion à MySQL.
@ExtendWith(MockitoExtension.class)
class LivreServiceTest {

    // Mockito remplace le repository par un objet dont on contrôle les réponses.
	
    @Mock
    private LivreRepository livreRepository;

    // Le vrai service reçoit ce repository simulé.
    @InjectMocks
    private LivreService livreService;

    @Test
    void findAllRetourneLesLivres() {
    	
        // Préparer : définir ce que le repository doit renvoyer.
        Livre premier = new Livre("Le Petit Prince", "Un voyage entre les planètes", 1943);
        Livre second = new Livre("Vingt mille lieues sous les mers", "Une aventure sous-marine", 1870);
        when(livreRepository.findAll()).thenReturn(List.of(premier, second));

        // Agir : appeler la méthode du service.
        List<Livre> resultat = livreService.findAll();

        // Vérifier : contrôler le résultat.
        assertEquals(2, resultat.size());
        assertEquals("Le Petit Prince", resultat.get(0).getTitre());
        assertEquals("Vingt mille lieues sous les mers", resultat.get(1).getTitre());
    }

    @Test
    void findByIdRetourneLeLivreExistant() {
        Livre livre = new Livre("Le Petit Prince", "Un voyage", 1943);
        livre.setId(1L);
        when(livreRepository.findById(1L)).thenReturn(Optional.of(livre));

        Livre resultat = livreService.findById(1L);

        assertEquals(1L, resultat.getId());
        assertEquals("Le Petit Prince", resultat.getTitre());
    }

    @Test
    void findByIdSignaleUnLivreIntrouvable() {
        when(livreRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException erreur = assertThrows(
                IllegalArgumentException.class,
                () -> livreService.findById(99L));

        assertEquals("Livre introuvable : 99", erreur.getMessage());
    }

    @Test
    void createIgnoreLIdentifiantFourniEtRetourneLeLivreEnregistre() {
        Livre livre = new Livre("Le Petit Prince", "Un voyage", 1943);
        livre.setId(99L);

        // Vérifie l'objet envoyé au repository, puis simule l'identifiant généré.
        when(livreRepository.save(any(Livre.class))).thenAnswer(invocation -> {
            Livre aEnregistrer = invocation.getArgument(0);
            assertNull(aEnregistrer.getId(), "Une création ne doit pas réutiliser l'id fourni");
            aEnregistrer.setId(2L);
            return aEnregistrer;
        });

        Livre resultat = livreService.create(livre);

        assertEquals(2L, resultat.getId());
        assertEquals("Le Petit Prince", resultat.getTitre());
        verify(livreRepository).save(livre);
    }

    @Test
    void updateModifieLesChampsEtConserveLIdentifiantExistant() {
        Livre existant = new Livre("Ancien titre", "Ancien résumé", 1900);
        existant.setId(1L);
        Livre modifications = new Livre("Nouveau titre", "Nouveau résumé", 2000);
        modifications.setId(99L);
        when(livreRepository.findById(1L)).thenReturn(Optional.of(existant));
        when(livreRepository.save(existant)).thenReturn(existant);

        Livre resultat = livreService.update(1L, modifications);

        assertEquals(1L, resultat.getId(), "L'id de l'URL désigne le livre à modifier");
        assertEquals("Nouveau titre", resultat.getTitre());
        assertEquals("Nouveau résumé", resultat.getResume());
        assertEquals(2000, resultat.getAnnee());
        verify(livreRepository).save(existant);
    }

    @Test
    void updateNeCreePasUnLivreSiLIdentifiantNExistePas() {
        when(livreRepository.findById(99L)).thenReturn(Optional.empty());
        Livre modifications = new Livre("Un titre", "Un résumé", 2000);

        assertThrows(IllegalArgumentException.class,
                () -> livreService.update(99L, modifications));

        verify(livreRepository, never()).save(any(Livre.class));
    }

    @Test
    void deleteSupprimeLeLivreExistant() {
        Livre livre = new Livre("Le Petit Prince", "Un voyage", 1943);
        livre.setId(1L);
        when(livreRepository.findById(1L)).thenReturn(Optional.of(livre));

        livreService.delete(1L);

        verify(livreRepository).delete(livre);
    }

    @Test
    void deleteSignaleUnLivreIntrouvableSansSupprimer() {
        when(livreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> livreService.delete(99L));

        verify(livreRepository, never()).delete(any(Livre.class));
    }
}
