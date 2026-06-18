package de.htw_berlin.bookmarks_backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.mockito.Mockito.mock;

/**
 * Test-Sicherheitskonfiguration.
 *
 * Ersetzt den echten JwtDecoder (der Auth0 aufrufen würde)
 * durch einen Mockito-Mock — Spring Boot kann so starten
 * ohne eine echte Auth0-Verbindung aufzubauen.
 *
 * Wird über @Import in Integrationstests eingebunden.
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return mock(JwtDecoder.class);
    }
}
