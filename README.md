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

---

## Projektstruktur

```
bookmarks-backend/
├── Dockerfile                                    ← Docker-Build für Render
├── build.gradle                                  ← Dependencies & Build-Konfiguration
├── settings.gradle
└── src/
    └── main/
        ├── java/de/htw_berlin/bookmarks_backend/
        │   ├── BookmarksBackendApplication.java  ← Einstiegspunkt (main)
        │   ├── config/
        │   │   └── SecurityConfig.java           ← JWT-Validierung, CORS
        │   ├── controller/
        │   │   └── BookmarkController.java       ← REST-Endpunkte
        │   ├── model/
        │   │   └── Bookmark.java                 ← JPA-Entität (inkl. ownerId)
        │   ├── repository/
        │   │   └── BookmarkRepository.java       ← Spring Data Repository
        │   └── service/
        │       └── BookmarkService.java          ← Geschäftslogik
        └── resources/
            ├── application.properties            ← Konfiguration (via Env-Variablen)
            └── db/migration/
                ├── V1__create_bookmarks.sql      ← Tabellen anlegen
                └── V2__add_owner_id.sql          ← owner_id für User-Isolation
```

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
Produktion:  https://bookmarkit-backend.onrender.com/api
```

### Endpunkte

#### `GET /api/bookmarks`
Gibt alle Bookmarks des eingeloggten Users zurück.

**Response `200 OK`:**
```json
[
  {
    "id": 1,
    "title": "HTW Berlin",
    "url": "https://www.htw-berlin.de",
    "description": "Hochschule für Technik und Wirtschaft Berlin",
    "gelesen": false,
    "favorit": false,
    "tags": ["Studium", "HTW"],
    "ownerId": "auth0|abc123",
    "createdAt": "2026-05-26T12:00:00"
  }
]
```

#### `GET /api/bookmarks/{id}`
Gibt einen einzelnen Bookmark zurück — nur wenn er dem eingeloggten User gehört.

**Response `200 OK`:** Bookmark-Objekt  
**Response `404 Not Found`:** Nicht gefunden oder gehört einem anderen User

#### `POST /api/bookmarks`
Erstellt einen neuen Bookmark. Die `ownerId` wird automatisch aus dem JWT-Token gesetzt.

**Request-Body:**
```json
{
  "title": "HTW Berlin",
  "url": "https://www.htw-berlin.de",
  "description": "Hochschule für Technik und Wirtschaft Berlin",
  "tags": ["Studium", "HTW"]
}
```

**Response `201 Created`:** Gespeicherter Bookmark mit generierter ID

#### `PUT /api/bookmarks/{id}`
Aktualisiert einen Bookmark — nur wenn er dem eingeloggten User gehört.  
Wird auch für Favorit/Gelesen-Toggle genutzt.

**Response `200 OK`:** Aktualisierter Bookmark  
**Response `404 Not Found`:** Nicht gefunden oder gehört einem anderen User

#### `DELETE /api/bookmarks/{id}`
Löscht einen Bookmark — nur wenn er dem eingeloggten User gehört.

**Response `204 No Content`:** Erfolgreich gelöscht  
**Response `404 Not Found`:** Nicht gefunden oder gehört einem anderen User

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

### Schema

```sql
-- V1: Grundstruktur
CREATE TABLE bookmarks (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    url         VARCHAR(500) NOT NULL,
    description TEXT,
    gelesen     BOOLEAN DEFAULT FALSE,
    favorit     BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bookmark_tags (
    bookmark_id BIGINT REFERENCES bookmarks(id) ON DELETE CASCADE,
    tag         VARCHAR(100)
);

-- V2: User-Isolation via Auth0
ALTER TABLE bookmarks ADD COLUMN owner_id VARCHAR(128) NOT NULL;
CREATE INDEX idx_bookmarks_owner_id ON bookmarks(owner_id);
```

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

Spring Security Konfiguration (`SecurityConfig.java`):
- Alle `/api/**` Routen erfordern Authentifizierung
- CORS erlaubt nur die eingetragene Frontend-URL (`FRONTEND_URL`)
- CSRF deaktiviert (stateless REST API)

---

## Lokale Entwicklung

### Voraussetzungen

- Java 21
- PostgreSQL (lokal installiert)
- Auth0-Account (kostenlos)

### Setup

```bash
git clone https://github.com/MHabachia/bookmarks-backend.git
cd bookmarks-backend
```

Lokale Konfiguration anlegen (wird nicht committed):

```bash
# src/main/resources/application-local.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bookmarkit
spring.datasource.username=postgres
spring.datasource.password=DEIN_PASSWORT
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://dev-XXXXX.eu.auth0.com/
frontend.url=http://localhost:5173
```

### Starten

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### JAR bauen

```bash
./gradlew bootJar
# JAR liegt in: build/libs/bookmarks-backend-0.0.1-SNAPSHOT.jar
```

---

## Deployment auf Render

### Architektur

```
Internet → Render (SSL) → Docker Container :10000 → PostgreSQL (Render Managed)
                Auth0 validiert JWT-Tokens
```

### Umgebungsvariablen auf Render setzen

| Variable | Beschreibung |
|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL URL aus Render Dashboard |
| `SPRING_DATASOURCE_USERNAME` | PostgreSQL User |
| `SPRING_DATASOURCE_PASSWORD` | PostgreSQL Passwort |
| `AUTH0_ISSUER_URI` | `https://dev-XXXXX.eu.auth0.com/` (mit `/` am Ende!) |
| `FRONTEND_URL` | `https://bookmarkit-frontend.onrender.com` |

### Render Build-Einstellungen

| Einstellung | Wert |
|---|---|
| Runtime | Docker |
| Dockerfile | `./Dockerfile` |
| Port | `10000` |

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

### Repositories

- **Backend:** https://github.com/MHabachia/bookmarks-backend
- **Frontend:** https://github.com/MHabachia/bookmarks-frontend
