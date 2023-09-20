drop table if exists USER_USER;
CREATE TABLE USER_USER
(
    id           BIGSERIAL primary key ,
    username     VARCHAR UNIQUE NOT NULL,
    email        VARCHAR UNIQUE NOT NULL,
    phone_number VARCHAR UNIQUE,
    password     VARCHAR,
    first_name   VARCHAR not null,
    last_name    varchar not null,
    birthdate    date,
    organization bigint,
    created_date date default now(),
    updated_date date,
    deleted_date date
);
insert into USER_USER (username, email, first_name, last_name)
values ('dev', 'dev@gpmn.net', 'Dev', 'developer');

drop table if exists USER_ROLE;
CREATE TABLE USER_ROLE
(
    id           BIGSERIAL primary key ,
    code         VARCHAR UNIQUE NOT NULL,
    name         VARCHAR UNIQUE NOT NULL,
    status       BOOLEAN default TRUE,
    description  VARCHAR,
    created_date date default now(),
    updated_date date,
    created_by   varchar,
    updated_by   varchar
);
insert into USER_ROLE (code, name)
values ('QL', 'Quản lý');

drop table if exists user_role_mapping;
CREATE TABLE user_role_mapping
(
    user_id bigint references USER_USER(id),
    role_id bigint references USER_ROLE(id)
)