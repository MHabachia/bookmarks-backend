package de.htw_berlin.bookmarks_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA-Entity für einen Lesezeichen-Eintrag (Bookmark).
 *
 * <p>Repräsentiert einen Bookmark in der Datenbank-Tabelle {@code bookmarks}.
 * Tags werden in einer separaten Tabelle {@code bookmark_tags} gespeichert
 * und werden beim Laden gefetcht.</p>
 *
 * <p>Jeder Bookmark gehört genau einem User — identifiziert durch die
 * {@code ownerId} (Auth0 {@code sub} Claim).</p>
 *
 * <p>Lombok-Annotationen:</p>
 * <ul>
 *   <li>{@code @Getter} / {@code @Setter} → Getter und Setter für alle Felder</li>
 *   <li>{@code @NoArgsConstructor} → Standardkonstruktor (für JPA erforderlich)</li>
 *   <li>{@code @AllArgsConstructor} → Konstruktor mit allen Feldern</li>
 *   <li>{@code @EqualsAndHashCode(onlyExplicitlyIncluded = true)} →
 *       equals/hashCode nur auf {@code id} — sicher für JPA/Hibernate</li>
 * </ul>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.1
 * @since SoSe 2026
 * @see de.htw_berlin.bookmarks_backend.repository.BookmarkRepository
 * @see de.htw_berlin.bookmarks_backend.service.BookmarkService
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "bookmarks")
public class Bookmark {

    /**
     * Eindeutiger Primärschlüssel, automatisch generiert von der Datenbank.
     * Wird für equals/hashCode verwendet ({@code @EqualsAndHashCode.Include}).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Titel des Bookmarks.
     * Pflichtfeld, darf nicht leer sein und maximal 255 Zeichen lang.
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
     * Gespeichert in Tabelle {@code bookmark_tags}, EAGER gefetcht.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bookmark_tags", joinColumns = @JoinColumn(name = "bookmark_id"))
    @Column(name = "tag")
    private List<String> tags;

    /**
     * Auth0 User-ID des Besitzers dieses Bookmarks ({@code sub} Claim).
     * Wird beim Erstellen automatisch gesetzt und kann nicht verändert werden.
     */
    @Column(name = "owner_id", nullable = false, updatable = false)
    private String ownerId;

    /**
     * Zeitstempel der Erstellung. Automatisch gesetzt, nicht veränderbar.
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * JPA Lifecycle-Callback: setzt {@link #createdAt} beim ersten Speichern.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
