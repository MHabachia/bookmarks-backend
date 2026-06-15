-- V2: owner_id Spalte für Auth0-User-Zuordnung

-- 1. Spalte hinzufügen (zunächst nullable für bestehende Zeilen)
ALTER TABLE bookmarks ADD COLUMN owner_id VARCHAR(128);

-- 2. Bestehende Bookmarks einem System-Platzhalter zuweisen
--    (können in der App nicht mehr gesehen werden — einloggen und neu erstellen)
UPDATE bookmarks SET owner_id = 'legacy|migrated' WHERE owner_id IS NULL;

-- 3. NOT NULL Constraint setzen
ALTER TABLE bookmarks ALTER COLUMN owner_id SET NOT NULL;

-- 4. Index für schnelle Abfragen nach User
CREATE INDEX idx_bookmarks_owner_id ON bookmarks(owner_id);
