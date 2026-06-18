package de.htw_berlin.bookmarks_backend.service;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import de.htw_berlin.bookmarks_backend.repository.BookmarkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests für BookmarkService.
 *
 * Verwendet Mockito um das Repository zu mocken —
 * keine Datenbankverbindung nötig.
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 */
@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @InjectMocks
    private BookmarkService bookmarkService;

    private Bookmark testBookmark;

    @BeforeEach
    void setUp() {
        testBookmark = new Bookmark();
        testBookmark.setId(1L);
        testBookmark.setTitle("HTW Berlin");
        testBookmark.setUrl("https://www.htw-berlin.de");
        testBookmark.setDescription("Meine Hochschule");
        testBookmark.setGelesen(false);
        testBookmark.setFavorit(false);
        testBookmark.setOwnerId("auth0|test123");
    }

    @Test
    void getAllBookmarks_gibtNurBookmarksDesUsers_zurueck() {
        when(bookmarkRepository.findByOwnerId("auth0|test123"))
            .thenReturn(List.of(testBookmark));

        List<Bookmark> result = bookmarkService.getAllBookmarks("auth0|test123");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("HTW Berlin");
    }

    @Test
    void createBookmark_setztOwnerId_korrekt() {
        Bookmark neu = new Bookmark();
        neu.setTitle("GitHub");
        neu.setUrl("https://github.com");

        when(bookmarkRepository.save(any(Bookmark.class))).thenAnswer(i -> i.getArgument(0));

        Bookmark result = bookmarkService.createBookmark(neu, "auth0|test123");

        assertThat(result.getOwnerId()).isEqualTo("auth0|test123");
    }

    @Test
    void getBookmarkById_gibtBookmarkZurueck_wennEsExistiert() {
        when(bookmarkRepository.findByIdAndOwnerId(1L, "auth0|test123"))
            .thenReturn(Optional.of(testBookmark));

        Optional<Bookmark> result = bookmarkService.getBookmarkById(1L, "auth0|test123");

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("HTW Berlin");
    }

    @Test
    void getBookmarkById_gibtLeerZurueck_wennNichtGefunden() {
        when(bookmarkRepository.findByIdAndOwnerId(99L, "auth0|test123"))
            .thenReturn(Optional.empty());

        Optional<Bookmark> result = bookmarkService.getBookmarkById(99L, "auth0|test123");

        assertThat(result).isEmpty();
    }

    @Test
    void updateBookmark_aktualisiertFelder_korrekt() {
        Bookmark aktualisiert = new Bookmark();
        aktualisiert.setTitle("HTW Berlin - Aktualisiert");
        aktualisiert.setUrl("https://www.htw-berlin.de");
        aktualisiert.setFavorit(true);
        aktualisiert.setGelesen(true);

        when(bookmarkRepository.findByIdAndOwnerId(1L, "auth0|test123"))
            .thenReturn(Optional.of(testBookmark));
        when(bookmarkRepository.save(any(Bookmark.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Bookmark> result = bookmarkService.updateBookmark(1L, aktualisiert, "auth0|test123");

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("HTW Berlin - Aktualisiert");
        assertThat(result.get().getFavorit()).isTrue();
        assertThat(result.get().getGelesen()).isTrue();
    }

    @Test
    void deleteBookmark_gibtTrue_wennErfolgreich() {
        when(bookmarkRepository.existsByIdAndOwnerId(1L, "auth0|test123")).thenReturn(true);
        doNothing().when(bookmarkRepository).deleteById(1L);

        boolean result = bookmarkService.deleteBookmark(1L, "auth0|test123");

        assertThat(result).isTrue();
        verify(bookmarkRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBookmark_gibtFalse_wennNichtGefunden() {
        when(bookmarkRepository.existsByIdAndOwnerId(99L, "auth0|test123")).thenReturn(false);

        boolean result = bookmarkService.deleteBookmark(99L, "auth0|test123");

        assertThat(result).isFalse();
        verify(bookmarkRepository, never()).deleteById(any());
    }
}
