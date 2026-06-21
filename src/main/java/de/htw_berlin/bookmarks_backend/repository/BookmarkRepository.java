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
 * User ausschließlich seine eigenen Bookmarks. Die User-Isolation wird
 * vollständig auf Datenbankebene durchgesetzt.</p>
 *
 * <p>Spring Data JPA generiert automatisch SQL-Queries aus den Methodennamen.
 * Beispiel: {@code findByOwnerId} wird zu:</p>
 * <pre>
 * SELECT * FROM bookmarks WHERE owner_id = ?
 * </pre>
 *
 * <p>Von {@link JpaRepository} werden folgende Standard-Methoden geerbt:</p>
 * <ul>
 *   <li>{@code save(Bookmark)} — Speichern/Aktualisieren</li>
 *   <li>{@code deleteById(Long)} — Löschen nach ID</li>
 *   <li>{@code findAll()} — Alle Einträge (nicht verwendet, da keine User-Isolation)</li>
 * </ul>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.0
 * @since SoSe 2026
 * @see Bookmark
 * @see BookmarkService
 */
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * Gibt alle Bookmarks eines bestimmten Users zurück.
     *
     * <p>Generiertes SQL:</p>
     * <pre>
     * SELECT * FROM bookmarks WHERE owner_id = ?
     * </pre>
     *
     * @param ownerId die Auth0 User-ID (sub Claim), z.B. {@code "auth0|abc123"}
     * @return Liste der Bookmarks dieses Users, leer wenn keine vorhanden
     */
    List<Bookmark> findByOwnerId(String ownerId);

    /**
     * Gibt einen einzelnen Bookmark zurück — nur wenn er dem angegebenen User gehört.
     *
     * <p>Generiertes SQL:</p>
     * <pre>
     * SELECT * FROM bookmarks WHERE id = ? AND owner_id = ?
     * </pre>
     *
     * @param id      die Datenbank-ID des gesuchten Bookmarks
     * @param ownerId die Auth0 User-ID des anfragenden Users
     * @return der Bookmark als {@link Optional}, oder {@link Optional#empty()}
     *         wenn die ID nicht existiert oder einem anderen User gehört
     */
    Optional<Bookmark> findByIdAndOwnerId(Long id, String ownerId);

    /**
     * Gibt alle als Favorit markierten Bookmarks eines Users zurück.
     *
     * <p>Generiertes SQL:</p>
     * <pre>
     * SELECT * FROM bookmarks WHERE owner_id = ? AND favorit = true
     * </pre>
     *
     * @param ownerId die Auth0 User-ID des anfragenden Users
     * @return Liste der Favoriten-Bookmarks, leer wenn keine vorhanden
     */
    List<Bookmark> findByOwnerIdAndFavoritTrue(String ownerId);

    /**
     * Gibt alle als gelesen markierten Bookmarks eines Users zurück.
     *
     * <p>Generiertes SQL:</p>
     * <pre>
     * SELECT * FROM bookmarks WHERE owner_id = ? AND gelesen = true
     * </pre>
     *
     * @param ownerId die Auth0 User-ID des anfragenden Users
     * @return Liste der gelesenen Bookmarks, leer wenn keine vorhanden
     */
    List<Bookmark> findByOwnerIdAndGelesenTrue(String ownerId);

    /**
     * Prüft ob ein Bookmark mit der angegebenen ID dem angegebenen User gehört.
     *
     * <p>Generiertes SQL:</p>
     * <pre>
     * SELECT COUNT(*) > 0 FROM bookmarks WHERE id = ? AND owner_id = ?
     * </pre>
     *
     * <p>Wird in {@link BookmarkService#deleteBookmark} verwendet um vor dem
     * Löschen zu prüfen ob der User der Besitzer ist.</p>
     *
     * @param id      die Datenbank-ID des Bookmarks
     * @param ownerId die Auth0 User-ID des anfragenden Users
     * @return {@code true} wenn der Bookmark existiert und dem User gehört,
     *         {@code false} sonst
     */
    boolean existsByIdAndOwnerId(Long id, String ownerId);
}
