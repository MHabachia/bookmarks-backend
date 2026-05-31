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
 * <p>Stellt folgende HTTP-Endpunkte bereit:</p>
 * <ul>
 *   <li>{@code GET    /api/bookmarks}      — alle Bookmarks laden</li>
 *   <li>{@code GET    /api/bookmarks/{id}} — einzelnen Bookmark laden</li>
 *   <li>{@code POST   /api/bookmarks}      — neuen Bookmark erstellen</li>
 *   <li>{@code PUT    /api/bookmarks/{id}} — Bookmark aktualisieren</li>
 *   <li>{@code DELETE /api/bookmarks/{id}} — Bookmark löschen</li>
 * </ul>
 *
 * <p>{@code @CrossOrigin(origins = "*")} erlaubt Anfragen von allen Origins —
 * notwendig damit das Vue.js Frontend (Port 5173) das Backend (Port 8080)
 * im "lokalen" Entwicklungsmodus erreichen kann.</p>
 *
 * <p>{@code @RequiredArgsConstructor} generiert den Konstruktor für
 * Constructor Injection des {@link BookmarkService}.</p>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 1.4
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BookmarkController {

    /** Service-Schicht für die Geschäftslogik. */
    private final BookmarkService bookmarkService;

    /**
     * Gibt alle Bookmarks zurück.
     *
     * <p>Beispiel-Request:</p>
     * <pre>GET /api/bookmarks</pre>
     *
     * <p>Beispiel-Response:</p>
     * <pre>
     * HTTP 200 OK
     * [
     *   {
     *     "id": 1,
     *     "title": "HTW Berlin",
     *     "url": "https://www.htw-berlin.de",
     *     "description": "...",
     *     "gelesen": false,
     *     "favorit": false,
     *     "tags": ["Studium"],
     *     "createdAt": "2026-05-26T12:00:00"
     *   }
     * ]
     * </pre>
     *
     * @return Liste aller Bookmarks als JSON, leere Liste {@code []} wenn keine vorhanden
     */
    @GetMapping(value = "/bookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Bookmark> getBookmarks() {
        return bookmarkService.getAllBookmarks();
    }

    /**
     * Gibt einen einzelnen Bookmark anhand der ID zurück.
     *
     * <p>Beispiel-Request:</p>
     * <pre>GET /api/bookmarks/1</pre>
     *
     * @param id die ID des gesuchten Bookmarks (Pfadvariable)
     * @return {@code 200 OK} mit Bookmark-JSON wenn gefunden,
     *         {@code 404 Not Found} wenn die ID nicht existiert
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
     * <p>{@code @Valid} aktiviert die Bean-Validierung — {@code @NotBlank} auf
     * {@code title} und {@code url} in der {@link Bookmark}-Entity wird geprüft.
     * Bei Validierungsfehler wird automatisch {@code 400 Bad Request} zurückgegeben.</p>
     *
     * <p>Beispiel-Request:</p>
     * <pre>
     * POST /api/bookmarks
     * Content-Type: application/json
     *
     * {
     *   "title": "HTW Berlin",
     *   "url": "https://www.htw-berlin.de",
     *   "description": "Hochschule Berlin",
     *   "tags": ["Studium", "HTW"]
     * }
     * </pre>
     *
     * @param bookmark das neue Bookmark aus dem Request-Body (JSON)
     * @return {@code 201 Created} mit dem gespeicherten Bookmark inkl. generierter ID
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
     * <p>Wird auch für den Favorit/Gelesen-Toggle genutzt — das Frontend
     * sendet das vollständige Bookmark-Objekt mit dem geänderten Status.</p>
     *
     * <p>Beispiel-Request:</p>
     * <pre>
     * PUT /api/bookmarks/1
     * Content-Type: application/json
     *
     * {
     *   "id": 1,
     *   "title": "HTW Berlin",
     *   "url": "https://www.htw-berlin.de",
     *   "favorit": true,
     *   "gelesen": false,
     *   "tags": ["Studium"]
     * }
     * </pre>
     *
     * @param id       die ID des zu aktualisierenden Bookmarks (Pfadvariable)
     * @param bookmark die neuen Daten aus dem Request-Body (JSON)
     * @return {@code 200 OK} mit aktualisiertem Bookmark,
     *         {@code 404 Not Found} wenn die ID nicht existiert
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
     * <p>Beim Löschen werden auch alle zugehörigen Tags automatisch
     * mitgelöscht ({@code ON DELETE CASCADE} in der Datenbank).</p>
     *
     * <p>Beispiel-Request:</p>
     * <pre>DELETE /api/bookmarks/1</pre>
     *
     * @param id die ID des zu löschenden Bookmarks (Pfadvariable)
     * @return {@code 204 No Content} wenn erfolgreich gelöscht,
     *         {@code 404 Not Found} wenn die ID nicht existiert
     */
    @DeleteMapping("/bookmarks/{id}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long id) {
        if (bookmarkService.deleteBookmark(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}