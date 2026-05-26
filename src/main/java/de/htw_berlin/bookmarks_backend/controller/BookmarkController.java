package de.htw_berlin.bookmarks_backend.controller;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import de.htw_berlin.bookmarks_backend.service.BookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller für die Bookmark-API.
 *
 * <p>Stellt folgende Endpunkte bereit:</p>
 * <ul>
 *   <li>GET    /api/bookmarks       — alle Bookmarks laden</li>
 *   <li>GET    /api/bookmarks/{id}  — einzelnen Bookmark laden</li>
 *   <li>POST   /api/bookmarks       — neuen Bookmark erstellen</li>
 *   <li>PUT    /api/bookmarks/{id}  — Bookmark aktualisieren</li>
 *   <li>DELETE /api/bookmarks/{id}  — Bookmark löschen</li>
 * </ul>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.0
 * @since SoSe 2026
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * Gibt alle Bookmarks zurück.
     *
     * @return Liste aller Bookmarks als JSON
     */
    @GetMapping(value = "/bookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Bookmark> getBookmarks() {
        return bookmarkService.getAllBookmarks();
    }

    /**
     * Gibt einen einzelnen Bookmark anhand der ID zurück.
     *
     * @param id die ID des gesuchten Bookmarks
     * @return 200 OK mit Bookmark oder 404 Not Found
     */
    @GetMapping(value = "/bookmarks/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Bookmark> getBookmarkById(@PathVariable Long id) {
        return bookmarkService.getBookmarkById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Erstellt einen neuen Bookmark.
     *
     * <p>{@code @Valid} aktiviert die Bean-Validierung —
     * {@code @NotBlank} auf title und url wird geprüft.</p>
     *
     * @param bookmark das neue Bookmark aus dem Request-Body (JSON)
     * @return 201 Created mit dem gespeicherten Bookmark
     */
    @PostMapping(value = "/bookmarks",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Bookmark> createBookmark(@Valid @RequestBody Bookmark bookmark) {
        Bookmark saved = bookmarkService.createBookmark(bookmark);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Aktualisiert einen bestehenden Bookmark.
     *
     * @param id       die ID des zu aktualisierenden Bookmarks
     * @param bookmark die neuen Daten aus dem Request-Body (JSON)
     * @return 200 OK mit aktualisiertem Bookmark oder 404 Not Found
     */
    @PutMapping(value = "/bookmarks/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Bookmark> updateBookmark(
            @PathVariable Long id,
            @Valid @RequestBody Bookmark bookmark) {
        return bookmarkService.updateBookmark(id, bookmark)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Löscht einen Bookmark anhand der ID.
     *
     * @param id die ID des zu löschenden Bookmarks
     * @return 204 No Content wenn erfolgreich oder 404 Not Found
     */
    @DeleteMapping("/bookmarks/{id}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long id) {
        if (bookmarkService.deleteBookmark(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
