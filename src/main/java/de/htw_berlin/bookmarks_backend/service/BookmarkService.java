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
 * <p>Bildet die mittlere Schicht der 3-Schichten-Architektur:</p>
 * <pre>
 * BookmarkController → BookmarkService → BookmarkRepository
 * </pre>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.1
 * @since SoSe 2026
 */
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    /**
     * Gibt alle Bookmarks des Users zurück.
     *
     * @param ownerId Auth0 User-ID
     * @return Liste der Bookmarks, leer wenn keine vorhanden
     */
    public List<Bookmark> getAllBookmarks(String ownerId) {
        return bookmarkRepository.findByOwnerId(ownerId);
    }

    /**
     * Gibt einen einzelnen Bookmark zurück — nur wenn er dem User gehört.
     *
     * @param id      Datenbank-ID
     * @param ownerId Auth0 User-ID
     * @return Bookmark oder {@link Optional#empty()}
     */
    public Optional<Bookmark> getBookmarkById(Long id, String ownerId) {
        return bookmarkRepository.findByIdAndOwnerId(id, ownerId);
    }

    /**
     * Erstellt einen neuen Bookmark. Die {@code ownerId} wird aus dem Token gesetzt.
     *
     * @param bookmark neues Bookmark-Objekt
     * @param ownerId  Auth0 User-ID
     * @return gespeicherter Bookmark mit generierter ID
     */
    public Bookmark createBookmark(Bookmark bookmark, String ownerId) {
        bookmark.setOwnerId(ownerId);
        return bookmarkRepository.save(bookmark);
    }

    /**
     * Aktualisiert einen Bookmark — nur wenn er dem User gehört.
     *
     * @param id      Datenbank-ID
     * @param updated Bookmark mit neuen Werten
     * @param ownerId Auth0 User-ID
     * @return aktualisierter Bookmark oder {@link Optional#empty()}
     */
    public Optional<Bookmark> updateBookmark(Long id, Bookmark updated, String ownerId) {
        return bookmarkRepository.findByIdAndOwnerId(id, ownerId).map(existing -> {
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
     * Löscht einen Bookmark — nur wenn er dem User gehört.
     *
     * <p>Optimierung: nur 1 DB-Query statt 2 (kein extra {@code exists} Check).</p>
     *
     * @param id      Datenbank-ID
     * @param ownerId Auth0 User-ID
     * @return {@code true} wenn gelöscht, {@code false} wenn nicht gefunden
     */
    public boolean deleteBookmark(Long id, String ownerId) {
        return bookmarkRepository.findByIdAndOwnerId(id, ownerId)
            .map(bookmark -> {
                bookmarkRepository.delete(bookmark);
                return true;
            })
            .orElse(false);
    }
}
