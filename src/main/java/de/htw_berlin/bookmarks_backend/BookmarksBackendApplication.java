package de.htw_berlin.bookmarks_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookmarksBackendApplication {

	/**
	 * Startmethode der Java-Anwendung.
	 *
	 * <p>Wird von der JVM (Java Virtual Machine) als erster Einstiegspunkt
	 * aufgerufen. Delegiert den Start an {@link SpringApplication#run},
	 * welches den gesamten Spring-Kontext hochfährt.</p>
	 *
	 * @param args optionale Kommandozeilen-Argumente, z.B.
	 *             {@code --server.port=9090} um den Port zu überschreiben
	 */
	public static void main(String[] args) {
		SpringApplication.run(BookmarksBackendApplication.class, args);
	}
}
