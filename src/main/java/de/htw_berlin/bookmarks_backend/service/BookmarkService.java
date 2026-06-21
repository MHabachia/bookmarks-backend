package de.htw_berlin.bookmarks_backend.service;

import de.htw_berlin.bookmarks_backend.model.Bookmark;
import de.htw_berlin.bookmarks_backend.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service-Klasse für die Geschäftslogik der Bookmark-Verwaltung.
 *
 * <p>Diese Klasse bildet die mittlere Schicht der 3-Schichten-Architektur:</p>
 * <pre>
 * BookmarkController (HTTP-Schicht)
 *         ↓
 * BookmarkService (Geschäftslogik-Schicht)  ← diese Klasse
 *         ↓
 * BookmarkRepository (Daten-Schicht)
 * </pre>
 *
 * <p>Alle Methoden erhalten {@code ownerId} als Parameter und leiten diesen
 * an das Repository weiter — so wird User-Isolation auf jeder Ebene durchgesetzt.
 * Kein User kann auf Bookmarks eines anderen Users zugreifen.</p>
 *
 * <p>Lombok {@code @RequiredArgsConstructor} generiert einen Konstruktor für
 * alle {@code final}-Felder, was Constructor Injection für {@link BookmarkRepository}
 * ermöglicht (bevorzugt gegenüber Field Injection).</p>
 *
 * @author Mohamad Habachia, Ibrahim Hassan
 * @version 2.0
 * @since SoSe 2026
 * @see BookmarkController
 * @see BookmarkRepository
 */
@Service
@RequiredArgsConstructor
public class BookmarkService {

    /**
     * Repository für den Datenbankzugriff.
     * Wird per Constructor Injection von Spring injiziert.
     */
    private final BookmarkRepository bookmarkRepository;

    /**
     * Gibt alle Bookmarks des angegebenen Users zurück.
     *
     * @param ownerId die Auth0 User-ID ({@code sub} Claim) des eingeloggten Users,
     *                z.B. {@code "auth0|abc123"}
     * @return Liste aller Bookmarks dieses Users, leer wenn keine vorhanden
     * @see BookmarkRepository#findByOwnerId(String)
     */
    public List<Bookmark> getAllBookmarks(String ownerId) {
        return bookmarkRepository.findByOwnerId(ownerId);
    }

    /**
     * Gibt einen einzelnen Bookmark zurück — nur wenn er dem angegebenen User gehört.
     *
     * <p>Gibt {@link Optional#empty()} zurück wenn:</p>
     * <ul>
     *   <li>Die ID nicht in der Datenbank existiert</li>
     *   <li>Die ID existiert, aber einem anderen User gehört</li>
     * </ul>
     *
     * @param id      die Datenbank-ID des gesuchten Bookmarks
     * @param ownerId die Auth0 User-ID des eingeloggten Users
     * @return den Bookmark als {@link Optional}, oder {@link Optional#empty()}
     * @see BookmarkRepository#findByIdAndOwnerId(Long, String)
     */
    public Optional<Bookmark> getBookmarkById(Long id, String ownerId) {
        return bookmarkRepository.findByIdAndOwnerId(id, ownerId);
    }

    /**
     * Erstellt einen neuen Bookmark für den eingeloggten User.
     *
     * <p>Die {@code ownerId} wird hier automatisch gesetzt — das Frontend
     * muss sie nicht mitsenden. So kann kein User Bookmarks im Namen
     * eines anderen Users erstellen.</p>
     *
     * @param bookmark das neue Bookmark-Objekt mit Titel, URL und optionalen Feldern
     * @param ownerId  die Auth0 User-ID des eingeloggten Users
     * @return der gespeicherte Bookmark mit generierter ID und Zeitstempel
     * @see BookmarkRepository#save(Object)
     */
    public Bookmark createBookmark(Bookmark bookmark, String ownerId) {
        bookmark.setOwnerId(ownerId);
        return bookmarkRepository.save(bookmark);
    }

    /**
     * Aktualisiert einen bestehenden Bookmark — nur wenn er dem angegebenen User gehört.
     *
     * <p>Folgende Felder werden aktualisiert:</p>
     * <ul>
     *   <li>{@code title} — Titel</li>
     *   <li>{@code url} — URL</li>
     *   <li>{@code description} — Beschreibung</li>
     *   <li>{@code tags} — Tags/Kategorien</li>
     *   <li>{@code gelesen} — Gelesen-Status</li>
     *   <li>{@code favorit} — Favorit-Status</li>
     * </ul>
     *
     * <p>Nicht veränderbar: {@code id}, {@code ownerId}, {@code createdAt}
     * (durch {@code updatable = false} in der Entity gesichert).</p>
     *
     * @param id      die Datenbank-ID des zu aktualisierenden Bookmarks
     * @param updated das Bookmark-Objekt mit den neuen Werten
     * @param ownerId die Auth0 User-ID des eingeloggten Users
     * @return den aktualisierten Bookmark als {@link Optional},
     *         oder {@link Optional#empty()} wenn nicht gefunden oder falscher User
     * @see BookmarkRepository#findByIdAndOwnerId(Long, String)
     */
    public Optional<Bookmark> updateBookmark(Long id, Bookmark updated, String ownerId) {
        return bookmarkRepository.findByIdAndOwnerId(id, ownerId).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setUrl(updated.getUrl());
            existing.setDescription(updated.getDescription());
            existing.setTags(updated.getTags());
            existing.setGelesen(updated.getGelesen());
            existing.setFavorit(updated.getFavorit());
            return bookmarkRepository.save(existing);
        });
    }

    /**
     * Löscht einen Bookmark — nur wenn er dem angegebenen User gehört.
     *
     * <p>Prüft zuerst per {@link BookmarkRepository#existsByIdAndOwnerId} ob der
     * Bookmark dem User gehört, bevor er gelöscht wird. So wird verhindert
     * dass ein User fremde Bookmarks löschen kann.</p>
     *
     * @param id      die Datenbank-ID des zu löschenden Bookmarks
     * @param ownerId die Auth0 User-ID des eingeloggten Users
     * @return {@code true} wenn erfolgreich gelöscht,
     *         {@code false} wenn nicht gefunden oder falscher User
     * @see BookmarkRepository#existsByIdAndOwnerId(Long, String)
     * @see BookmarkRepository#deleteById(Object)
     */
    public boolean deleteBookmark(Long id, String ownerId) {
        if (bookmarkRepository.existsByIdAndOwnerId(id, ownerId)) {
            bookmarkRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
