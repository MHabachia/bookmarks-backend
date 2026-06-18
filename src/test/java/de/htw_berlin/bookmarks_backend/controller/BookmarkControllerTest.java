package de.htw_berlin.bookmarks_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htw_berlin.bookmarks_backend.model.Bookmark;
import de.htw_berlin.bookmarks_backend.service.BookmarkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-Layer Tests für BookmarkController.
 *
 * Verwendet @WebMvcTest + MockMvc — kein echter Server oder DB nötig.
 * JWT-Authentifizierung wird über einen Mock-Token simuliert.
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 */
@WebMvcTest(BookmarkController.class)
class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookmarkService bookmarkService;

    @Autowired
    private ObjectMapper objectMapper;

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

        // Mock JWT-Token für Auth0
        Jwt jwt = Jwt.withTokenValue("mock-token")
            .header("alg", "RS256")
            .claim("sub", "auth0|test123")
            .build();
        mockToken = new JwtAuthenticationToken(jwt);
    }

    @Test
    void getBookmarks_gibt200_mitListe_zurueck() throws Exception {
        when(bookmarkService.getAllBookmarks("auth0|test123"))
            .thenReturn(List.of(testBookmark));

        mockMvc.perform(get("/api/bookmarks")
                .with(authentication(mockToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("HTW Berlin"))
            .andExpect(jsonPath("$[0].url").value("https://www.htw-berlin.de"));
    }

    @Test
    void getBookmarks_gibt401_ohneToken() throws Exception {
        mockMvc.perform(get("/api/bookmarks"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getBookmarkById_gibt200_wennGefunden() throws Exception {
        when(bookmarkService.getBookmarkById(1L, "auth0|test123"))
            .thenReturn(Optional.of(testBookmark));

        mockMvc.perform(get("/api/bookmarks/1")
                .with(authentication(mockToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("HTW Berlin"));
    }

    @Test
    void getBookmarkById_gibt404_wennNichtGefunden() throws Exception {
        when(bookmarkService.getBookmarkById(99L, "auth0|test123"))
            .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/bookmarks/99")
                .with(authentication(mockToken)))
            .andExpect(status().isNotFound());
    }

    @Test
    void createBookmark_gibt201_mitGespeichertemBookmark() throws Exception {
        when(bookmarkService.createBookmark(any(Bookmark.class), eq("auth0|test123")))
            .thenReturn(testBookmark);

        mockMvc.perform(post("/api/bookmarks")
                .with(authentication(mockToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookmark)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("HTW Berlin"));
    }

    @Test
    void deleteBookmark_gibt204_wennErfolgreich() throws Exception {
        when(bookmarkService.deleteBookmark(1L, "auth0|test123")).thenReturn(true);

        mockMvc.perform(delete("/api/bookmarks/1")
                .with(authentication(mockToken)))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteBookmark_gibt404_wennNichtGefunden() throws Exception {
        when(bookmarkService.deleteBookmark(99L, "auth0|test123")).thenReturn(false);

        mockMvc.perform(delete("/api/bookmarks/99")
                .with(authentication(mockToken)))
            .andExpect(status().isNotFound());
    }
}
