package fr.bibliotheque.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
// Activer les annotations @PreAuthorize / @PostAuthorize
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    // Une clé propre au processus, jamais la clé d'exemple partagée dans Git.
    // Les sessions expirent aussi au redémarrage du serveur.
    private final SecretKey signingKey = new SecretKeySpec(
        java.security.SecureRandom.getSeed(32), "HmacSHA256");

    private SecretKey secretKey() { return signingKey; }

    // Ce JwtEncoder permettra de générer des jetons JWT, signés avec la clé privée
    @Bean
    JwtEncoder jwtEncoder() {
        SecretKey secretKey = this.secretKey();

        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    // Ce JwtDecoder permettra de valider les jetons reçus dans les requêtes HTTP, utilisé par le Filter qui se chargera de valider le jeton
    @Bean
    JwtDecoder jwtDecoder() {
        SecretKey secretKey = this.secretKey();

        return NimbusJwtDecoder
            .withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256) // HmacSHA256
            .build()
        ;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) {
        // Les API utilisent exclusivement le JWT, sans session ni HTTP Basic.
        http.securityMatcher("/api/**");
        http.cors(Customizer.withDefaults());
        http.sessionManagement(session -> session.sessionCreationPolicy(
            org.springframework.security.config.http.SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers(HttpMethod.POST, "/api/user/subscribe", "/api/user/auth").permitAll();
            // Choix du groupe : tous les CRUD pour USER et ADMIN connectés.
            authorize.anyRequest().hasAnyAuthority("USER", "ADMIN");
        });
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
            jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        http.exceptionHandling(errors -> errors.authenticationEntryPoint(
            (request, response, exception) -> response.sendError(401)));
        // Pas de cookie d'authentification sur cette chaîne API stateless.
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter granted = new JwtGrantedAuthoritiesConverter();
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        // Par défaut, le préfix est "SCOPE_"
        granted.setAuthorityPrefix("");

        converter.setJwtGrantedAuthoritiesConverter(granted);

        return converter;
    }

    @Bean
    org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        var config = new org.springframework.web.cors.CorsConfiguration();
        config.setAllowedOrigins(java.util.List.of("http://localhost:4200", "http://127.0.0.1:4200"));
        config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "Accept"));
        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {

        // return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }

    // On intègre l'AuthenticationManager dans le contexte de Spring pour pouvoir l'utiliser directement
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }
}
