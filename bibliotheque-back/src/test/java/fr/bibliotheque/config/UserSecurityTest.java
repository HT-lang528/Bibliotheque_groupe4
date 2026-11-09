package fr.bibliotheque.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.web.servlet.MockMvc;
import fr.bibliotheque.dao.IDAOUser;
import fr.bibliotheque.model.User;

@SpringBootTest
@AutoConfigureMockMvc
class UserSecurityTest {
    @Autowired MockMvc mvc;
    @Autowired IDAOUser users;
    @Autowired PasswordEncoder passwords;
    @Autowired JwtEncoder encoder;
    @Autowired JwtDecoder decoder;
    @BeforeEach void reset() { users.deleteAll(); }

    private void register() throws Exception {
        mvc.perform(post("/api/user/subscribe").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"lecteur-test\",\"password\":\"Secret-test-2026\"}"))
            .andExpect(status().isOk());
    }
    private String token(String authority, Instant expiry) {
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),
            JwtClaimsSet.builder().subject("lecteur-test").issuedAt(expiry.minusSeconds(3600))
                .expiresAt(expiry).claim("scope", authority).build())).getTokenValue();
    }
    @Test void inscriptionHacheLeMotDePasseEtIgnoreLeRoleDemande() throws Exception {
        mvc.perform(post("/api/user/subscribe").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"lecteur-test\",\"password\":\"Secret-test-2026\",\"role\":\"ADMIN\"}"))
            .andExpect(status().isOk());
        User user = users.findByUsername("lecteur-test").orElseThrow();
        assertNotEquals("Secret-test-2026", user.getPassword());
        assertTrue(passwords.matches("Secret-test-2026", user.getPassword()));
        assertEquals("USER", user.getRole());
    }
    @Test void refuseInscriptionInvalideEtDoublon() throws Exception {
        mvc.perform(post("/api/user/subscribe").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"\",\"password\":\"court\"}"))
            .andExpect(status().isBadRequest());
        register();
        mvc.perform(post("/api/user/subscribe").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"lecteur-test\",\"password\":\"Secret-test-2026\"}"))
            .andExpect(status().isConflict());
    }
    @Test void connexionUtiliseLeCompteEtSonMotDePasseEnBase() throws Exception {
        register();
        String body = mvc.perform(post("/api/user/auth").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"lecteur-test\",\"password\":\"Secret-test-2026\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.token").isString()).andReturn().getResponse().getContentAsString();
        String jwt = body.split("\"token\":\"")[1].split("\"")[0];
        assertEquals("lecteur-test", decoder.decode(jwt).getSubject());
        assertTrue(java.util.Arrays.asList(decoder.decode(jwt).getClaimAsString("scope").split(" ")).contains("USER"));
    }
    @Test void refuseMotDePasseIncorrectEtUtilisateurInconnu() throws Exception {
        register();
        for (String name : new String[]{"lecteur-test", "inconnu"}) {
            mvc.perform(post("/api/user/auth").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + name + "\",\"password\":\"123456$\"}"))
                .andExpect(status().isUnauthorized());
        }
    }
    @Test void refuseUnTokenInvalideOuExpire() throws Exception {
        for (String jwt : new String[]{"faux-token", token("USER", Instant.now().minusSeconds(300))}) {
            mvc.perform(get("/api/livre").header("Authorization", "Bearer " + jwt))
                .andExpect(status().isUnauthorized());
        }
    }
    @Test void exigeConnexionPourLeCatalogue() throws Exception {
        mvc.perform(get("/api/livre")).andExpect(status().isUnauthorized());
    }
    @Test void autoriseUserEtAdminMaisRefuseUneAutoriteInconnue() throws Exception {
        for (String role : new String[]{"USER", "ADMIN"}) {
            String jwt = token(role, Instant.now().plusSeconds(3600));
            mvc.perform(get("/api/livre").header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
            // Passe le contrôle d'accès puis échoue à la validation métier (titre absent).
            mvc.perform(post("/api/livre").header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/livre").header("Authorization", "Bearer " + token("GUEST", Instant.now().plusSeconds(3600))))
            .andExpect(status().isForbidden());
    }

    @Test void valideLaLimiteBcryptEnOctetsPourLesAccents() throws Exception {
        for (int length : new int[]{36, 37}) {
            var request = mvc.perform(post("/api/user/subscribe").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"accent-" + length + "\",\"password\":\"" + "é".repeat(length) + "\"}"));
            request.andExpect(length == 36 ? status().isOk() : status().isBadRequest());
        }
    }

}
