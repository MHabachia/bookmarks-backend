package de.htw_berlin.bookmarks_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookmarks")
public class Bookmark {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "Titel darf nicht leer sein")
    @Size(max = 255, message = "Titel darf maximal 255 Zeichen haben")
    @Column(nullable = false)
    private String title;


    @NotBlank(message = "URL darf nicht leer sein")
    @Size(max = 500, message = "URL darf maximal 500 Zeichen haben")
    @Column(nullable = false)
    private String url;


    @Column(columnDefinition = "TEXT")
    private String description;


    @Column(nullable = false)
    private Boolean gelesen = false;


    @Column(nullable = false)
    private Boolean favorit = false;


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bookmark_tags", joinColumns = @JoinColumn(name = "bookmark_id"))
    @Column(name = "tag")
    private List<String> tags;


    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
