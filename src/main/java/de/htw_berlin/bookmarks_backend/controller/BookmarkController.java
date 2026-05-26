package de.htw_berlin.bookmarks_backend.controller;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import de.htw_berlin.bookmarks_backend.service.BookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;


    @GetMapping(value = "/bookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Bookmark> getBookmarks() {
        return bookmarkService.getAllBookmarks();
    }


    @GetMapping(value = "/bookmarks/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Bookmark> getBookmarkById(@PathVariable Long id) {
        return bookmarkService.getBookmarkById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping(value = "/bookmarks",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Bookmark> createBookmark(@Valid @RequestBody Bookmark bookmark) {
        Bookmark saved = bookmarkService.createBookmark(bookmark);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    @PutMapping(value = "/bookmarks/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Bookmark> updateBookmark(
            @PathVariable Long id,
            @Valid @RequestBody Bookmark bookmark) {
        return bookmarkService.updateBookmark(id, bookmark)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/bookmarks/{id}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long id) {
        if (bookmarkService.deleteBookmark(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
