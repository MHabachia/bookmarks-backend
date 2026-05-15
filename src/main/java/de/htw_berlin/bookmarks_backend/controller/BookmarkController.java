package de.htw_berlin.bookmarks_backend;

import de.htw_berlin.bookmarks_backend.Bookmark;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BookmarkController {

    // Beispiel-Daten für M1 (noch keine Datenbank)
    private final List<Bookmark> sampleBookmarks = List.of(
        new Bookmark(1L, "HTW Berlin", "https://www.htw-berlin.de", "Hochschule für Technik und Wirtschaft Berlin"),
        new Bookmark(2L, "Spring Boot Docs", "https://docs.spring.io/spring-boot", "Offizielle Spring Boot Dokumentation"),
        new Bookmark(3L, "Vue.js Docs", "https://vuejs.org", "Offizielle Vue.js 3 Dokumentation"),
        new Bookmark(4L, "MDN Web Docs", "https://developer.mozilla.org", "Web-Entwicklungs-Referenz von Mozilla")
    );

    @GetMapping(value = "/bookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Bookmark> getBookmarks() {
        return sampleBookmarks;
    }
}
