package de.htw_berlin.bookmarks_backend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {

    private Long id;
    private String title;
    private String url;
    private String description;
}
