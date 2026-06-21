package de.htw_berlin.bookmarks_backend.controller;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import de.htw_berlin.bookmarks_backend.service.BookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller für die Bookmark-API.
 *
 * <p>Stellt alle CRUD-Endpunkte unter dem Basispfad {@code /api} bereit.
 * Alle Endpunkte erfordern einen gültigen JWT-Bearer-Token im
 * {@code Authorization} Header (gesichert durch {@code SecurityConfig}).</p>
 *
 * <p>Die Auth0 User-ID wird aus dem JWT-Token extrahiert ({@code sub} Claim)
 * und an den {@link BookmarkService} weitergegeben — so sieht jeder User
 * nur seine eigenen Bookmarks.</p>
 *
 * <p>CORS wird zentral in {@code SecurityConfig.corsConfigurationSource()} geregelt,
 * nicht mehr per {@code @CrossOrigin} Annotation.</p>
 *
 * <p>Verfügbare Endpunkte:</p>
 * <ul>
 *   <li>{@code GET /api/bookmarks} — Alle Bookmarks des Users</li>
 *   <li>{@code GET /api/bookmarks/{id}} — Einzelner Bookmark</li>
 *   <li>{@code POST /api/bookmarks} — Neuen Bookmark erstellen</li>
 *   <li>{@code PUT /api/bookmarks/{id}} — Bookmark aktualisieren</li>
 *   <li>{@code DELETE /api/bookmarks/{id}} — Bookmark löschen</li>
 * </ul>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.0
 * @since SoSe 2026
 * @see BookmarkService
 * @see de.htw_berlin.bookmarks_backend.config.SecurityConfig
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookmarkController {

    /**
     * Service für die Geschäftslogik der Bookmark-Verwaltung.
     * Wird per Constructor Injection von Spring injiziert.
     */
    private final BookmarkService bookmarkService;

    /**
     * Gibt alle Bookmarks des eingeloggten Users zurück.
     *
     * <p>{@code token.getName()} liefert den {@code sub}-Claim des JWT-Tokens,
     * z.B. {@code "auth0|abc123"} — die eindeutige Auth0 User-ID.</p>
     *
     * <p>HTTP: {@code GET /api/bookmarks}</p>
     *
     * @param token der JWT-Token des eingeloggten Users,
     *              wird von Spring Security automatisch injiziert
     * @return Liste aller Bookmarks dieses Users als JSON,
     *         leer wenn keine Bookmarks vorhanden ({@code 200 OK})
     */
    @GetMapping(value = "/bookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Bookmark> getBookmarks(JwtAuthenticationToken token) {
        String ownerId = token.getName();
        return bookmarkService.getAllBookmarks(ownerId);
    }

    /**
     * Gibt einen einzelnen Bookmark des eingeloggten Users zurück.
     *
     * <p>Gibt {@code 404 Not Found} zurück wenn:</p>
     * <ul>
     *   <li>Die ID nicht in der Datenbank existiert</li>
     *   <li>Die ID existiert, aber einem anderen User gehört</li>
     * </ul>
     *
     * <p>HTTP: {@code GET /api/bookmarks/{id}}</p>
     *
     * @param id    die Datenbank-ID des gesuchten Bookmarks (Pfadvariable)
     * @param token der JWT-Token des eingeloggten Users
     * @return {@code 200 OK} mit dem Bookmark als JSON,
     *         oder {@code 404 Not Found} wenn nicht gefunden
     */
    @GetMapping(value = "/bookmarks/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Bookmark> getBookmarkById(
            @PathVariable Long id,
            JwtAuthenticationToken token) {
        String ownerId = token.getName();
        return bookmarkService.getBookmarkById(id, ownerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Erstellt einen neuen Bookmark für den eingeloggten User.
     *
     * <p>Die {@code ownerId} wird serverseitig aus dem JWT-Token gesetzt —
     * das Frontend muss sie nicht mitsenden. Felder werden per Bean Validation
     * ({@code @Valid}) geprüft: {@code title} und {@code url} sind Pflichtfelder.</p>
     *
     * <p>HTTP: {@code POST /api/bookmarks}</p>
     *
     * <p>Beispiel Request-Body:</p>
     * <pre>
     * {
     *   "title": "HTW Berlin",
     *   "url": "https://www.htw-berlin.de",
     *   "description": "Meine Hochschule",
     *   "tags": ["Studium", "HTW"]
     * }
     * </pre>
     *
     * @param bookmark das neue Bookmark-Objekt aus dem Request-Body (JSON)
     * @param token    der JWT-Token des eingeloggten Users
     * @return {@code 201 Created} mit dem gespeicherten Bookmark (inkl. generierter ID)
     */
    @PostMapping(value = "/bookmarks",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Bookmark> createBookmark(
            @Valid @RequestBody Bookmark bookmark,
            JwtAuthenticationToken token) {
        String ownerId = token.getName();
        Bookmark saved = bookmarkService.createBookmark(bookmark, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Aktualisiert einen Bookmark — nur wenn er dem eingeloggten User gehört.
     *
     * <p>Wird auch für das Togglen von Favorit- und Gelesen-Status verwendet,
     * da diese Felder Teil des Bookmark-Objekts sind.</p>
     *
     * <p>HTTP: {@code PUT /api/bookmarks/{id}}</p>
     *
     * @param id       die Datenbank-ID des zu aktualisierenden Bookmarks (Pfadvariable)
     * @param bookmark das Bookmark-Objekt mit den neuen Werten (Request-Body, JSON)
     * @param token    der JWT-Token des eingeloggten Users
     * @return {@code 200 OK} mit dem aktualisierten Bookmark,
     *         oder {@code 404 Not Found} wenn nicht gefunden oder falscher User
     */
    @PutMapping(value = "/bookmarks/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Bookmark> updateBookmark(
            @PathVariable Long id,
            @Valid @RequestBody Bookmark bookmark,
            JwtAuthenticationToken token) {
        String ownerId = token.getName();
        return bookmarkService.updateBookmark(id, bookmark, ownerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Löscht einen Bookmark — nur wenn er dem eingeloggten User gehört.
     *
     * <p>HTTP: {@code DELETE /api/bookmarks/{id}}</p>
     *
     * @param id    die Datenbank-ID des zu löschenden Bookmarks (Pfadvariable)
     * @param token der JWT-Token des eingeloggten Users
     * @return {@code 204 No Content} wenn erfolgreich gelöscht,
     *         oder {@code 404 Not Found} wenn nicht gefunden oder falscher User
     */
    @DeleteMapping("/bookmarks/{id}")
    public ResponseEntity<Void> deleteBookmark(
            @PathVariable Long id,
            JwtAuthenticationToken token) {
        String ownerId = token.getName();
        if (bookmarkService.deleteBookmark(id, ownerId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
