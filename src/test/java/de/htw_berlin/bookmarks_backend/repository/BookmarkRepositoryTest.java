package de.htw_berlin.bookmarks_backend.repository;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integrationstests für BookmarkRepository mit echter PostgreSQL via Testcontainers.
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class BookmarkRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("bookmarkit_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled",      () -> "false");
        registry.add("spring.main.allow-bean-definition-overriding", () -> "true");
    }

    @BeforeAll
    static void runMigrations() {
        Flyway.configure()
            .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
            .locations("classpath:db/migration")
            .load()
            .migrate();
    }

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @BeforeEach
    void setUp() {
        bookmarkRepository.deleteAll();
    }

    @Test
    void findByOwnerId_gibtNurBookmarksDesUsers_zurueck() {
        bookmarkRepository.saveAll(List.of(
            createBookmark("HTW Berlin", "https://htw-berlin.de", "auth0|user1"),
            createBookmark("GitHub",     "https://github.com",    "auth0|user2")
        ));

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
    void findByIdAndOwnerId_gibtLeer_wennFremderUser() {
        Bookmark saved = bookmarkRepository.save(
            createBookmark("HTW Berlin", "https://htw-berlin.de", "auth0|user1")
        );

        Optional<Bookmark> result = bookmarkRepository.findByIdAndOwnerId(saved.getId(), "auth0|fremder");

        assertThat(result).isEmpty();
    }

    @Test
    void findByOwnerIdAndFavoritTrue_gibtNurFavoriten() {
        bookmarkRepository.saveAll(List.of(
            createBookmarkFavorit("Favorit",      "https://a.de", "auth0|user1", true),
            createBookmarkFavorit("Kein Favorit", "https://b.de", "auth0|user1", false)
        ));

        List<Bookmark> result = bookmarkRepository.findByOwnerIdAndFavoritTrue("auth0|user1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Favorit");
    }

    @Test
    void existsByIdAndOwnerId_gibtTrue_wennUserStimmt() {
        Bookmark saved = bookmarkRepository.save(
            createBookmark("HTW Berlin", "https://htw-berlin.de", "auth0|user1")
        );

        boolean exists = bookmarkRepository.existsByIdAndOwnerId(saved.getId(), "auth0|user1");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByIdAndOwnerId_gibtFalse_wennFremderUser() {
        Bookmark saved = bookmarkRepository.save(
            createBookmark("HTW Berlin", "https://htw-berlin.de", "auth0|user1")
        );

        boolean exists = bookmarkRepository.existsByIdAndOwnerId(saved.getId(), "auth0|fremder");

        assertThat(exists).isFalse();
    }

    private Bookmark createBookmark(String title, String url, String ownerId) {
        Bookmark b = new Bookmark();
        b.setTitle(title);
        b.setUrl(url);
        b.setOwnerId(ownerId);
        b.setFavorit(false);
        b.setGelesen(false);
        return b;
    }

    private Bookmark createBookmarkFavorit(String title, String url, String ownerId, boolean favorit) {
        Bookmark b = createBookmark(title, url, ownerId);
        b.setFavorit(favorit);
        return b;
    }
}
