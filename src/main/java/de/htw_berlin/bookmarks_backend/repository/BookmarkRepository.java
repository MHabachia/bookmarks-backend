package de.htw_berlin.bookmarks_backend.repository;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository für {@link Bookmark}-Entitäten.
 *
 * <p>Durch das Erweitern von {@link JpaRepository} werden automatisch alle
 * Standard-CRUD-Operationen bereitgestellt:</p>
 * <ul>
 *   <li>{@code findAll()} — alle Bookmarks laden</li>
 *   <li>{@code findById(Long id)} — einzelnen Bookmark anhand der ID laden</li>
 *   <li>{@code save(Bookmark bookmark)} — Bookmark speichern oder aktualisieren</li>
 *   <li>{@code existsById(Long id)} — prüfen ob ein Bookmark existiert</li>
 *   <li>{@code deleteById(Long id)} — Bookmark löschen</li>
 * </ul>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 1.4
 */
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * Findet alle Bookmarks die als Favorit markiert sind.
     *
     * <p>Spring Data JPA generiert automatisch folgende SQL-Query:</p>
     * <pre>SELECT * FROM bookmarks WHERE favorit = true</pre>
     *
     * @return Liste der Bookmarks mit {@code favorit = true},
     *         leere Liste wenn keine vorhanden
     */
    List<Bookmark> findByFavoritTrue();

    /**
     * Findet alle Bookmarks die als gelesen markiert sind.
     *
     * <p>Spring Data JPA generiert automatisch folgende SQL-Query:</p>
     * <pre>SELECT * FROM bookmarks WHERE gelesen = true</pre>
     *
     * @return Liste der Bookmarks mit {@code gelesen = true},
     *         leere Liste wenn keine vorhanden
     */
    List<Bookmark> findByGelesenTrue();

    /**
     * Findet alle Bookmarks die einen bestimmten Tag enthalten.
     *
     * <p>Spring Data JPA generiert automatisch einen JOIN auf die
     * Tabelle {@code bookmark_tags}.</p>
     *
     * @param tag der gesuchte Tag (Groß-/Kleinschreibung wird beachtet)
     * @return Liste der Bookmarks die den angegebenen Tag enthalten,
     *         leere Liste wenn keine vorhanden
     */
    List<Bookmark> findByTagsContaining(String tag);
}