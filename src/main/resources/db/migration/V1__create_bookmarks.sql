CREATE TABLE bookmarks (
                           id          BIGSERIAL PRIMARY KEY,
                           title       VARCHAR(255) NOT NULL,
                           url         VARCHAR(500) NOT NULL,
                           description TEXT,
                           gelesen     BOOLEAN DEFAULT FALSE,
                           favorit     BOOLEAN DEFAULT FALSE,
                           created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Separate Tabelle für Tags (wegen @ElementCollection)
CREATE TABLE bookmark_tags (
                               bookmark_id BIGINT REFERENCES bookmarks(id) ON DELETE CASCADE,
                               tag         VARCHAR(100)
);