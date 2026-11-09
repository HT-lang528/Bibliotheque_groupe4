package fr.bibliotheque.dao;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import fr.bibliotheque.model.Avis;

@DataJpaTest
public class IDAOAvisTest {

    @Autowired
    private IDAOAvis dao;

    @Test
    @Sql(scripts = "classpath:/create-avis.sql")
    void shouldFindByLivreIdSizeOk() {

        // given
        Integer idLivre = 1;

        // when
        List<Avis> avis = this.dao.findByLivreId(idLivre);

        // then
        Assertions.assertNotNull(avis);
        Assertions.assertEquals(2, avis.size());
    }
}
    

