package de.htw_berlin.bookmarks_backend;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Manuelle Flyway-Konfiguration für Spring Boot
 *
 * <p>Diese Klasse konfiguriert Flyway explizit als Spring Bean und stellt sicher,
 * dass Datenbankmigrationen beim Start der Anwendung automatisch ausgeführt werden.</p>
 *
 * <p>Beim ersten Start auf einer leeren Datenbank führt Flyway automatisch alle
 * SQL-Migrationsskripte aus {@code classpath:db/migration} aus und erstellt
 * dabei die Tabellen {@code bookmarks} und {@code bookmark_tags}.</p>
 *
 * <p>Bei jedem weiteren Start prüft Flyway ob neue Migrationsskripte vorhanden sind
 * und führt nur noch nicht ausgeführte Versionen aus.</p>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 1.4
 */
@Configuration
public class FlywayConfig {

    /**
     * JDBC-URL der PostgreSQL-Datenbank aus {@code application.properties}.
     */
    @Value("${spring.datasource.url}")
    private String url;

    /**
     * Datenbankbenutzername aus {@code application.properties}.
     */
    @Value("${spring.datasource.username}")
    private String user;

    /**
     * Datenbankpasswort aus {@code application.properties}.
     */
    @Value("${spring.datasource.password}")
    private String password;

    /**
     * Erstellt und konfiguriert die Flyway-Instanz als Spring Bean.
     *
     * <p>Die Methode {@code migrate()} wird durch {@code initMethod = "migrate"}
     * automatisch nach der Bean-Initialisierung aufgerufen und führt alle
     * ausstehenden Migrationen aus.</p>
     *
     * <p>Konfiguration:</p>
     * <ul>
     *   <li>{@code baselineOnMigrate(true)} — ermöglicht Migration auf bestehenden Datenbanken</li>
     *   <li>{@code baselineVersion("0")} — setzt die Baseline-Version auf 0</li>
     *   <li>{@code locations("classpath:db/migration")} — Ordner mit SQL-Migrationsskripten</li>
     * </ul>
     *
     * @return konfigurierte und bereite Flyway-Instanz
     */
    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return Flyway.configure()
                .dataSource(url, user, password)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load();
    }
}