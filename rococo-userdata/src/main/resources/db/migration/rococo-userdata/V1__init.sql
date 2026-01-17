CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS "user"
(
    id        UUID           NOT NULL DEFAULT gen_random_uuid(),
    username  varchar(50) UNIQUE NOT NULL,
    firstname varchar(255),
    lastname  varchar(255),
    avatar    bytea,
    PRIMARY KEY (id)
);