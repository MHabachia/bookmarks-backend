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
 * JPA-Entity für einen Lesezeichen-Eintrag (Bookmark).
 *
 * <p>Repräsentiert einen Bookmark in der Datenbank-Tabelle {@code bookmarks}.
 * Tags werden in einer separaten Tabelle {@code bookmark_tags} gespeichert
 * und werden beim Laden eager gefetcht.</p>
 *
 * <p>Jeder Bookmark gehört genau einem User — identifiziert durch die
 * {@code ownerId} (Auth0 {@code sub} Claim). Beim Zugriff über die API
 * werden immer nur die Bookmarks des eingeloggten Users zurückgegeben.</p>
 *
 * <p>Lombok-Annotationen generieren automatisch:</p>
 * <ul>
 *   <li>{@code @Data} → Getter, Setter, equals(), hashCode(), toString()</li>
 *   <li>{@code @NoArgsConstructor} → Standardkonstruktor (für JPA erforderlich)</li>
 *   <li>{@code @AllArgsConstructor} → Konstruktor mit allen Feldern</li>
 * </ul>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.0
 * @since SoSe 2026
 * @see BookmarkRepository
 * @see BookmarkService
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookmarks")
public class Bookmark {

    /**
     * Eindeutiger Primärschlüssel, automatisch generiert von der Datenbank.
     * Entspricht der Spalte {@code id} (BIGSERIAL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Titel des Bookmarks.
     * Pflichtfeld, darf nicht leer sein und maximal 255 Zeichen lang.
     *
     * @see NotBlank
     * @see Size
     */
    @NotBlank(message = "Titel darf nicht leer sein")
    @Size(max = 255, message = "Titel darf maximal 255 Zeichen haben")
    @Column(nullable = false)
    private String title;

    /**
     * URL des Bookmarks inklusive Protokoll (z.B. {@code https://}).
     * Pflichtfeld, darf nicht leer sein und maximal 500 Zeichen lang.
     */
    @NotBlank(message = "URL darf nicht leer sein")
    @Size(max = 500, message = "URL darf maximal 500 Zeichen haben")
    @Column(nullable = false)
    private String url;

    /**
     * Optionale Beschreibung des Bookmarks.
     * Gespeichert als TEXT in der Datenbank (unbegrenzte Länge).
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Gibt an ob der Bookmark als gelesen markiert wurde.
     * Standardwert: {@code false}.
     */
    @Column(nullable = false)
    private Boolean gelesen = false;

    /**
     * Gibt an ob der Bookmark als Favorit markiert wurde.
     * Standardwert: {@code false}.
     */
    @Column(nullable = false)
    private Boolean favorit = false;

    /**
     * Liste der Tags/Kategorien des Bookmarks.
     *
     * <p>Wird in der separaten Tabelle {@code bookmark_tags} gespeichert.
     * Beim Löschen eines Bookmarks werden zugehörige Tags automatisch
     * gelöscht (ON DELETE CASCADE in V1-Migration).</p>
     *
     * <p>Fetch-Typ EAGER bedeutet: Tags werden immer zusammen mit dem
     * Bookmark geladen, kein separater SQL-Query nötig.</p>
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bookmark_tags", joinColumns = @JoinColumn(name = "bookmark_id"))
    @Column(name = "tag")
    private List<String> tags;

    /**
     * Auth0 User-ID des Besitzers dieses Bookmarks.
     *
     * <p>Entspricht dem {@code sub} Claim des JWT-Tokens.
     * Mögliche Formate:</p>
     * <ul>
     *   <li>{@code auth0|abc123} — Auth0 Username/Password</li>
     *   <li>{@code google-oauth2|abc123} — Google Social Login</li>
     * </ul>
     *
     * <p>Wird beim Erstellen automatisch aus dem JWT-Token gesetzt
     * ({@link BookmarkService#createBookmark}) und kann danach nicht
     * mehr verändert werden ({@code updatable = false}).</p>
     */
    @Column(name = "owner_id", nullable = false, updatable = false)
    private String ownerId;

    /**
     * Zeitstempel der Erstellung dieses Bookmarks.
     * Wird automatisch beim ersten Speichern gesetzt ({@link #onCreate()}).
     * Kann danach nicht mehr verändert werden ({@code updatable = false}).
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * JPA Lifecycle-Callback: wird automatisch vor dem ersten Speichern aufgerufen.
     * Setzt {@link #createdAt} auf den aktuellen Zeitstempel.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
