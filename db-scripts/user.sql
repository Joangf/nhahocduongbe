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
    created_date date default now(),
    updated_date date,
    deleted_date date
);
insert into user_user (username, email, first_name, last_name)
values ('dev', 'dev@gpmn.net', 'Dev', 'developer');
