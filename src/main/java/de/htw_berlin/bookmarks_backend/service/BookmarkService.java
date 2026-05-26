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
 * <p>Liegt zwischen Controller und Repository — der Controller ruft
 * den Service auf, der Service ruft das Repository auf.
 * So bleibt die Geschäftslogik vom HTTP-Layer getrennt.</p>
 *
 * <p>{@code @RequiredArgsConstructor} von Lombok generiert automatisch
 * einen Konstruktor für alle {@code final} Felder — das ist der
 * empfohlene Weg für Dependency Injection in Spring.</p>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 1.0
 * @since SoSe 2026
 */
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    /**
     * Gibt alle Bookmarks zurück.
     *
     * @return Liste aller Bookmarks aus der Datenbank
     */
    public List<Bookmark> getAllBookmarks() {
        return bookmarkRepository.findAll();
    }

    /**
     * Gibt einen einzelnen Bookmark anhand der ID zurück.
     *
     * @param id die ID des gesuchten Bookmarks
     * @return Optional mit dem Bookmark oder leer wenn nicht gefunden
     */
    public Optional<Bookmark> getBookmarkById(Long id) {
        return bookmarkRepository.findById(id);
    }

    /**
     * Erstellt einen neuen Bookmark und speichert ihn in der Datenbank.
     *
     * @param bookmark das neue Bookmark-Objekt
     * @return das gespeicherte Bookmark mit generierter ID
     */
    public Bookmark createBookmark(Bookmark bookmark) {
        return bookmarkRepository.save(bookmark);
    }

    /**
     * Aktualisiert einen bestehenden Bookmark.
     *
     * <p>Sucht den Bookmark anhand der ID und aktualisiert
     * alle Felder mit den neuen Werten.</p>
     *
     * @param id       die ID des zu aktualisierenden Bookmarks
     * @param updated  das Objekt mit den neuen Werten
     * @return Optional mit dem aktualisierten Bookmark oder leer wenn nicht gefunden
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
     * Löscht einen Bookmark anhand der ID.
     *
     * @param id die ID des zu löschenden Bookmarks
     * @return true wenn erfolgreich gelöscht, false wenn nicht gefunden
     */
    public boolean deleteBookmark(Long id) {
        if (bookmarkRepository.existsById(id)) {
            bookmarkRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
