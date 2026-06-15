package de.htw_berlin.bookmarks_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA-Entity für einen Bookmark.
 *
 * <p>Änderungen gegenüber v1.4:</p>
 * <ul>
 *   <li>{@code ownerId} — Auth0 User-ID ({@code sub} Claim), z.B. {@code "auth0|abc123"}</li>
 *   <li>Jeder Bookmark gehört genau einem User — andere User sehen ihn nicht</li>
 * </ul>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.0
 */
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

    /**
     * Auth0 User-ID aus dem JWT {@code sub} Claim.
     * Format: {@code "auth0|abc123"} oder {@code "google-oauth2|abc123"}
     *
     * <p>Wird beim Erstellen automatisch aus dem Token gesetzt
     * und danach nicht mehr verändert ({@code updatable = false}).</p>
     */
    @Column(name = "owner_id", nullable = false, updatable = false)
    private String ownerId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
