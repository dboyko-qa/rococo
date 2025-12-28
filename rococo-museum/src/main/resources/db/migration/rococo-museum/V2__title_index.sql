CREATE UNIQUE INDEX IF NOT EXISTS uq_museum_title_ci
ON museum (LOWER(title));