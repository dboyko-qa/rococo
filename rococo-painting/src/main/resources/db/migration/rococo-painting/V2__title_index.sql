CREATE UNIQUE INDEX IF NOT EXISTS uq_painting_title_ci
ON painting (LOWER(title));