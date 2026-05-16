package de.htw_berlin.bookmarks_backend.controller;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * REST-Controller für die Verwaltung von Bookmarks.
 *
 * <p>Dieser Controller stellt die öffentliche REST-API der BookmarkIt-Anwendung
 * bereit. Alle Endpunkte sind unter dem Basispfad {@code /api} erreichbar.</p>
 *
 * <p>CORS ist für alle Origins aktiviert ({@code *}), damit das Vue.js-Frontend
 * auf einem anderen Port (z.B. {@code localhost:5173}) die API aufrufen kann.</p>
 *
 * <p>Verfügbare Endpunkte:</p>
 * <ul>
 *   <li>{@code GET /api/bookmarks} — Liste aller Bookmarks</li>
 * </ul>
 *
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BookmarkController {

    /**
     * Statische Beispieldaten.
     * Die Liste ist unveränderlich ({@code List.of}) — es können
     * keine Elemente hinzugefügt oder entfernt werden.</p>
     */
    private final List<Bookmark> sampleBookmarks = List.of(
            new Bookmark(1L, "HTW Berlin",
                    "https://www.htw-berlin.de",
                    "Hochschule für Technik und Wirtschaft Berlin"),
            new Bookmark(2L, "Spring Boot Docs",
                    "https://docs.spring.io/spring-boot",
                    "Offizielle Spring Boot Dokumentation"),
            new Bookmark(3L, "Facebook",
                    "https://facebook.com",
                    "Facebook von Meta"),
            new Bookmark(4L, "Youtube",
                    "https://youtube.com",
                    "Youtube vom Meta")
    );

    /**
     * Gibt eine Liste aller gespeicherten Bookmarks zurück.
     *
     * <p>Der Endpunkt ist öffentlich erreichbar und erfordert keine
     * Authentifizierung. Die Antwort wird von Spring
     * automatisch in JSON umgewandelt.</p>
     *
     * <p>HTTP-Request:</p>
     * <pre>
     * GET /api/bookmarks
     * </pre>
     *
     * <p>HTTP-Response (200 OK):</p>
     * <pre>
     * [
     *   {
     *     "id": 1,
     *     "title": "HTW Berlin",
     *     "url": "https://www.htw-berlin.de",
     *     "description": "Hochschule für Technik und Wirtschaft Berlin"
     *   },
     *   ...
     * ]
     * </pre>
     *
     * @return eine unveränderliche {@link List} aller {@link Bookmark}-Objekte
     */
    @GetMapping(value = "/bookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Bookmark> getBookmarks() {
        return sampleBookmarks;
    }
}
