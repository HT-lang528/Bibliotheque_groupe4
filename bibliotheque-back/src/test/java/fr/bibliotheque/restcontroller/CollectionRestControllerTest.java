package fr.bibliotheque.restcontroller;

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

import fr.bibliotheque.restController.CollectionRestController;
import fr.bibliotheque.dao.IDAOCollection;
import fr.bibliotheque.model.Collection;

@WebMvcTest(controllers = CollectionRestController.class)
public class CollectionRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IDAOCollection dao;

    @Test
    @WithMockUser
    void shouldFindAllStatusOk() throws Exception {
        // given
        String url = "/api/collection";

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders.get(url)
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk()); // Status OK
        result.andExpect(MockMvcResultMatchers.jsonPath("$").exists()); 

        Mockito.verify(this.dao).findAll(); 
    }

    @Test
    @WithMockUser
    void shouldCreateStatusOk() throws Exception {
        // given
        String url = "/api/collection";

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(url)
                .content("""
                    {
                        "nom": "TEST DE CREATION"
                    }
                """)
                .contentType(MediaType.APPLICATION_JSON)
                // .contentType("application/json")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(this.dao).save(Mockito.any()); 
    }

    @Test
    @WithMockUser
    void shouldCreateStatusOkVersion2() throws Exception {
        // given
        String url = "/api/collection";
        ArgumentCaptor<Collection> cptCollection = ArgumentCaptor.captor();

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(url)
                .content("""
                    {
                        "nom": "TEST DE CREATION"
                    }
                """)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(this.dao).save(cptCollection.capture()); 

      
        Collection savedCollection = cptCollection.getValue();
        Assertions.assertEquals("TEST DE CREATION", savedCollection.getNom());
        Assertions.assertNotNull(savedCollection.getId()); 
        Assertions.assertNotEquals(0, savedCollection.getId()); 
    }

    @Test
    @WithMockUser
    void shouldDeleteByIdStatusOk() throws Exception {
        // given
        int id = 1;
        String url = "/api/collection/" + id;

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .delete(url)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(this.dao).deleteById(id); 
    }
}
