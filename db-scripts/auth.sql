drop table if exists AUTH_PASSWORD;
CREATE TABLE AUTH_PASSWORD(
    id BIGSERIAL PRIMARY KEY ,
    user_id BIGINT NOT NULL,
    password TEXT NOT NULL,
    created_date date default now(),
    updated_date date,
    deleted_date date
);
