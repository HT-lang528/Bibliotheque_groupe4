package fr.bibliotheque.restcontroller;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import fr.bibliotheque.dao.IDAOAvis;
import fr.bibliotheque.model.Avis;


@WebMvcTest(controllers = AvisRestController.class)
public class AvisRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IDAOAvis dao;

    @Test
    @WithMockUser
    void shouldFindAllStatusOk() throws Exception {
        // given
        String url = "/api/avis";

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders.get(url)
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk()); // Status OK
        result.andExpect(MockMvcResultMatchers.jsonPath("$").exists()); // On vérifie si y'a bien une liste

        Mockito.verify(this.dao).findAll(); // On vérifie si findAll a été appelée
    }

    @Test
    @WithMockUser
    void shouldFindByIdStatusOk() throws Exception {
        // given
        int id = 1;
        String url = "/api/avis/" + id;

        Mockito.when(this.dao.findById(id))
            .thenReturn(Optional.of(new Avis()));

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders.get(url)
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
        
        Mockito.verify(this.dao).findById(id);
    }

    @Test
    @WithMockUser
    void shouldCreateStatusOk() throws Exception {
        // given
        String url = "/api/avis";
        ArgumentCaptor<Avis> cptAvis = ArgumentCaptor.captor();

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(url)
                .content("""
                    {
                        "note": 4.5,
                        "commentaire": "Très bon livre",
                        "date": "2026-11-06"
                    }
                """)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(this.dao).save(cptAvis.capture()); // On vérifie si save a été appelée

        // On vérifie l'avis envoyée dans save
        Avis savedAvis = cptAvis.getValue();
        Assertions.assertEquals(4.5, savedAvis.getNote());
        Assertions.assertEquals("Très bon livre", savedAvis.getCommentaire());
        Assertions.assertEquals(LocalDate.now(), savedAvis.getDate());
        //Assertions.assertNotNull(savedAvis.getId()); // Après save, l'id n'est plus à 0 car auto-incrémenté
        //Assertions.assertNotEquals(0, savedAvis.getId()); // Après save, l'id n'est plus à 0 car auto-incrémenté
    }

    @Test
    @WithMockUser
    void shouldUpdateStatusOk() throws Exception {
        // given
        int id = 1;
        String url = "/api/avis/" + id;
        //récupérer l'objet Avis que le contrôleur a envoyé au DAO pour vérifier son contenu:
        ArgumentCaptor<Avis> cptAvis = ArgumentCaptor.captor(); 

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .put(url)
                .content("""
                    {
                        "note": 5.0,
                        "commentaire": "Excellent livre",
                        "date": "2026-11-06"
                    }
                """)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(this.dao).save(cptAvis.capture());

        Avis updatedAvis = cptAvis.getValue();

        Assertions.assertEquals(id, updatedAvis.getId());
        Assertions.assertEquals(5.0, updatedAvis.getNote());
        Assertions.assertEquals("Excellent livre", updatedAvis.getCommentaire());
        Assertions.assertEquals(LocalDate.now(), updatedAvis.getDate());
    }

    @Test
    @WithMockUser
    void shouldDeleteByIdStatusOk() throws Exception {
        // given
        int id = 1;
        String url = "/api/avis/" + id;

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .delete(url)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(this.dao).deleteById(id); // On vérifie si deleteById a été appelée
    }
}
