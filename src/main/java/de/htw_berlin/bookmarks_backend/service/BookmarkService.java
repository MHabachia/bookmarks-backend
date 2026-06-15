package de.htw_berlin.bookmarks_backend.service;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import de.htw_berlin.bookmarks_backend.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service-Klasse für die Bookmark-Verwaltung.
 *
 * <p>Änderungen gegenüber v1.4: Alle Methoden erhalten {@code ownerId}
 * als Parameter — Datenzugriff ist auf den jeweiligen User beschränkt.</p>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    /**
     * Alle Bookmarks eines Users.
     *
     * @param ownerId Auth0 sub-Claim des eingeloggten Users
     */
    public List<Bookmark> getAllBookmarks(String ownerId) {
        return bookmarkRepository.findByOwnerId(ownerId);
    }

    /**
     * Einzelner Bookmark — nur wenn er dem User gehört.
     * Gibt Optional.empty() zurück wenn die ID nicht existiert oder einem anderen User gehört.
     */
    public Optional<Bookmark> getBookmarkById(Long id, String ownerId) {
        return bookmarkRepository.findByIdAndOwnerId(id, ownerId);
    }

    /**
     * Erstellt einen neuen Bookmark und setzt die ownerId automatisch aus dem Token.
     * Das Frontend muss die ownerId nicht mitsenden.
     */
    public Bookmark createBookmark(Bookmark bookmark, String ownerId) {
        bookmark.setOwnerId(ownerId);
        return bookmarkRepository.save(bookmark);
    }

    /**
     * Aktualisiert einen Bookmark — nur wenn er dem User gehört.
     * Die ownerId wird nicht verändert (updatable = false in der Entity).
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
     * @return true wenn gelöscht, false wenn nicht gefunden oder falscher User
     */
    public boolean deleteBookmark(Long id, String ownerId) {
        if (bookmarkRepository.existsByIdAndOwnerId(id, ownerId)) {
            bookmarkRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
