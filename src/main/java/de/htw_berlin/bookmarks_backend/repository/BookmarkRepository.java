package de.htw_berlin.bookmarks_backend.repository;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository für den Datenbankzugriff auf {@link Bookmark}-Entitäten.
 *
 * <p>Alle Query-Methoden filtern nach {@code ownerId} — dadurch sieht jeder
 * User ausschließlich seine eigenen Bookmarks.</p>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.1
 * @since SoSe 2026
 */
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * Gibt alle Bookmarks eines bestimmten Users zurück.
     *
     * @param ownerId die Auth0 User-ID
     * @return Liste der Bookmarks, leer wenn keine vorhanden
     */
    List<Bookmark> findByOwnerId(String ownerId);

    /**
     * Gibt einen einzelnen Bookmark zurück — nur wenn er dem User gehört.
     *
     * @param id      die Datenbank-ID des Bookmarks
     * @param ownerId die Auth0 User-ID
     * @return der Bookmark oder {@link Optional#empty()}
     */
    Optional<Bookmark> findByIdAndOwnerId(Long id, String ownerId);

    /**
     * Gibt alle Favoriten eines Users zurück.
     *
     * @param ownerId die Auth0 User-ID
     * @return Liste der Favoriten-Bookmarks
     */
    List<Bookmark> findByOwnerIdAndFavoritTrue(String ownerId);

    /**
     * Prüft ob ein Bookmark dem User gehört.
     *
     * @param id      die Datenbank-ID des Bookmarks
     * @param ownerId die Auth0 User-ID
     * @return {@code true} wenn vorhanden und dem User gehörend
     */
    boolean existsByIdAndOwnerId(Long id, String ownerId);
}
