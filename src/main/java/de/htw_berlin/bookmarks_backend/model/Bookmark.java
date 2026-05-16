package de.htw_berlin.bookmarks_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Repräsentiert einen Bookmark (Lesezeichen) in der BookmarkIt-Anwendung.
 *
 * <p>Diese Klasse dient als Datenmodell für einen einzelnen Bookmark.
 * Sie wird vom {@link de.htw_berlin.bookmarks_backend.controller.bookmarkcontroller} verwendet,
 * um Bookmark-Daten als JSON über die REST-API auszuliefern.</p>
 *
 * <p>Lombok-Annotationen generieren automatisch Getter, Setter,
 * {@code toString()}, {@code equals()} und {@code hashCode()} sowie
 * beide Konstruktoren zur Compile-Zeit.</p>
 *
 * <p>Beispiel-JSON-Darstellung eines Bookmarks:</p>
 * <pre>
 * {
 *   "id": 1,
 *   "title": "HTW Berlin",
 *   "url": "https://www.htw-berlin.de",
 *   "description": "Hochschule für Technik und Wirtschaft Berlin"
 * }
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {

    /**
     * Die eindeutige ID des Bookmarks.
     * <p>Wird als {@code Long} (nicht primitives {@code long}) definiert, da der Wert {@code null} sein kann — z.B. vor dem Speichern
     * in der Datenbank später.</p>
     */
    private Long id;

    /**
     * Der Titel des Bookmarks.
     * <p>Wird in der UI als Hauptüberschrift der Bookmark-Karte angezeigt.
     * Pflichtfeld — darf nicht leer sein.</p>
     */
    private String title;

    /**
     * Die vollständige URL des Bookmarks inklusive Protokoll.
     * <p>Pflichtfeld — muss eine gültige URL sein.</p>
     */
    private String url;

    /**
     * Eine kurze Beschreibung des Bookmarks.
     * <p>Optionales Feld — gibt dem Nutzer zusätzliche Informationen über den verlinkten Inhalt.</p>
     */
    private String description;
}
