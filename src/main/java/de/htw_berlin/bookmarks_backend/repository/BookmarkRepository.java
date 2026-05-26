package de.htw_berlin.bookmarks_backend.repository;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository für {@link Bookmark}-Entitäten.
 *
 * <p>Durch das Erweitern von {@link JpaRepository} werden automatisch
 * alle CRUD-Operationen bereitgestellt — ohne eine einzige SQL-Zeile
 * schreiben zu müssen:</p>
 * <ul>
 *   <li>{@code findAll()} — alle Bookmarks laden</li>
 *   <li>{@code findById(id)} — einzelnen Bookmark laden</li>
 *   <li>{@code save(bookmark)} — Bookmark speichern oder aktualisieren</li>
 *   <li>{@code deleteById(id)} — Bookmark löschen</li>
 * </ul>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 1.0
 * @since SoSe 2026
 */
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * Findet alle Bookmarks die als Favorit markiert sind.
     *
     * @return Liste der Favoriten-Bookmarks
     */
    List<Bookmark> findByFavoritTrue();

    /**
     * Findet alle Bookmarks die als gelesen markiert sind.
     *
     * @return Liste der gelesenen Bookmarks
     */
    List<Bookmark> findByGelesenTrue();

    /**
     * Findet alle Bookmarks die einen bestimmten Tag enthalten.
     *
     * @param tag der gesuchte Tag
     * @return Liste der Bookmarks mit diesem Tag
     */
    List<Bookmark> findByTagsContaining(String tag);
}
