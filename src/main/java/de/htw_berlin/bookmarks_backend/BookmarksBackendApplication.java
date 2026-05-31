package de.htw_berlin.bookmarks_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Einstiegspunkt der BookmarkIt Spring Boot Anwendung.
 *
 * <p>Diese Klasse startet den gesamten Spring-Kontext inkl. Webserver (Tomcat),
 * Datenbankverbindung (JPA/Hibernate), Flyway-Migration und alle REST-Controller.</p>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 1.4
 */
@SpringBootApplication
public class BookmarksBackendApplication {

	/**
	 * Startmethode der Java-Anwendung.
	 *

	 */
	public static void main(String[] args) {
		SpringApplication.run(BookmarksBackendApplication.class, args);
	}
}