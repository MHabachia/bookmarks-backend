package de.htw_berlin.bookmarks_backend.service;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import de.htw_berlin.bookmarks_backend.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;


    public List<Bookmark> getAllBookmarks() {
        return bookmarkRepository.findAll();
    }


    public Optional<Bookmark> getBookmarkById(Long id) {
        return bookmarkRepository.findById(id);
    }


    public Bookmark createBookmark(Bookmark bookmark) {
        return bookmarkRepository.save(bookmark);
    }


    public Optional<Bookmark> updateBookmark(Long id, Bookmark updated) {
        return bookmarkRepository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setUrl(updated.getUrl());
            existing.setDescription(updated.getDescription());
            existing.setTags(updated.getTags());
            existing.setGelesen(updated.getGelesen());
            existing.setFavorit(updated.getFavorit());
            return bookmarkRepository.save(existing);
        });
    }


    public boolean deleteBookmark(Long id) {
        if (bookmarkRepository.existsById(id)) {
            bookmarkRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
