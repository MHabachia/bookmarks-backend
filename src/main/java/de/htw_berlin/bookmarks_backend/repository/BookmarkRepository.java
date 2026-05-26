package de.htw_berlin.bookmarks_backend.repository;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {


    List<Bookmark> findByFavoritTrue();


    List<Bookmark> findByGelesenTrue();


    List<Bookmark> findByTagsContaining(String tag);
}
