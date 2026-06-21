package de.htw_berlin.bookmarks_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security Konfiguration für JWT-Authentifizierung via Auth0.
 *
 * <p>Diese Klasse konfiguriert die gesamte Sicherheitsinfrastruktur der Anwendung:</p>
 * <ul>
 *   <li>JWT-Validierung gegen den Auth0 JWKS-Endpoint</li>
 *   <li>CORS-Konfiguration für das Vue.js Frontend</li>
 *   <li>Stateless Session-Management (kein Server-Side State)</li>
 *   <li>Zugriffskontrolle: alle {@code /api/**} Endpunkte erfordern Authentifizierung</li>
 * </ul>
 *
 * <p>Mit {@code @Profile("!test")} wird diese Klasse im Test-Profil vollständig ignoriert.
 * Für Tests übernimmt stattdessen {@code TestSecurityConfig} mit offenen Zugriffsregeln.</p>
 *
 * <p>Ablauf der JWT-Validierung bei einem eingehenden Request:</p>
 * <pre>
 * Request mit "Authorization: Bearer &lt;JWT&gt;"
 *         ↓
 * Spring Security extrahiert den Token
 *         ↓
 * JwtDecoder prüft Signatur gegen Auth0 JWKS
 *         ↓
 * token.getName() liefert Auth0 User-ID (sub Claim)
 *         ↓
 * Controller verarbeitet Request mit ownerId
 * </pre>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.3
 * @since SoSe 2026
 * @see BookmarkController
 */
@Configuration
@EnableWebSecurity
@Profile("!test")
public class SecurityConfig {

    /**
     * Auth0 Issuer URI aus der Umgebungsvariable {@code AUTH0_ISSUER_URI}.
     * Format: {@code https://dev-XXXXX.eu.auth0.com/} (mit abschliessendem Slash).
     * Wird zur Validierung der JWT-Token-Signatur verwendet.
     */
    @Value("${auth0.issuer-uri}")
    private String issuerUri;

    /**
     * Frontend-URL aus der Umgebungsvariable {@code FRONTEND_URL}.
     * Standardwert: {@code http://localhost:5173} für lokale Entwicklung.
     * Wird für die CORS-Konfiguration verwendet.
     */
    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    /**
     * Konfiguriert die Security-Filter-Chain.
     *
     * <p>Einstellungen:</p>
     * <ul>
     *   <li>CORS: erlaubt Anfragen vom Vue.js Frontend</li>
     *   <li>CSRF: deaktiviert (nicht nötig bei stateless JWT-Auth)</li>
     *   <li>Session: STATELESS (kein serverseitiger State)</li>
     *   <li>Autorisierung: {@code /api/**} erfordert gültigen JWT-Token</li>
     *   <li>OAuth2 Resource Server: validiert JWT-Tokens automatisch</li>
     * </ul>
     *
     * @param http das {@link HttpSecurity} Konfigurationsobjekt von Spring Security
     * @return die konfigurierte {@link SecurityFilterChain}
     * @throws Exception wenn die Konfiguration fehlschlägt
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * Erstellt einen {@link JwtDecoder} zur Validierung von Auth0 JWT-Tokens.
     *
     * <p>Der Decoder lädt automatisch den JWKS (JSON Web Key Set) vom Auth0
     * Discovery-Endpoint und prüft damit die Signatur jedes eingehenden Tokens:</p>
     * <pre>
     * https://dev-XXXXX.eu.auth0.com/.well-known/openid-configuration
     *         ↓
     * jwks_uri → https://dev-XXXXX.eu.auth0.com/.well-known/jwks.json
     *         ↓
     * Öffentliche Schlüssel zur Token-Validierung
     * </pre>
     *
     * @return ein konfigurierter {@link JwtDecoder} für Auth0-Tokens
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
            .withIssuerLocation(issuerUri)
            .build();
    }

    /**
     * Konfiguriert CORS (Cross-Origin Resource Sharing) für das Vue.js Frontend.
     *
     * <p>Erlaubte Origins, Methoden und Header:</p>
     * <ul>
     *   <li>Origins: nur {@code frontendUrl} (keine Wildcard {@code *} in Produktion)</li>
     *   <li>Methoden: GET, POST, PUT, DELETE, OPTIONS</li>
     *   <li>Headers: Authorization (JWT-Token), Content-Type (JSON)</li>
     * </ul>
     *
     * <p>Gilt nur für {@code /api/**} Endpunkte.</p>
     *
     * @return die CORS-Konfigurationsquelle für Spring Security
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(frontendUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
