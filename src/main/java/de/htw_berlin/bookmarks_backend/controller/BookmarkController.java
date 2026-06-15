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
 * <p>Änderungen gegenüber v1.4:</p>
 * <ul>
 *   <li>{@code @CrossOrigin} entfernt — CORS wird jetzt zentral in {@code SecurityConfig} geregelt</li>
 *   <li>Alle Methoden erhalten den {@link JwtAuthenticationToken} Parameter —
 *       damit kann die Auth0-User-ID ({@code sub} Claim) ausgelesen werden</li>
 *   <li>Bookmarks werden nur noch für den eingeloggten User geladen/erstellt</li>
 * </ul>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.0
 */
@RestController
@RequestMapping("/api")
// @CrossOrigin ENTFERNT — wird jetzt in SecurityConfig.corsConfigurationSource() geregelt
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * Gibt alle Bookmarks des eingeloggten Users zurück.
     *
     * <p>{@code token.getName()} gibt den {@code sub}-Claim des JWT zurück,
     * z.B. {@code "auth0|abc123"} — das ist die eindeutige Auth0 User-ID.</p>
     *
     * @param token der JWT-Token des eingeloggten Users (von Spring Security injiziert)
     * @return Liste der Bookmarks dieses Users
     */
    @GetMapping(value = "/bookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Bookmark> getBookmarks(JwtAuthenticationToken token) {
        String ownerId = token.getName(); // Auth0 sub: "auth0|abc123"
        return bookmarkService.getAllBookmarks(ownerId);
    }

    /**
     * Gibt einen einzelnen Bookmark des eingeloggten Users zurück.
     * Gibt 404 zurück wenn der Bookmark einem anderen User gehört.
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
     * Die {@code ownerId} wird aus dem Token gesetzt — das Frontend muss sie nicht mitsenden.
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
