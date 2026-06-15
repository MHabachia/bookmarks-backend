package de.htw_berlin.bookmarks_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security Konfiguration für JWT-Validierung via Auth0.
 *
 * <p>Was diese Klasse tut:</p>
 * <ul>
 *   <li>Alle {@code /api/**} Endpunkte erfordern ein gültiges JWT-Token</li>
 *   <li>Das Token wird automatisch gegen Auth0 validiert (Signatur + Ablaufzeit)</li>
 *   <li>CORS erlaubt Anfragen vom Vue.js Frontend</li>
 *   <li>CSRF ist deaktiviert — bei reinen REST-APIs mit JWT nicht nötig</li>
 *   <li>Session-Management ist stateless — kein serverseitiger Session-State</li>
 * </ul>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * URL des Vue.js Frontends — wird aus der Umgebungsvariable {@code FRONTEND_URL} gelesen.
     * Auf Render setzen: z.B. {@code https://bookmarkit-frontend.onrender.com}
     * Lokal: {@code http://localhost:5173}
     */
    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    /**
     * Konfiguriert die Security-Filter-Chain.
     *
     * <p>Ablauf bei einem eingehenden Request:</p>
     * <pre>
     * Request → CORS-Check → JWT-Validierung → Controller
     * </pre>
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS-Konfiguration aus corsConfigurationSource() verwenden
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // CSRF deaktivieren — nicht nötig bei stateless JWT-Auth
            .csrf(csrf -> csrf.disable())

            // Kein serverseitiger Session-State — jeder Request trägt sein Token selbst
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Alle /api/** Routen erfordern Authentifizierung
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )

            // JWT-Validierung: Spring Security ruft automatisch den JWKS-Endpoint
            // von Auth0 ab und prüft damit die Token-Signatur
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * CORS-Konfiguration: erlaubt Anfragen vom Vue.js Frontend.
     *
     * <p>Ersetzt {@code @CrossOrigin(origins = "*")} im Controller —
     * hier wird die Origin auf die konkrete Frontend-URL eingeschränkt.</p>
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Nur das eigene Frontend darf Anfragen stellen
        config.setAllowedOrigins(List.of(frontendUrl));

        // Alle nötigen HTTP-Methoden erlauben
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Authorization-Header muss erlaubt sein (trägt den JWT-Token)
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
