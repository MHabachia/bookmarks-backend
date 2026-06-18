# BookmarkIt — Backend

REST-API Backend der BookmarkIt-Webanwendung.  
Entwickelt im Rahmen des Moduls **Webtechnologien** an der HTW Berlin, SoSe 2026.

BookmarkIt ist eine Webanwendung zur Verwaltung von Lesezeichen. Das Backend stellt eine
gesicherte REST-API bereit, die vom Vue.js-Frontend konsumiert wird. Alle Endpunkte sind
über **Auth0 JWT-Authentifizierung** geschützt — jeder User sieht nur seine eigenen Bookmarks.

---

## Features

- Vollständige CRUD-API für Bookmarks (GET, POST, PUT, DELETE)
- JWT-Authentifizierung via Auth0 (OAuth2 Resource Server)
- User-Isolation: Bookmarks sind per `owner_id` an den eingeloggten User gebunden
- PostgreSQL-Datenbank mit JPA/Hibernate
- Automatische Datenbank-Migration via Flyway
- Tags/Kategorien pro Bookmark
- Favorit & Gelesen Status
- Docker-Deployment auf Render

---

## Technologie-Stack

| Technologie | Version | Zweck |
|---|---|---|
| Java | 21 | Programmiersprache |
| Spring Boot | 4.0 | Web-Framework |
| Spring Security | 4.0 | Authentifizierung & Autorisierung |
| OAuth2 Resource Server | 4.0 | JWT-Validierung gegen Auth0 |
| Gradle | 8 | Build-Tool |
| Spring Data JPA | 4.0 | Datenbank-Abstraktion |
| PostgreSQL | 16 | Datenbank |
| Flyway | 11.14 | Datenbank-Migration |
| Lombok | aktuell | Boilerplate-Reduktion |
| Docker | aktuell | Container-Deployment |
| Testcontainers | 1.20.4 | Echte PostgreSQL in Tests |

---

## Projektstruktur

```
bookmarks-backend/
├── Dockerfile                                    ← Docker-Build für Render
├── build.gradle                                  ← Dependencies & Build-Konfiguration
├── settings.gradle
└── src/
    ├── main/
    │   ├── java/de/htw_berlin/bookmarks_backend/
    │   │   ├── BookmarksBackendApplication.java  ← Einstiegspunkt (main)
    │   │   ├── config/
    │   │   │   └── SecurityConfig.java           ← JWT-Validierung, CORS
    │   │   ├── controller/
    │   │   │   └── BookmarkController.java       ← REST-Endpunkte
    │   │   ├── model/
    │   │   │   └── Bookmark.java                 ← JPA-Entität (inkl. ownerId)
    │   │   ├── repository/
    │   │   │   └── BookmarkRepository.java       ← Spring Data Repository
    │   │   └── service/
    │   │       └── BookmarkService.java          ← Geschäftslogik
    │   └── resources/
    │       ├── application.properties            ← Konfiguration (via Env-Variablen)
    │       └── db/migration/
    │           ├── V1__create_bookmarks.sql      ← Tabellen anlegen
    │           └── V2__add_owner_id.sql          ← owner_id für User-Isolation
    └── test/
        ├── java/de/htw_berlin/bookmarks_backend/
        │   ├── config/
        │   │   └── TestSecurityConfig.java       ← Security-Mock für Tests
        │   ├── controller/
        │   │   └── BookmarkControllerTest.java   ← Unit-Tests (Mockito)
        │   ├── repository/
        │   │   └── BookmarkRepositoryTest.java   ← Integrationstests (Testcontainers)
        │   └── service/
        │       └── BookmarkServiceTest.java      ← Unit-Tests (Mockito)
        └── resources/
            └── application-test.properties       ← Test-Konfiguration
```

---

## Tests

### Teststrategie

Das Projekt nutzt zwei Arten von Tests entsprechend der **Test-Pyramide**:

```
         △  Integrationstests — echte PostgreSQL (Testcontainers)
        △△△  Unit-Tests — Mockito (kein Datenbankzugriff)
```

**Unit-Tests** testen jede Schicht isoliert und sind sehr schnell.  
**Integrationstests** testen SQL-Queries gegen eine echte Datenbank.

### Tests ausführen

```bash
./gradlew test
```

### Testübersicht

| Datei | Typ | Anzahl | Was wird getestet |
|---|---|---|---|
| `BookmarkServiceTest` | Unit (Mockito) | 6 | Geschäftslogik, `ownerId`-Zuordnung |
| `BookmarkControllerTest` | Unit (Mockito) | 6 | HTTP-Status-Codes, Response-Bodies |
| `BookmarkRepositoryTest` | Integration (Testcontainers) | 5 | SQL-Queries, Flyway-Migrationen, User-Isolation |

**Gesamt: 17 Tests**

### BookmarkServiceTest (Unit)

Testet die Geschäftslogik ohne Datenbankverbindung via Mockito:

- `getAllBookmarks` gibt nur Bookmarks des eingeloggten Users zurück
- `createBookmark` setzt `ownerId` korrekt aus dem JWT-Token
- `getBookmarkById` gibt Bookmark zurück wenn gefunden
- `getBookmarkById` gibt leer zurück wenn nicht gefunden
- `updateBookmark` aktualisiert Felder korrekt
- `deleteBookmark` gibt `true`/`false` je nach Ergebnis

### BookmarkControllerTest (Unit)

Testet HTTP-Verhalten ohne Server und Datenbankverbindung:

- `GET /api/bookmarks` → 200 mit Liste
- `GET /api/bookmarks/{id}` → 200 wenn gefunden
- `GET /api/bookmarks/{id}` → 404 wenn nicht gefunden
- `POST /api/bookmarks` → 201 mit gespeichertem Bookmark
- `DELETE /api/bookmarks/{id}` → 204 wenn erfolgreich
- `DELETE /api/bookmarks/{id}` → 404 wenn nicht gefunden

### BookmarkRepositoryTest (Integration)

Testet SQL-Queries gegen eine **echte PostgreSQL 16** Datenbank via Testcontainers.
Flyway läuft automatisch durch (V1 + V2 Migrationen):

- `findByOwnerId` — User-Isolation funktioniert auf DB-Ebene
- `findByIdAndOwnerId` — kein fremder User sieht fremde Bookmarks
- `findByOwnerIdAndFavoritTrue` — Favoriten-Filter korrekt
- `existsByIdAndOwnerId` — Existenz-Check mit User-Prüfung

---

## GitHub Actions CI

Bei jedem Push auf `main` wird automatisch ausgeführt:

```
Push auf main
      ↓
1. Java 21 einrichten
2. Gradle Wrapper berechtigen
3. ./gradlew test  → 17 Tests (Unit + Integration mit Testcontainers)
4. ./gradlew bootJar  → Production JAR bauen
```

Die Pipeline läuft vollständig ohne externe Dienste — Testcontainers startet
PostgreSQL automatisch als Docker-Container innerhalb des GitHub Actions Runners.

---

## API-Dokumentation

> **Alle Endpunkte erfordern einen gültigen JWT-Token:**
> ```
> Authorization: Bearer <token>
> ```
> Der Token wird vom Vue.js Frontend automatisch nach dem Auth0-Login mitgeschickt.

### Basis-URL

```
Lokal:       http://localhost:8080/api
Produktion:  https://bookmarks-backend-uats.onrender.com/api
```

### Endpunkte

| Methode | Endpunkt | Beschreibung | Response |
|---|---|---|---|
| GET | `/api/bookmarks` | Alle Bookmarks des eingeloggten Users | 200 |
| GET | `/api/bookmarks/{id}` | Einzelnen Bookmark laden | 200 / 404 |
| POST | `/api/bookmarks` | Neuen Bookmark erstellen | 201 |
| PUT | `/api/bookmarks/{id}` | Bookmark aktualisieren | 200 / 404 |
| DELETE | `/api/bookmarks/{id}` | Bookmark löschen | 204 / 404 |

### Datenmodell

| Feld | Typ | Pflicht | Beschreibung |
|---|---|---|---|
| `id` | `Long` | auto | Eindeutige ID |
| `title` | `String` | ✅ | Titel (max. 255 Zeichen) |
| `url` | `String` | ✅ | URL inkl. Protokoll (max. 500 Zeichen) |
| `description` | `String` | nein | Kurze Beschreibung |
| `gelesen` | `Boolean` | nein | Gelesen-Status (Standard: `false`) |
| `favorit` | `Boolean` | nein | Favorit-Status (Standard: `false`) |
| `tags` | `List<String>` | nein | Tags/Kategorien |
| `ownerId` | `String` | auto | Auth0 User-ID (`sub` Claim) |
| `createdAt` | `LocalDateTime` | auto | Erstellungszeitpunkt |

---

## Datenbank

### Flyway Migrationen

| Version | Datei | Beschreibung |
|---|---|---|
| V1 | `V1__create_bookmarks.sql` | Tabellen `bookmarks` + `bookmark_tags` |
| V2 | `V2__add_owner_id.sql` | `owner_id` Spalte + Index |

---

## Authentifizierung

Das Backend ist ein **OAuth2 Resource Server**. Es validiert JWT-Tokens selbst —
kein Session-Management, vollständig stateless.

```
Frontend → Authorization: Bearer <JWT> → Spring Security
                                               ↓
                                   JWT-Signatur prüfen gegen
                                   Auth0 JWKS-Endpoint
                                               ↓
                                   token.getName() = Auth0 sub
                                   z.B. "auth0|abc123"
                                               ↓
                                   Nur Bookmarks dieses Users
```

---

## Lokale Entwicklung

### Voraussetzungen

- Java 21
- PostgreSQL (lokal installiert)
- Docker (für Testcontainers)
- Auth0-Account (kostenlos)

### Setup

```bash
git clone https://github.com/MHabachia/bookmarks-backend.git
cd bookmarks-backend
```

Lokale Konfiguration anlegen (wird nicht committed):

```properties
# src/main/resources/application-local.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bookmarkit
spring.datasource.username=postgres
spring.datasource.password=DEIN_PASSWORT
auth0.issuer-uri=https://dev-XXXXX.eu.auth0.com/
frontend.url=http://localhost:5173
```

### Starten

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

---

## Deployment auf Render

### Umgebungsvariablen

| Variable | Beschreibung |
|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL URL aus Render Dashboard |
| `SPRING_DATASOURCE_USERNAME` | PostgreSQL User |
| `SPRING_DATASOURCE_PASSWORD` | PostgreSQL Passwort |
| `AUTH0_ISSUER_URI` | `https://dev-XXXXX.eu.auth0.com/` (mit `/` am Ende!) |
| `FRONTEND_URL` | `https://bookmarkit-frontend.onrender.com` |

---

## Milestones

| Milestone | Beschreibung | Deadline | Status |
|---|---|---|---|
| M1 | Spring Boot Backend mit `GET /api/bookmarks` | 19. April | ✅ |
| M2 | Vue.js Frontend auf GitHub | 10. Mai | ✅ |
| M3 | Frontend & Backend deployed | 24. Mai | ✅ |
| M4 | PostgreSQL + vollständige CRUD-API | 14. Juni | ✅ |
| Finale | Auth0, User-Isolation, Tests, GitHub Actions | 5. Juli | ✅ |

---

## Team

**Team 40 · Kurs: Webtechnologien · HTW Berlin · SoSe 2026**

| Name | GitHub |
|---|---|
| Mohamad Habachia | [@MHabachia](https://github.com/MHabachia) |
| Ibrahim Hassan | [@Hassan9977](https://github.com/Hassan9977) |

- **Backend:** https://github.com/MHabachia/bookmarks-backend
- **Frontend:** https://github.com/MHabachia/bookmarks-frontend
