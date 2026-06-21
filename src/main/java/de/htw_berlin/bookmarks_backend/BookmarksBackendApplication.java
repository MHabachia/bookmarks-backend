package de.htw_berlin.bookmarks_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Einstiegspunkt der BookmarkIt Spring Boot Anwendung.
 *
 * <p>Diese Klasse startet den gesamten Spring-Kontext inklusive:</p>
 * <ul>
 *   <li>Eingebetteter Webserver (Apache Tomcat auf Port 8080)</li>
 *   <li>Datenbankverbindung via JPA/Hibernate</li>
 *   <li>Automatische Datenbank-Migration via Flyway</li>
 *   <li>Spring Security mit JWT-Authentifizierung via Auth0</li>
 *   <li>Alle REST-Controller unter {@code /api}</li>
 * </ul>
 *
 * <p>Die Anwendung ist vollständig stateless — kein Session-Management.
 * Alle Anfragen werden über JWT-Bearer-Tokens authentifiziert.</p>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.0
 * @since SoSe 2026
 * @see <a href="https://github.com/MHabachia/bookmarks-backend">GitHub Repository</a>
 */
@SpringBootApplication
public class BookmarksBackendApplication {

    /**
     * Startet die Spring Boot Anwendung.
     *
     * <p>Beim Start werden folgende Schritte automatisch ausgeführt:</p>
     * <ol>
     *   <li>Spring Application Context wird initialisiert</li>
     *   <li>Flyway führt ausstehende Datenbank-Migrationen aus (V1, V2)</li>
     *   <li>JPA/Hibernate verbindet sich mit der PostgreSQL-Datenbank</li>
     *   <li>Tomcat startet auf Port 8080 (oder {@code PORT} Umgebungsvariable)</li>
     * </ol>
     *
     * @param args Kommandozeilenargumente, z.B. {@code --spring.profiles.active=local}
     */
    public static void main(String[] args) {
        SpringApplication.run(BookmarksBackendApplication.class, args);
    }
}
