package de.htw_berlin.bookmarks_backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Ersetzt SecurityConfig vollständig für Integrationstests.
 * Alle Requests werden erlaubt — kein JWT-Decoder nötig.
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
