package fr.bibliotheque.dao;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import fr.bibliotheque.model.Collection;

@DataJpaTest 
public class IDAOCollectionTest {
    @Autowired
    private IDAOCollection dao;

    @ParameterizedTest
    @CsvSource({
        "Dico",
        "Poche"
    })

    void TestFindById(String nom) {
        
        Collection collection = new Collection();
        collection.setNom(nom);

        Collection saved = this.dao.save(collection);
        Optional<Collection> opt = this.dao.findById(saved.getId());

        Assertions.assertNotNull(saved.getId()); 
        Assertions.assertTrue(opt.isPresent());
        Assertions.assertEquals(nom, opt.get().getNom());
    }

     @Test
    void TestFindAll() {

        this.dao.save(new Collection(null, "Dico"));
        this.dao.save(new Collection(null, "Poche"));

        Assertions.assertEquals(2, this.dao.findAll().size());
    }
}