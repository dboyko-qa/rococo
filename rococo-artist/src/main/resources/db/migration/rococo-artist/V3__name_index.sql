CREATE UNIQUE INDEX IF NOT EXISTS uq_artist_name_ci
ON artist (LOWER(name));