create EXTENSION IF NOT EXISTS "pgcrypto";

create table if not exists painting
(
    id        UUID           NOT NULL DEFAULT gen_random_uuid(),
    title      VARCHAR(255)  NOT NULL,
    description VARCHAR(1000) ,
    content     BYTEA,
    artist_id        UUID   not null,
    museum_id        UUID   not null,
    PRIMARY KEY (id)
    );

create unique index uq_painting_title_artist_ci
on painting (
    lower(title),
    artist_id
);

create index if not exists idx_painting_artist_id
on painting (artist_id);