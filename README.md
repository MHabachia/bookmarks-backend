# BookmarkIt — Backend

REST-API Backend der BookmarkIt-Webanwendung.  
Entwickelt im Rahmen des Moduls **Webtechnologien** an der HTW Berlin, SoSe 2026.

---



## Projektbeschreibung

BookmarkIt ist eine Webanwendung zur Verwaltung von Lesezeichen (Bookmarks).  
Das Backend stellt eine REST-API bereit, die vom Vue.js-Frontend konsumiert wird.

Aktuell (M1–M3) werden Bookmarks als statische In-Memory-Liste zurückgegeben.  
Ab M4 wird eine PostgreSQL-Datenbank angebunden.

---

## Technologie-Stack

| Technologie            | Zweck     |
|------------------------|-----------|
| Java version 21        | Programmiersprache |
| Spring Boot version 4  | Web-Framework |
| Gradle       version 8 | Build-Tool |

---

## Projektstruktur

```
bookmark-backend/
├── build.gradle                          ← Dependencies & Build-Konfiguration
├── settings.gradle                       ← Einstellungen
├── gradle/wrapper/
│   └── gradle-wrapper.properties         ← Gradle-Konfiguration
├── src/
│   └── main/
│       ├── java/com/htw/bookmark/
│       │   ├── BookmarkApplication.java  ← Einstiegspunkt (main)
│       │   ├── controller/
│       │   │   └── BookmarkController.java ← REST-Endpunkte
│       │   └── model/
│       │       └── Bookmark.java         ← Datenmodell (DB-Entität)
│       └── resources/
│           └── application.properties    ← Konfiguration (Port usw.)
└── README.md
```

---

## API-Dokumentation

### Basis-URL

```
Lokal:       http://localhost:8080/api
Produktion:  https://domain.de/api
```

### Endpunkte

#### `GET /api/bookmarks`

Gibt eine Liste aller gespeicherten Bookmarks zurück.

**Request:**
```http
GET /api/bookmarks
```

**Response `200 OK`:**
```json
[
  {
    "id": 1,
    "title": "HTW Berlin",
    "url": "https://www.htw-berlin.de",
    "description": "Hochschule für Technik und Wirtschaft Berlin"
  },
```

**Felder:**

| Feld | Typ | Pflicht | Beschreibung |
|---|---|---|---|
| `id` | `Long` | ja | Eindeutige ID des Bookmarks |
| `title` | `String` | ja | Titel des Bookmarks |
| `url` | `String` | ja | Vollständige URL inkl. Protokoll |
| `description` | `String` | nein | Kurze Beschreibung |

---

## Lokale Deployment

### Voraussetzungen

- Java 21 oder höher
- Gradle (oder der mitgelieferte Wrapper `./gradlew`)

### Starten

```bash
# Repository clonen
git clone https://github.com/MHabachia/bookmarks-backend.git
cd bookmarks-backend

# Anwendung starten
./gradlew bootRun
```

Die API ist dann erreichbar unter:
```
http://localhost:8080/api/bookmarks
```

### JAR bauen

```bash
./gradlew bootJar
# JAR liegt in: build/libs/bookmarks-backend-0.0.1-SNAPSHOT.jar
```

### JAR starten

```bash
java -jar build/libs/bookmarks-backend-0.0.1-SNAPSHOT.jar
```

---

## Aktuelle Deployment

Das Backend wird auf einem selbst gehosteten Server (Proxmox LXC, Ubuntu 24.04)
betrieben. Nginx leitet `/api`-Anfragen intern an Spring Boot weiter.
Reverse-Proxy übernimmt SSL-Terminierung und Routing.

### Architektur

```
Internet → Reverse-Proxy (SSL-Termination) → Nginx (Port 80) → Spring Boot (Port 8080)
```


## Milestones

| Milestone | Beschreibung | Deadline | Status |
|---|---|---|---|
| M1 | Spring Boot Backend mit `GET /api/bookmarks` | 19. April | ✅ |
| M2 | Vue.js Frontend auf GitHub | 10. Mai | ✅ |
| M3 | Frontend & Backend deployed, Frontend ruft GET-Route auf | 24. Mai | ✅ |
| M4 | PostgreSQL-Datenbank + POST-Route | 14. Juni | ⏳ |
| Finale | Tests, GitHub Actions, Screenshot-Dokumentation | 5. Juli | ⏳ |

---

## Team

- **Team: 40** · **Kurs: Webtechnologien**
- Studiengang: Wirtschaftsinfromatik · HTW Berlin · SoSe 2026

| Name | GitHub |
|---|---|
| Mohamad Habachia | [@MHabachia](https://github.com/MHabachia) |
| Ibrahim Hassan | |

### Repositories

- **Backend:** https://github.com/MHabachia/bookmarks-backend
- **Frontend:** https://github.com/MHabachia/bookmarks-frontend
