package de.htw_berlin.bookmarks_backend.repository;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integrations-Tests für BookmarkRepository.
 *
 * Testcontainers startet automatisch einen echten PostgreSQL-Container.
 * Flyway läuft durch — V1 und V2 Migrationen werden ausgeführt.
 * Alle SQL-Queries werden gegen eine echte Datenbank getestet.
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookmarkRepositoryTest {

    /**
     * Testcontainers startet PostgreSQL 16 automatisch.
     * @ServiceConnection verbindet Spring Boot automatisch mit dem Container.
     */
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @BeforeEach
    void setUp() {
        bookmarkRepository.deleteAll();
    }

    @Test
    void findByOwnerId_gibtNurBookmarksDesUsers_zurueck() {
        // Zwei User mit je einem Bookmark
        Bookmark b1 = createBookmark("HTW Berlin", "https://htw-berlin.de", "auth0|user1");
        Bookmark b2 = createBookmark("GitHub", "https://github.com", "auth0|user2");
        bookmarkRepository.saveAll(List.of(b1, b2));

        List<Bookmark> result = bookmarkRepository.findByOwnerId("auth0|user1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("HTW Berlin");
    }

    @Test
    void findByIdAndOwnerId_gibtBookmark_wennUserStimmt() {
        Bookmark saved = bookmarkRepository.save(
            createBookmark("HTW Berlin", "https://htw-berlin.de", "auth0|user1")
        );

        Optional<Bookmark> result = bookmarkRepository.findByIdAndOwnerId(saved.getId(), "auth0|user1");

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("HTW Berlin");
    }

    @Test
    void findByIdAndOwnerId_gibtLeer_wennUserNichtStimmt() {
        Bookmark saved = bookmarkRepository.save(
            createBookmark("HTW Berlin", "https://htw-berlin.de", "auth0|user1")
        );

        Optional<Bookmark> result = bookmarkRepository.findByIdAndOwnerId(saved.getId(), "auth0|anderer-user");

        assertThat(result).isEmpty();
    }

    @Test
    void findByOwnerIdAndFavoritTrue_gibtNurFavoriten() {
        bookmarkRepository.saveAll(List.of(
            createBookmarkMitFavorit("Favorit", "https://a.de", "auth0|user1", true),
            createBookmarkMitFavorit("Kein Favorit", "https://b.de", "auth0|user1", false)
        ));

        List<Bookmark> result = bookmarkRepository.findByOwnerIdAndFavoritTrue("auth0|user1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Favorit");
    }

    @Test
    void findByOwnerIdAndGelesenTrue_gibtNurGelesene() {
        bookmarkRepository.saveAll(List.of(
            createBookmarkMitGelesen("Gelesen", "https://a.de", "auth0|user1", true),
            createBookmarkMitGelesen("Ungelesen", "https://b.de", "auth0|user1", false)
        ));

        List<Bookmark> result = bookmarkRepository.findByOwnerIdAndGelesenTrue("auth0|user1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Gelesen");
    }

    @Test
    void existsByIdAndOwnerId_gibtTrue_wennBeideStimmen() {
        Bookmark saved = bookmarkRepository.save(
            createBookmark("HTW Berlin", "https://htw-berlin.de", "auth0|user1")
        );

        boolean exists = bookmarkRepository.existsByIdAndOwnerId(saved.getId(), "auth0|user1");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByIdAndOwnerId_gibtFalse_wennUserNichtStimmt() {
        Bookmark saved = bookmarkRepository.save(
            createBookmark("HTW Berlin", "https://htw-berlin.de", "auth0|user1")
        );

        boolean exists = bookmarkRepository.existsByIdAndOwnerId(saved.getId(), "auth0|anderer-user");

        assertThat(exists).isFalse();
    }

    // ── Hilfsmethoden ──────────────────────────────────────────────────────────

    private Bookmark createBookmark(String title, String url, String ownerId) {
        Bookmark b = new Bookmark();
        b.setTitle(title);
        b.setUrl(url);
        b.setOwnerId(ownerId);
        b.setFavorit(false);
        b.setGelesen(false);
        return b;
    }

    private Bookmark createBookmarkMitFavorit(String title, String url, String ownerId, boolean favorit) {
        Bookmark b = createBookmark(title, url, ownerId);
        b.setFavorit(favorit);
        return b;
    }

    private Bookmark createBookmarkMitGelesen(String title, String url, String ownerId, boolean gelesen) {
        Bookmark b = createBookmark(title, url, ownerId);
        b.setGelesen(gelesen);
        return b;
    }
}
