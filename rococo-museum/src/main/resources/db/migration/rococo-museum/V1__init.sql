create EXTENSION IF NOT EXISTS "pgcrypto";

create table if not exists museum
(
    id        UUID           NOT NULL DEFAULT gen_random_uuid(),
    title      VARCHAR(255)  NOT NULL,
    description VARCHAR(1000) ,
    photo     BYTEA,
    city      VARCHAR(255) ,
    country_id        UUID ,
    PRIMARY KEY (id)
    );

create unique index uq_museum_title_city_country_ci
on museum (
    lower(title),
    lower(city),
    country_id
);