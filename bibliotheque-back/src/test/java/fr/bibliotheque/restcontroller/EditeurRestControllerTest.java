package fr.bibliotheque.restcontroller;

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

import fr.bibliotheque.dao.IDAOEditeur;
import fr.bibliotheque.model.Editeur;


@WebMvcTest(controllers = EditeurRestController.class)
public class EditeurRestControllerTest {
   @Autowired
    private MockMvc mockMvc;

     @MockitoBean
    private IDAOEditeur dao;   

    @Test
    @WithMockUser
    void shouldFindAllStatusOk() throws Exception {
        // given
        String url = "/api/editeur";

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
        String url = "/api/editeur/" + id;

        Mockito.when(this.dao.findById(id))
            .thenReturn(Optional.of(new Editeur(1, "SOLEIL", "France")));
        
        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders.get(url)
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.nom").value("SOLEIL"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.pays").value("France"));

        Mockito.verify(this.dao).findById(id);
    }

    @Test
    @WithMockUser
    void shouldCreateStatusOk() throws Exception {
        // given
        String url = "/api/editeur";
        ArgumentCaptor<Editeur> cptEditeur = ArgumentCaptor.captor();

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(url)
                .content("""
                    {
                        "nom" : "SOLEIL"
                        "pays" : "France"
                    }
                    """)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(this.dao).save(cptEditeur.capture()); // On vérifie si save a été appelée

        // On vérifie l'editeur envoyée dans save
        Editeur savedEditeur = cptEditeur.getValue();
        Assertions.assertEquals("SOLEIL", savedEditeur.getNom());
        Assertions.assertEquals("France", savedEditeur.getPays());
        Assertions.assertNotNull(savedEditeur.getId()); 
        Assertions.assertNotEquals(0, savedEditeur.getId()); 

    }

    @Test
    @WithMockUser
    void shouldUpdateStatusOk() throws Exception {
        // given
        int id = 1;
        String url = "/api/editeur" + id;
        ArgumentCaptor<Editeur> cptEditeur = ArgumentCaptor.captor();


        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(url)
                .content("""
                    {
                        "nom" : "SOLEIL"
                        "pays" : "France"
                    }
                    """)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(this.dao).save(cptEditeur.capture());

        Editeur updatedEditeur = cptEditeur.getValue();

        Assertions.assertEquals("SOLEIL", updatedEditeur.getNom());
        Assertions.assertEquals("France", updatedEditeur.getPays());
        Assertions.assertNotNull(updatedEditeur.getId()); 
        Assertions.assertNotEquals(0, updatedEditeur.getId()); 
    }

    @Test
    @WithMockUser
    void shouldDeleteByIdStatusOk() throws Exception {
        // given
        int id = 1;
        String url = "/api/editeur/" + id;

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
