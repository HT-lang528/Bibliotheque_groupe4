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

import fr.bibliotheque.dao.IDAOAuteur;
import fr.bibliotheque.model.Auteur;

@WebMvcTest(controllers = AuteurRestController.class)
public class AuteurRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IDAOAuteur dao;


    @Test
    @WithMockUser
    void shouldFindAllStatusOk()throws Exception {
        String url = "/api/auteur";
        
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders.get(url)
        );
        // then
        result.andExpect(MockMvcResultMatchers.status().isOk()); // Status OK
        result.andExpect(MockMvcResultMatchers.jsonPath("$").exists()); // On vérifie si y'a bien une liste

        Mockito.verify(this.dao).findAll(); 
    }

    @Test 
    @WithMockUser 
    void shouldFindById()throws Exception{
        int id = 1;
        String url = "/api/auteur/" + id;
        Mockito.when(this.dao.findById(id)).thenReturn(Optional.of(new Auteur()));
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders.get(url)
        );
        result.andExpect(MockMvcResultMatchers.status().isOk()); // Status OK
        result.andExpect(MockMvcResultMatchers.jsonPath("$").exists()); // On vérifie si y'a bien une liste

        Mockito.verify(this.dao).findById(id); 


    }


    @Test 
    @WithMockUser 
    void shouldCreateStatusOk()throws Exception{
        String url= "/api/auteur";
        ArgumentCaptor<Auteur> auteur = ArgumentCaptor.captor();
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(url)
                .content("""
                    {
                            
                            "nationalite":"francais",

                            "nom":"doe",
                            "prenom":"john"

                        }
                """)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())  
        );

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$").exists());
        Mockito.verify(this.dao).save(auteur.capture());

        Auteur saveAuteur=auteur.getValue();
        Assertions.assertEquals("francais", saveAuteur.getNationalite());
        Assertions.assertEquals("john", saveAuteur.getPrenom());
        Assertions.assertEquals("doe", saveAuteur.getNom());
        
    }

    @Test
    @WithMockUser
    void shouldDeleteByIdStatusOk() throws Exception {
        // given
        int id = 1;
        String url = "/api/auteur/" + id;

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

    @Test 
    @WithMockUser 
    void shouldUpdateStatueOk() throws Exception{
        int id = 1;
        String url = "/api/auteur/" + id;
        ArgumentCaptor<Auteur> auteur = ArgumentCaptor.captor();
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .put(url)
                .content("""
                    {
                            
                            "nationalite":"francais",

                            "nom":"doe",
                            "prenom":"john"

                        }
                """)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())  
        );

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$").exists());
        Mockito.verify(this.dao).save(auteur.capture());

        Auteur saveAuteur=auteur.getValue();
        Assertions.assertEquals("francais", saveAuteur.getNationalite());
        Assertions.assertEquals("john", saveAuteur.getPrenom());
        Assertions.assertEquals("doe", saveAuteur.getNom());
        
    }
}
