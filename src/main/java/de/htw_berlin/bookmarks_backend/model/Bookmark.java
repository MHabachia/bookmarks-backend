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
 * in der PostgreSQL-Datenbank. Die zugehörige Tabelle {@code bookmark_tags}
 * speichert die Tags als separate Zeilen (One-to-Many via {@code @ElementCollection}).</p>
 *
 * <p>Lombok-Annotationen generieren automatisch:</p>
 * <ul>
 *   <li>{@code @Data} — Getter, Setter, {@code toString()}, {@code equals()}, {@code hashCode()}</li>
 *   <li>{@code @NoArgsConstructor} — Standardkonstruktor (von JPA benötigt)</li>
 *   <li>{@code @AllArgsConstructor} — Konstruktor mit allen Feldern</li>
 * </ul>
 *
 * <p>Datenbankschema:</p>
 * <pre>
 * CREATE TABLE bookmarks (
 *     id          BIGSERIAL PRIMARY KEY,
 *     title       VARCHAR(255) NOT NULL,
 *     url         VARCHAR(500) NOT NULL,
 *     description TEXT,
 *     gelesen     BOOLEAN DEFAULT FALSE,
 *     favorit     BOOLEAN DEFAULT FALSE,
 *     created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
 * );
 * </pre>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 1.4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookmarks")
public class Bookmark {

    /**
     * Primärschlüssel — wird von PostgreSQL automatisch generiert.
     * Entspricht dem Datenbankfeld {@code id}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Titel des Bookmarks. Pflichtfeld, max. 255 Zeichen.
     * Wird durch {@code @NotBlank} und {@code @Size} validiert.
     */
    @NotBlank(message = "Titel darf nicht leer sein")
    @Size(max = 255, message = "Titel darf maximal 255 Zeichen haben")
    @Column(nullable = false)
    private String title;

    /**
     * Vollständige URL des Bookmarks inkl. Protokoll (z.B. {@code https://...}).
     * Pflichtfeld, max. 500 Zeichen.
     */
    @NotBlank(message = "URL darf nicht leer sein")
    @Size(max = 500, message = "URL darf maximal 500 Zeichen haben")
    @Column(nullable = false)
    private String url;

    /**
     * Optionale Beschreibung des Bookmarks.
     * Wird als {@code TEXT} in der Datenbank gespeichert (keine Längenbegrenzung).
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Gibt an ob der Bookmark als gelesen markiert wurde.
     * Standard: {@code false}
     */
    @Column(nullable = false)
    private Boolean gelesen = false;

    /**
     * Gibt an ob der Bookmark als Favorit markiert wurde.
     * Standard: {@code false}
     */
    @Column(nullable = false)
    private Boolean favorit = false;

    /**
     * Tags / Kategorien des Bookmarks.
     *
     * <p>Wird als separate Tabelle {@code bookmark_tags} in der Datenbank gespeichert.
     * {@code FetchType.EAGER} stellt sicher dass die Tags immer zusammen mit dem
     * Bookmark geladen werden.</p>
     *
     * <p>Beim Löschen eines Bookmarks werden alle zugehörigen Tags automatisch
     * mitgelöscht ({@code ON DELETE CASCADE} in der Datenbank).</p>
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bookmark_tags", joinColumns = @JoinColumn(name = "bookmark_id"))
    @Column(name = "tag")
    private List<String> tags;

    /**
     * Erstellungszeitpunkt des Bookmarks.
     * Wird automatisch beim ersten Speichern gesetzt und danach nicht mehr verändert
     * ({@code updatable = false}).
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * JPA Lifecycle-Callback — wird automatisch vor dem ersten Speichern aufgerufen.
     * Setzt {@code createdAt} auf den aktuellen Zeitpunkt.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}