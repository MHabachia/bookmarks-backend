# BookmarkIt — Backend

REST-API Backend der BookmarkIt-Webanwendung.  
Entwickelt im Rahmen des Moduls **Webtechnologien** an der HTW Berlin, SoSe 2026.



BookmarkIt ist eine Webanwendung zur Verwaltung von Lesezeichen (Bookmarks). 
Das Backend stellt eine REST-API bereit, die vom Vue.js-Frontend konsumiert wird.

## Features:
- Vollständige CRUD-API für Bookmarks (GET, POST, PUT, DELETE)
- PostgreSQL-Datenbank mit JPA/Hibernate
- Automatische Datenbank-Migration via Flyway
- Tags/Kategorien pro Bookmark
- Favorit & Gelesen Status

---

## Technologie-Stack

| Technologie | Version | Zweck |
|---|---|---|
| Java | 21 | Programmiersprache |
| Spring Boot | 4.0 | Web-Framework |
| Gradle | 8 | Build-Tool |
| Spring Data JPA | 4.0 | Datenbank-Abstraktion |
| PostgreSQL | 16 | Datenbank |
| Flyway | 11.14 | Datenbank-Migration |


---

## Projektstruktur

```
bookmarks-backend/
├── build.gradle                          ← Dependencies & Build-Konfiguration
├── settings.gradle                       ← Einstellungen
├── gradle/wrapper/
│   └── gradle-wrapper.properties         ← Gradle-Konfiguration
└── src/
    └── main/
        ├── java/de/htw_berlin/bookmarks_backend/
        │   ├── BookmarksBackendApplication.java  ← Einstiegspunkt (main)
        │   ├── FlywayConfig.java                 ← Flyway-Konfiguration
        │   ├── controller/
        │   │   └── BookmarkController.java       ← REST-Endpunkte
        │   ├── model/
        │   │   └── Bookmark.java                 ← JPA-Entität
        │   ├── repository/
        │   │   └── BookmarkRepository.java       ← Spring Data Repository
        │   └── service/
        │       └── BookmarkService.java          ← Geschäftslogik
        └── resources/
            ├── application.properties            ← Konfiguration
            └── db/migration/
                └── V1__create_bookmarks.sql      ← Flyway Migration
```

---

## API-Dokumentation

### Basis-URL

```
Lokal:       http://localhost:8080/api
Produktion:  https://your-domain.domain/api
```

### Endpunkte

#### `GET /api/bookmarks`

Gibt alle Bookmarks zurück.

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
    "createdAt": "2026-05-26T12:00:00"
  }
]
```

#### `GET /api/bookmarks/{id}`

Gibt einen einzelnen Bookmark zurück.

**Response `200 OK`:** Bookmark-Objekt  
**Response `404 Not Found`:** Bookmark nicht gefunden

#### `POST /api/bookmarks`

Erstellt einen neuen Bookmark.

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

Aktualisiert einen bestehenden Bookmark (auch für Favorit/Gelesen Toggle).

**Request-Body:** Vollständiges Bookmark-Objekt  
**Response `200 OK`:** Aktualisierter Bookmark  
**Response `404 Not Found`:** Bookmark nicht gefunden

#### `DELETE /api/bookmarks/{id}`

Löscht einen Bookmark.

**Response `204 No Content`:** Erfolgreich gelöscht  
**Response `404 Not Found`:** Bookmark nicht gefunden

### Felder

| Feld | Typ | Pflicht | Beschreibung |
|---|---|---|---|
| `id` | `Long` | auto | Eindeutige ID (automatisch generiert) |
| `title` | `String` | ja | Titel des Bookmarks (max. 255 Zeichen) |
| `url` | `String` | ja | Vollständige URL inkl. Protokoll (max. 500 Zeichen) |
| `description` | `String` | nein | Kurze Beschreibung |
| `gelesen` | `Boolean` | nein | Gelesen-Status (Standard: false) |
| `favorit` | `Boolean` | nein | Favorit-Status (Standard: false) |
| `tags` | `List<String>` | nein | Liste der Tags/Kategorien |
| `createdAt` | `LocalDateTime` | auto | Erstellungszeitpunkt (automatisch gesetzt) |

---

## Datenbank

### Schema

```sql
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
```

### Flyway Migration

Das Schema wird automatisch via Flyway beim ersten Start erstellt.  
Migrationsdateien liegen unter `src/main/resources/db/migration/`.

| Version | Datei | Beschreibung |
|---|---|---|
| V1 | `V1__create_bookmarks.sql` | Erstellt bookmarks + bookmark_tags Tabellen |

---

## Server-Deployment — Voraussetzungen

Folgende Software muss auf dem Server installiert sein:

| Software | Version | Zweck |
|---|---|---|
| Ubuntu | 24.04 | Betriebssystem |
| Java | 21 | Spring Boot Runtime |
| PostgreSQL | 16+ | Datenbank |
| Nginx | aktuell | API Reverse Proxy |

### PostgreSQL einrichten

```bash
# Datenbank und User anlegen
sudo -u postgres psql

CREATE DATABASE bookmarkit;
CREATE USER bookmarkit WITH PASSWORD 'DEIN_PASSWORT';
GRANT ALL PRIVILEGES ON DATABASE bookmarkit TO bookmarkit;
\c bookmarkit
GRANT ALL ON SCHEMA public TO bookmarkit;
GRANT CREATE ON SCHEMA public TO bookmarkit;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO bookmarkit;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO bookmarkit;
\q
```

> Die Tabellen werden beim ersten Start **automatisch** von Flyway erstellt.

---

## Lokale Entwicklung

### Voraussetzungen

- Java 21
- PostgreSQL (lokal oder via SSH-Tunnel)

### SSH-Tunnel zur Produktions-DB (optional)

```bash
ssh -L 5432:localhost:5432 user@SERVER_IP -N
```

### Konfiguration (`application.properties`)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bookmarkit
spring.datasource.username=bookmarkit
spring.datasource.password=DEIN_PASSWORT
spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

### Starten

```bash
git clone https://github.com/MHabachia/bookmarks-backend.git
cd bookmarks-backend
./gradlew bootRun
```

### JAR bauen

```bash
./gradlew bootJar
# JAR liegt in: build/libs/bookmarks-backend-0.0.1-SNAPSHOT.jar
```

---

## Deployment

Das Backend läuft auf einem selbst gehosteten Server (Virtuelle Maschine, Ubuntu 24.04).  
Nginx leitet `/api`-Anfragen intern an Spring Boot weiter.

### Architektur

```
Internet → Reverse-Proxy (SSL) → Nginx (Port 80) → Spring Boot (Port 8080) → PostgreSQL (Port 5432)
```

### Deploy-Befehl

```bash
cd /opt/bookmarkit/bookmarks-backend
git pull
./gradlew bootJar
systemctl restart bookmarkit
```

---

## Milestones

| Milestone | Beschreibung | Deadline | Status |
|---|---|---|---|
| M1 | Spring Boot Backend mit `GET /api/bookmarks` | 19. April | ✅ |
| M2 | Vue.js Frontend auf GitHub | 10. Mai | ✅ |
| M3 | Frontend & Backend deployed | 24. Mai | ✅ |
| M4 | PostgreSQL + CRUD API (GET/POST/PUT/DELETE) | 14. Juni | ✅ |
| Finale | Tests, GitHub Actions, Screenshot-Dokumentation | 5. Juli | ⏳ |

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
