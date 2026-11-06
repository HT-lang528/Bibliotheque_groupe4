package fr.bibliotheque.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.bibliotheque.model.Editeur;
import jakarta.persistence.EntityManager;

public class IDAOEditeurTest {
    @Autowired 
    private EntityManager entityManager;

    @Autowired 
    private IDAOEditeur dao;
    
    @Test
    void shouldCreateEditeur() {

        // given
        Editeur editeur = new Editeur(null, "SOLEIL", "France");

        // when
        Editeur savedEditeur = dao.save(editeur);

        // then
        Assertions.assertNotNull(savedEditeur.getId());
        Assertions.assertEquals("SOLEIL", savedEditeur.getNom());
        Assertions.assertEquals("France", savedEditeur.getPays());
    }

    @Test
    void shouldFindEditeurById() {

        // given
        Editeur editeur = new Editeur(null, "SOLEIL", "France");
        entityManager.persist(editeur);
        entityManager.flush();

        // when
        Editeur foundEditeur = dao.findById(editeur.getId()).orElse(null);

        // then
        Assertions.assertNotNull(foundEditeur);
        Assertions.assertEquals(editeur.getId(), foundEditeur.getId());
        Assertions.assertEquals("SOLEIL", foundEditeur.getNom());
        Assertions.assertEquals("France", foundEditeur.getPays());
    }

    @Test
    void shouldFindAllEditeurs() {

        // given
        Editeur editeur1 = new Editeur(null, "SOLEIL", "France");
        Editeur editeur2 = new Editeur(null, "GALLIMARD", "France");

        entityManager.persist(editeur1);
        entityManager.persist(editeur2);
        entityManager.flush();

        // when
        var editeurs = dao.findAll();

        // then
        Assertions.assertTrue(editeurs.size() >= 2);
        Assertions.assertTrue(editeurs.stream()
            .anyMatch(e -> e.getNom().equals("SOLEIL")));
        Assertions.assertTrue(editeurs.stream()
            .anyMatch(e -> e.getNom().equals("GALLIMARD")));
    }

    @Test
    void shouldUpdateEditeur() {

        // given
        Editeur editeur = new Editeur(null, "SOLEIL", "France");
        entityManager.persist(editeur);
        entityManager.flush();

        // when
        editeur.setNom("GALLIMARD");
        editeur.setPays("Belgique");

        dao.save(editeur);
        entityManager.flush();
        entityManager.clear();

        Editeur updatedEditeur = dao.findById(editeur.getId()).orElse(null);

        // then
        Assertions.assertNotNull(updatedEditeur);
        Assertions.assertEquals("GALLIMARD", updatedEditeur.getNom());
        Assertions.assertEquals("Belgique", updatedEditeur.getPays());
    }

    @Test
    void shouldDeleteEditeur() {

        // given
        Editeur editeur = new Editeur(null, "SOLEIL", "France");
        entityManager.persist(editeur);
        entityManager.flush();

        Integer id = editeur.getId();

        // when
        dao.deleteById(id);
        entityManager.flush();

        // then
        Assertions.assertFalse(dao.findById(id).isPresent());
    }
}
