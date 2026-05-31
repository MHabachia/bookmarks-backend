package de.htw_berlin.bookmarks_backend.service;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import de.htw_berlin.bookmarks_backend.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service-Klasse für die Geschäftslogik der Bookmark-Verwaltung.
 *
 * <p>Liegt zwischen Controller und Repository in der Schichtenarchitektur:</p>
 * <pre>
 * HTTP-Request → BookmarkController → BookmarkService → BookmarkRepository → PostgreSQL
 * </pre>
 *
 * <p>Die Vorteile:</p>
 * <ul>
 *   <li>Geschäftslogik ist unabhängig vom HTTP-Layer testbar</li>
 *   <li>Controller bleibt schlank und delegiert an den Service</li>
 *   <li>Service kann von mehreren Controllern oder anderen Services genutzt werden</li>
 * </ul>
 *
 * <p>{@code @RequiredArgsConstructor} von Lombok generiert automatisch einen
 * Konstruktor für alle {@code final} Felder.</p>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 1.4
 */
@Service
@RequiredArgsConstructor
public class BookmarkService {

    /** Repository für den Datenbankzugriff — wird per Constructor Injection gesetzt. */
    private final BookmarkRepository bookmarkRepository;

    /**
     * Gibt alle Bookmarks aus der Datenbank zurück.
     *
     * @return Liste aller Bookmarks, leere Liste wenn keine vorhanden
     */
    public List<Bookmark> getAllBookmarks() {
        return bookmarkRepository.findAll();
    }

    /**
     * Gibt einen einzelnen Bookmark anhand der ID zurück.
     *
     * @param id die ID des gesuchten Bookmarks
     * @return {@link Optional} mit dem Bookmark wenn gefunden,
     *         {@link Optional#empty()} wenn nicht gefunden
     */
    public Optional<Bookmark> getBookmarkById(Long id) {
        return bookmarkRepository.findById(id);
    }

    /**
     * Erstellt einen neuen Bookmark und speichert ihn in der Datenbank.
     *
     * <p>Der {@code createdAt}-Zeitstempel wird automatisch durch den
     * {@code @PrePersist} Callback in {@link Bookmark#onCreate()} gesetzt.</p>
     *
     * @param bookmark das neue Bookmark-Objekt (ohne ID)
     * @return das gespeicherte Bookmark mit generierter ID und {@code createdAt}
     */
    public Bookmark createBookmark(Bookmark bookmark) {
        return bookmarkRepository.save(bookmark);
    }

    /**
     * Aktualisiert einen bestehenden Bookmark in der Datenbank.
     *
     * <p>Sucht den Bookmark anhand der ID und aktualisiert alle veränderbaren
     * Felder. Der {@code createdAt}-Zeitstempel wird nicht verändert
     * ({@code updatable = false} in der Entity).</p>
     *
     * @param id      die ID des zu aktualisierenden Bookmarks
     * @param updated das Objekt mit den neuen Werten
     * @return {@link Optional} mit dem aktualisierten Bookmark wenn gefunden,
     *         {@link Optional#empty()} wenn die ID nicht existiert
     */
    public Optional<Bookmark> updateBookmark(Long id, Bookmark updated) {
        return bookmarkRepository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setUrl(updated.getUrl());
            existing.setDescription(updated.getDescription());
            existing.setTags(updated.getTags());
            existing.setGelesen(updated.getGelesen());
            existing.setFavorit(updated.getFavorit());
            return bookmarkRepository.save(existing);
        });
    }

    /**
     * Löscht einen Bookmark anhand der ID aus der Datenbank.
     *
     * <p>Beim Löschen werden auch alle zugehörigen Tags aus der Tabelle
     * {@code bookmark_tags} automatisch gelöscht ({@code ON DELETE CASCADE}).</p>
     *
     * @param id die ID des zu löschenden Bookmarks
     * @return {@code true} wenn erfolgreich gelöscht,
     *         {@code false} wenn die ID nicht existiert
     */
    public boolean deleteBookmark(Long id) {
        if (bookmarkRepository.existsById(id)) {
            bookmarkRepository.deleteById(id);
            return true;
        }
        return false;
    }
}