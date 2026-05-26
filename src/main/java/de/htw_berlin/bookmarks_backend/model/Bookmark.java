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
 * JPA-Entity für einen Bookmark in der BookmarkIt-Anwendung.
 *
 * <p>Repräsentiert einen Datensatz in der Tabelle {@code bookmarks}
 * in der PostgreSQL-Datenbank. Die Tabelle wird durch Flyway-Migration
 * {@code V1__create_bookmarks.sql} erstellt.</p>
 *
 * <p>Lombok generiert automatisch Getter, Setter, toString(),
 * equals(), hashCode() und beide Konstruktoren.</p>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.0
 * @since SoSe 2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookmarks")
public class Bookmark {

    /**
     * Primärschlüssel — wird von PostgreSQL automatisch generiert (BIGSERIAL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Titel des Bookmarks. Pflichtfeld, max. 255 Zeichen.
     */
    @NotBlank(message = "Titel darf nicht leer sein")
    @Size(max = 255, message = "Titel darf maximal 255 Zeichen haben")
    @Column(nullable = false)
    private String title;

    /**
     * Vollständige URL inkl. Protokoll. Pflichtfeld, max. 500 Zeichen.
     */
    @NotBlank(message = "URL darf nicht leer sein")
    @Size(max = 500, message = "URL darf maximal 500 Zeichen haben")
    @Column(nullable = false)
    private String url;

    /**
     * Optionale Beschreibung des Bookmarks.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Gibt an ob der Bookmark als gelesen markiert wurde.
     * Standard: false
     */
    @Column(nullable = false)
    private Boolean gelesen = false;

    /**
     * Gibt an ob der Bookmark als Favorit markiert wurde.
     * Standard: false
     */
    @Column(nullable = false)
    private Boolean favorit = false;

    /**
     * Tags / Kategorien des Bookmarks.
     * Wird als separate Tabelle (bookmarks_tags) gespeichert.
     */
    @ElementCollection
    @CollectionTable(name = "bookmark_tags", joinColumns = @JoinColumn(name = "bookmark_id"))
    @Column(name = "tag")
    private List<String> tags;

    /**
     * Erstellungszeitpunkt — wird automatisch beim Erstellen gesetzt.
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Setzt createdAt automatisch vor dem ersten Speichern.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
