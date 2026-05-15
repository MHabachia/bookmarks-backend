# Bookmark App – Backend

Einfache Bookmark-App als Studiumsprojekt im Fach **Webtechnologien** an der HTW Berlin.

## Technologie-Stack

- **Java 21**
- **Spring Boot 4.0**
- **Gradle** (Build-Tool)
- **Lombok** (weniger Boilerplate)

## Projektbeschreibung

Diese App ermöglicht es, Bookmarks (Links mit Titel und Beschreibung) zu verwalten.  
Das Backend stellt eine REST-API bereit, die vom Vue.js-Frontend konsumiert wird.

## API-Endpunkte

| Methode | Route            | Beschreibung                    |
|---------|------------------|---------------------------------|
| GET     | `/api/bookmarks` | Liste aller Bookmarks zurückgeben |

## Lokale Ausführung

```bash
./gradlew bootRun
```

Die API ist dann erreichbar unter: `http://localhost:8080/api/bookmarks`

## Milestones

- [x] **M1** – Spring Boot Projekt aufgesetzt, GET-Route `/api/bookmarks` gibt Beispiel-Daten zurück
- [ ] **M2** – Vue.js Frontend deployed auf Render, ruft GET-Route auf
- [ ] **M3** – Frontend und Backend auf Render deployed
- [ ] **M4** – PostgreSQL-Datenbank, POST-Route zum Speichern neuer Bookmarks

## Team

| Name | Rolle |
|------|-------|
| ...  | ...   |
