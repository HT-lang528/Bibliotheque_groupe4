package fr.bibliotheque.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import fr.bibliotheque.model.Auteur;
import fr.bibliotheque.model.Avis;
import fr.bibliotheque.model.Collection;
import fr.bibliotheque.model.Editeur;
import fr.bibliotheque.model.Livre;

// Vérifie réellement les relations JPA dans H2, jamais dans le MySQL du groupe.
@DataJpaTest
class LivrePersistenceTest {
    @Autowired private EntityManager entityManager;
    @Autowired private IDAOLivre daoLivre;
    @Autowired private IDAOAvis daoAvis;

    private Livre creerLivre(boolean avecCollection) {
        Auteur auteur = new Auteur(null, "Française", "de Saint-Exupéry", "Antoine");
        Editeur editeur = new Editeur(null, "Gallimard", "France");
        entityManager.persist(auteur);
        entityManager.persist(editeur);
        Livre livre = new Livre("Le Petit Prince", "Un voyage", 1943);
        livre.setAuteur(auteur);
        livre.setEditeur(editeur);
        if (avecCollection) {
            Collection collection = new Collection(null, "Folio");
            entityManager.persist(collection);
            livre.setCollection(collection);
        }
        return daoLivre.saveAndFlush(livre);
    }

    @Test
    void lesTroisRelationsSontRechargeesDepuisLaBase() {
        Integer id = creerLivre(true).getId();
        entityManager.clear();
        Livre relu = daoLivre.findById(id).orElseThrow();
        assertEquals("de Saint-Exupéry", relu.getAuteur().getNom());
        assertEquals("Gallimard", relu.getEditeur().getNom());
        assertEquals("Folio", relu.getCollection().getNom());
    }

    @Test
    void unLivreSansCollectionPeutRecevoirPlusieursAvis() {
        Livre livre = creerLivre(false);
        daoAvis.save(new Avis(null, 4, "Très bon", LocalDate.of(2026, 1, 1), livre));
        daoAvis.save(new Avis(null, 5, "Excellent", LocalDate.of(2026, 1, 2), livre));
        daoAvis.flush();
        entityManager.clear();
        assertEquals(2, daoAvis.count());
        assertNull(daoLivre.findById(livre.getId()).orElseThrow().getCollection());
    }
}
