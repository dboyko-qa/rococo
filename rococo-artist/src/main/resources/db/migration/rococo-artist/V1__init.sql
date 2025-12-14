CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS artist
(
    id        UUID           NOT NULL DEFAULT gen_random_uuid(),
    name      VARCHAR(255)   UNIQUE NOT NULL,
    biography VARCHAR(2000)  NOT NULL,
    photo     BYTEA,
    PRIMARY KEY (id)
);