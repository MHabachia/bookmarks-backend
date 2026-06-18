package de.htw_berlin.bookmarks_backend.controller;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import de.htw_berlin.bookmarks_backend.service.BookmarkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit-Tests für BookmarkController — nur Mockito, keine Spring-Infrastruktur.
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 */
@ExtendWith(MockitoExtension.class)
class BookmarkControllerTest {

    @Mock
    private BookmarkService bookmarkService;

    @InjectMocks
    private BookmarkController bookmarkController;

    private Bookmark testBookmark;
    private JwtAuthenticationToken mockToken;

    @BeforeEach
    void setUp() {
        testBookmark = new Bookmark();
        testBookmark.setId(1L);
        testBookmark.setTitle("HTW Berlin");
        testBookmark.setUrl("https://www.htw-berlin.de");
        testBookmark.setGelesen(false);
        testBookmark.setFavorit(false);
        testBookmark.setOwnerId("auth0|test123");

        Jwt jwt = Jwt.withTokenValue("mock-token")
            .header("alg", "RS256")
            .claim("sub", "auth0|test123")
            .build();
        mockToken = new JwtAuthenticationToken(jwt);
    }

    @Test
    void getBookmarks_gibtListeZurueck() {
        when(bookmarkService.getAllBookmarks("auth0|test123"))
            .thenReturn(List.of(testBookmark));

        List<Bookmark> result = bookmarkController.getBookmarks(mockToken);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("HTW Berlin");
    }

    @Test
    void getBookmarkById_gibt200_wennGefunden() {
        when(bookmarkService.getBookmarkById(1L, "auth0|test123"))
            .thenReturn(Optional.of(testBookmark));

        ResponseEntity<Bookmark> response = bookmarkController.getBookmarkById(1L, mockToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("HTW Berlin");
    }

    @Test
    void getBookmarkById_gibt404_wennNichtGefunden() {
        when(bookmarkService.getBookmarkById(99L, "auth0|test123"))
            .thenReturn(Optional.empty());

        ResponseEntity<Bookmark> response = bookmarkController.getBookmarkById(99L, mockToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createBookmark_gibt201_mitGespeichertemBookmark() {
        when(bookmarkService.createBookmark(any(Bookmark.class), eq("auth0|test123")))
            .thenReturn(testBookmark);

        ResponseEntity<Bookmark> response = bookmarkController.createBookmark(testBookmark, mockToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getTitle()).isEqualTo("HTW Berlin");
    }

    @Test
    void deleteBookmark_gibt204_wennErfolgreich() {
        when(bookmarkService.deleteBookmark(1L, "auth0|test123")).thenReturn(true);

        ResponseEntity<Void> response = bookmarkController.deleteBookmark(1L, mockToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteBookmark_gibt404_wennNichtGefunden() {
        when(bookmarkService.deleteBookmark(99L, "auth0|test123")).thenReturn(false);

        ResponseEntity<Void> response = bookmarkController.deleteBookmark(99L, mockToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
