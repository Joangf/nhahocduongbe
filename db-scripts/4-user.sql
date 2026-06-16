-- drop table if exists USER_USER;
-- CREATE TABLE USER_USER
-- (
--     id           BIGSERIAL primary key,
--     username     VARCHAR UNIQUE NOT NULL,
--     email        VARCHAR UNIQUE NOT NULL,
--     phone_number VARCHAR UNIQUE,
--     password     VARCHAR,
--     first_name   VARCHAR        not null,
--     last_name    varchar        not null,
--     birthdate    date,
--     organization bigint,
--     created_date timestamp default now(),
--     updated_date timestamp,
--     created_by   varchar,
--     updated_by   varchar
-- );

--default password: 123
insert into USER_USER (username, password, email, first_name, last_name, organization)
values ('admin', '$2a$10$4DwHEwyHmjhZr/TMKxX7euQMUczpOvuyxggLdNQR8wLwRng..1R3W', 'dev@gpmn.net', 'admin', 'developer',
        null),
       ('bvrhm', '$2a$10$4DwHEwyHmjhZr/TMKxX7euQMUczpOvuyxggLdNQR8wLwRng..1R3W', 'ql_bvrhm@ql_bvrhm.net', 'Quản lý',
        'BVRHM', 1),
       ('083001', '$2a$10$4DwHEwyHmjhZr/TMKxX7euQMUczpOvuyxggLdNQR8wLwRng..1R3W', '083001', 'Quản lý',
        'Trường TH Thị trấn Chợ Lách', 10279),
       ('083002', '$2a$10$4DwHEwyHmjhZr/TMKxX7euQMUczpOvuyxggLdNQR8wLwRng..1R3W', '083002', 'Quản lý',
        'Trường TH Thị trấn Giồng Trôm', 10280),
       ('083003', '$2a$10$4DwHEwyHmjhZr/TMKxX7euQMUczpOvuyxggLdNQR8wLwRng..1R3W', '083003', 'Quản lý',
        'Trường TH Chu Văn An', 10281),
       ('083004', '$2a$10$4DwHEwyHmjhZr/TMKxX7euQMUczpOvuyxggLdNQR8wLwRng..1R3W', '083004', 'Quản lý',
        'Trường TH Trần Hoàn Vũ', 10282),
       ('083005', '$2a$10$4DwHEwyHmjhZr/TMKxX7euQMUczpOvuyxggLdNQR8wLwRng..1R3W', '083005', 'Quản lý',
        'Trường TH Tân Bình', 10283),
       ('083006', '$2a$10$4DwHEwyHmjhZr/TMKxX7euQMUczpOvuyxggLdNQR8wLwRng..1R3W', '083006', 'Quản lý',
        'Trường TH Tân Thạch A', 10284),
       ('083007', '$2a$10$4DwHEwyHmjhZr/TMKxX7euQMUczpOvuyxggLdNQR8wLwRng..1R3W', '083007', 'Quản lý',
        'Trường TH An Thới', 10285),
       ('083008', '$2a$10$4DwHEwyHmjhZr/TMKxX7euQMUczpOvuyxggLdNQR8wLwRng..1R3W', '083008', 'Quản lý',
        'Trường TH Thị trấn Thạnh Phú', 10286),
       ('083009', '$2a$10$4DwHEwyHmjhZr/TMKxX7euQMUczpOvuyxggLdNQR8wLwRng..1R3W', '083009', 'Quản lý',
        'Trường TH An Bình Tây', 10287);
;

drop table if exists USER_ROLE;
CREATE TABLE USER_ROLE
(
    id           BIGSERIAL primary key,
    code         VARCHAR UNIQUE NOT NULL,
    name         VARCHAR UNIQUE NOT NULL,
    status       BOOLEAN   default TRUE,
    description  VARCHAR,
    created_date timestamp default now(),
    updated_date timestamp,
    created_by   varchar,
    updated_by   varchar
);
insert into USER_ROLE (code, name)
values ('QL', 'Quản lý');

drop table if exists user_role_mapping;
CREATE TABLE user_role_mapping
(
    user_id bigint references USER_USER (id),
    role_id bigint references USER_ROLE (id)
)