package de.htw_berlin.bookmarks_backend.repository;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository für Bookmarks.
 *
 * <p>Alle Queries filtern nach {@code ownerId} —
 * jeder User sieht nur seine eigenen Bookmarks.</p>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.0
 */
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /** Alle Bookmarks eines bestimmten Users. */
    List<Bookmark> findByOwnerId(String ownerId);

    /** Einzelner Bookmark — nur wenn er dem User gehört. */
    Optional<Bookmark> findByIdAndOwnerId(Long id, String ownerId);

    /** Favoriten eines Users. */
    List<Bookmark> findByOwnerIdAndFavoritTrue(String ownerId);

    /** Gelesene Bookmarks eines Users. */
    List<Bookmark> findByOwnerIdAndGelesenTrue(String ownerId);

    /** Prüft ob ein Bookmark existiert und dem User gehört. */
    boolean existsByIdAndOwnerId(Long id, String ownerId);
}
