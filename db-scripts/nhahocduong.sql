DROP TABLE if exists NHAHOCDUONG_DENTIST CASCADE ;
CREATE TABLE NHAHOCDUONG_DENTIST
(
    id           BIGSERIAL primary key not null,
    user_id       BIGINT NOT NULL,
    title        VARCHAR NOT NULL,

    created_date date,
    updated_date date,
    deleted_date date
);
insert into nhahocduong_dentist(id, user_id, title)
values
    (1, 1,'Bác sĩ A'),
    (2, 2,'Bác sĩ B'),
    (3, 3,'Bác sĩ C')
;
alter sequence nhahocduong_dentist_id_seq restart 100;


DROP TABLE if exists NHAHOCDUONG_ORGANIZATION CASCADE ;
CREATE TABLE NHAHOCDUONG_ORGANIZATION
(
    id           BIGSERIAL primary key not null,
    name         VARCHAR NOT NULL,
    code         varchar not null,
    address      varchar not null,
    area_code    varchar not null,
    head_member   bigint references user_user(id),
    parent          bigint references nhahocduong_organization(id),
    type          int,
    classes      jsonb,
    status boolean not null default true,
    created_date timestamp,
    updated_date timestamp,
    created_by varchar,
    updated_by varchar
);

alter table USER_USER add constraint user_organization_fk
    foreign key (organization)
        references nhahocduong_organization (id);

insert into NHAHOCDUONG_ORGANIZATION (id, name, code, address, area_code, classes, type)
VALUES
(1, 'Bệnh viện Răng hàm mặt Trung ương', 'BVRHM', 'Thành phố Hồ Chí Minh', '79', null, 4)
;
-- Dữ liệu thật bắt đầu từ id 10_000 trở đi
alter sequence nhahocduong_organization_id_seq restart 10000;
-- Đổ dữ liệu thật tỉnh Tỉnh Vĩnh Long
insert into NHAHOCDUONG_ORGANIZATION (id, name, code, address, area_code, classes, type)
values
(10000, 'Trường tiểu học Nguyễn Du', 'VL001', 'Tỉnh Vĩnh Long', '86', null, 1),
(10001, 'Trường tiểu học Hùng Vương', 'VL002', 'Tỉnh Vĩnh Long', '86', null, 1),
(10002, 'Trường tiểu học Nguyễn Huệ', 'VL003', 'Tỉnh Vĩnh Long', '86', null, 1),
(10003, 'Trường tiểu học Sư phạm Thực hành', 'VL004', 'Tỉnh Vĩnh Long', '86', null, 1),
(10004, 'Trường tiểu học Lê Lợi', 'VL005', 'Tỉnh Vĩnh Long', '86', null, 1),
(10005, 'Trường tiểu học Trần Đại Nghĩa', 'VL006', 'Tỉnh Vĩnh Long', '86', null, 1),
(10006, 'Trường tiểu học Trần Quốc Tuấn', 'VL007', 'Tỉnh Vĩnh Long', '86', null, 1),
(10007, 'Trường tiểu học Thiềng Đức', 'VL008', 'Tỉnh Vĩnh Long', '86', null, 1),
(10008, 'Trường tiểu học Trương Định', 'VL009', 'Tỉnh Vĩnh Long', '86', null, 1),
(10009, 'Trường tiểu học Chu Văn An', 'VL010', 'Tỉnh Vĩnh Long', '86', null, 1),
(10010, 'Trường tiểu học Phạm Hùng', 'VL011', 'Tỉnh Vĩnh Long', '86', null, 1),
(10011, 'Trường tiểu học Nguyễn Hữu Huân', 'VL012', 'Tỉnh Vĩnh Long', '86', null, 1),
(10012, 'Trường tiểu học Nguyễn Trung Trực', 'VL013', 'Tỉnh Vĩnh Long', '86', null, 1),
(10013, 'Trường tiểu học Lê Hồng Phong', 'VL014', 'Tỉnh Vĩnh Long', '86', null, 1),
(10014, 'Trường tiểu học Lý Thường Kiệt', 'VL015', 'Tỉnh Vĩnh Long', '86', null, 1),
(10015, 'Trường tiểu học An Bình B', 'VL016', 'Tỉnh Vĩnh Long', '86', null, 1),
(10016, 'Trường tiểu học Bình Hòa Phước A', 'VL017', 'Tỉnh Vĩnh Long', '86', null, 1),
(10017, 'Trường tiểu học Trương Văn Ba', 'VL018', 'Tỉnh Vĩnh Long', '86', null, 1),
(10018, 'Trường tiểu học Đồng Phú A', 'VL019', 'Tỉnh Vĩnh Long', '86', null, 1),
(10019, 'Trường tiểu học Long Phước B', 'VL020', 'Tỉnh Vĩnh Long', '86', null, 1),
(10020, 'Trường tiểu học Phước Hậu A', 'VL021', 'Tỉnh Vĩnh Long', '86', null, 1),
(10021, 'Trường tiểu học Phước Hậu C', 'VL022', 'Tỉnh Vĩnh Long', '86', null, 1),
(10022, 'Trường tiểu học Tân Hạnh B', 'VL023', 'Tỉnh Vĩnh Long', '86', null, 1),
(10023, 'Trường tiểu học Tân Hạnh C', 'VL024', 'Tỉnh Vĩnh Long', '86', null, 1),
(10024, 'Trường tiểu học Lộc Hòa A', 'VL025', 'Tỉnh Vĩnh Long', '86', null, 1),
(10025, 'Trường tiểu học Lộc Hòa B', 'VL026', 'Tỉnh Vĩnh Long', '86', null, 1),
(10026, 'Trường tiểu học Hòa Phú A', 'VL027', 'Tỉnh Vĩnh Long', '86', null, 1),
(10027, 'Trường tiểu học Phú Quới A', 'VL028', 'Tỉnh Vĩnh Long', '86', null, 1),
(10028, 'Trường tiểu học Phú Quới C', 'VL029', 'Tỉnh Vĩnh Long', '86', null, 1),
(10029, 'Trường tiểu học Thạnh Quới A', 'VL030', 'Tỉnh Vĩnh Long', '86', null, 1),
(10030, 'Trường tiểu học Long An A', 'VL031', 'Tỉnh Vĩnh Long', '86', null, 1),
(10031, 'Trường tiểu học Phú Đức A', 'VL032', 'Tỉnh Vĩnh Long', '86', null, 1),
(10032, 'Trường tiểu học Phú Đức C', 'VL033', 'Tỉnh Vĩnh Long', '86', null, 1),
(10033, 'Trường tiểu học Thanh Đức B', 'VL034', 'Tỉnh Vĩnh Long', '86', null, 1),
(10034, 'Trường tiểu học Thanh Đức C', 'VL035', 'Tỉnh Vĩnh Long', '86', null, 1),
(10035, 'Trường tiểu học A Thị Trấn LH', 'VL036', 'Tỉnh Vĩnh Long', '86', null, 1),
(10036, 'Trường tiểu học Chánh An A', 'VL037', 'Tỉnh Vĩnh Long', '86', null, 1),
(10037, 'Trường tiểu học An Phước A', 'VL038', 'Tỉnh Vĩnh Long', '86', null, 1),
(10038, 'Trường tiểu học Bình Phước A', 'VL039', 'Tỉnh Vĩnh Long', '86', null, 1),
(10039, 'Trường tiểu học Bình Phước C', 'VL040', 'Tỉnh Vĩnh Long', '86', null, 1),
(10040, 'Trường tiểu học Hòa Tịnh A', 'VL041', 'Tỉnh Vĩnh Long', '86', null, 1),
(10041, 'Trường tiểu học Hòa Tịnh B', 'VL042', 'Tỉnh Vĩnh Long', '86', null, 1),
(10042, 'Trường tiểu học Long Mỹ', 'VL043', 'Tỉnh Vĩnh Long', '86', null, 1),
(10043, 'Trường tiểu học Mỹ An A', 'VL044', 'Tỉnh Vĩnh Long', '86', null, 1),
(10044, 'Trường tiểu học Mỹ An B', 'VL045', 'Tỉnh Vĩnh Long', '86', null, 1),
(10045, 'Trường tiểu học Mỹ Phước A', 'VL046', 'Tỉnh Vĩnh Long', '86', null, 1),
(10046, 'Trường tiểu học Nhơn Phú A', 'VL047', 'Tỉnh Vĩnh Long', '86', null, 1),
(10047, 'Trường tiểu học Nhơn Phú C', 'VL048', 'Tỉnh Vĩnh Long', '86', null, 1),
(10048, 'Trường tiểu học Tân Long Hội A', 'VL049', 'Tỉnh Vĩnh Long', '86', null, 1),
(10049, 'Trường tiểu học Thị Trấn Cái Nhum ', 'VL050', 'Tỉnh Vĩnh Long', '86', null, 1),
(10050, 'Trường tiểu học Chánh Hội A', 'VL051', 'Tỉnh Vĩnh Long', '86', null, 1),
(10051, 'Trường tiểu học Tân An Hội A', 'VL052', 'Tỉnh Vĩnh Long', '86', null, 1),
(10052, 'Trường tiểu học Tân Long A', 'VL053', 'Tỉnh Vĩnh Long', '86', null, 1),
(10053, 'Trường tiểu học Tân An Luông', 'VL054', 'Tỉnh Vĩnh Long', '86', null, 1),
(10054, 'Trường tiểu học Hiếu Phụng A', 'VL055', 'Tỉnh Vĩnh Long', '86', null, 1),
(10055, 'Trường tiểu học Huỳnh Văn Lời', 'VL056', 'Tỉnh Vĩnh Long', '86', null, 1),
(10056, 'Trường tiểu học Nguyễn Văn Quỳ', 'VL057', 'Tỉnh Vĩnh Long', '86', null, 1),
(10057, 'Trường tiểu học Hiếu Nhơn B', 'VL058', 'Tỉnh Vĩnh Long', '86', null, 1),
(10058, 'Trường tiểu học Hiếu Thành', 'VL059', 'Tỉnh Vĩnh Long', '86', null, 1),
(10059, 'Trường tiểu học Đỗ Quang Mười', 'VL060', 'Tỉnh Vĩnh Long', '86', null, 1),
(10060, 'Trường tiểu học Trung An A', 'VL061', 'Tỉnh Vĩnh Long', '86', null, 1),
(10061, 'Trường tiểu học Nguyễn Trung Kiên', 'VL062', 'Tỉnh Vĩnh Long', '86', null, 1),
(10062, 'Trường tiểu học Trung Thành A', 'VL063', 'Tỉnh Vĩnh Long', '86', null, 1),
(10063, 'Trường tiểu học Nguyễn Văn Thời', 'VL064', 'Tỉnh Vĩnh Long', '86', null, 1),
(10064, 'Trường tiểu học Đặng Thị Chính', 'VL065', 'Tỉnh Vĩnh Long', '86', null, 1),
(10065, 'Trường tiểu học Trung Thành Đông A', 'VL066', 'Tỉnh Vĩnh Long', '86', null, 1),
(10066, 'Trường tiểu học Thị trấn Vũng Liêm', 'VL067', 'Tỉnh Vĩnh Long', '86', null, 1),
(10067, 'Trường tiểu học Trung Thành Tây A', 'VL068', 'Tỉnh Vĩnh Long', '86', null, 1),
(10068, 'Trường tiểu học Trung Hiệp A', 'VL069', 'Tỉnh Vĩnh Long', '86', null, 1),
(10069, 'Trường tiểu học Trung Chánh A', 'VL070', 'Tỉnh Vĩnh Long', '86', null, 1),
(10070, 'Trường tiểu học Tân Quới Trung A', 'VL071', 'Tỉnh Vĩnh Long', '86', null, 1),
(10071, 'Trường tiểu học Nguyễn Văn Kết', 'VL072', 'Tỉnh Vĩnh Long', '86', null, 1),
(10072, 'Trường tiểu học Lê Văn Cư', 'VL073', 'Tỉnh Vĩnh Long', '86', null, 1),
(10073, 'Trường tiểu học Võ Ngọc Tốt', 'VL074', 'Tỉnh Vĩnh Long', '86', null, 1),
(10074, 'Trường tiểu học Thanh Bình B', 'VL075', 'Tỉnh Vĩnh Long', '86', null, 1),
(10075, 'Trường tiểu học  Lưu Văn Liệt', 'VL076', 'Tỉnh Vĩnh Long', '86', null, 1),
(10076, 'Trường tiểu học Tường Lộc A', 'VL077', 'Tỉnh Vĩnh Long', '86', null, 1),
(10077, 'Trường tiểu học Tường Lộc B', 'VL078', 'Tỉnh Vĩnh Long', '86', null, 1),
(10078, 'Trường tiểu học Mỹ Thạnh Trung A', 'VL079', 'Tỉnh Vĩnh Long', '86', null, 1),
(10079, 'Trường tiểu học Mỹ Thạnh Trung B', 'VL080', 'Tỉnh Vĩnh Long', '86', null, 1),
(10080, 'Trường tiểu học Hòa Lộc A', 'VL081', 'Tỉnh Vĩnh Long', '86', null, 1),
(10081, 'Trường tiểu học Hòa Lộc B', 'VL082', 'Tỉnh Vĩnh Long', '86', null, 1),
(10082, 'Trường tiểu học Hòa Hiệp', 'VL083', 'Tỉnh Vĩnh Long', '86', null, 1),
(10083, 'Trường tiểu học Hòa Thạnh', 'VL084', 'Tỉnh Vĩnh Long', '86', null, 1),
(10084, 'Trường tiểu học Hậu Lộc', 'VL085', 'Tỉnh Vĩnh Long', '86', null, 1),
(10085, 'Trường tiểu học Tân Lộc', 'VL086', 'Tỉnh Vĩnh Long', '86', null, 1),
(10086, 'Trường tiểu học Phú Lộc', 'VL087', 'Tỉnh Vĩnh Long', '86', null, 1),
(10087, 'Trường tiểu học Cái Ngang', 'VL088', 'Tỉnh Vĩnh Long', '86', null, 1),
(10088, 'Trường tiểu học Mỹ Lộc', 'VL089', 'Tỉnh Vĩnh Long', '86', null, 1),
(10089, 'Trường tiểu học Song Phú A', 'VL090', 'Tỉnh Vĩnh Long', '86', null, 1),
(10090, 'Trường tiểu học Song Phú B', 'VL091', 'Tỉnh Vĩnh Long', '86', null, 1),
(10091, 'Trường tiểu học Tô Hùng Vĩ', 'VL092', 'Tỉnh Vĩnh Long', '86', null, 1),
(10092, 'Trường tiểu học Long Phú', 'VL093', 'Tỉnh Vĩnh Long', '86', null, 1),
(10093, 'Trường tiểu học Phú Thịnh A', 'VL094', 'Tỉnh Vĩnh Long', '86', null, 1),
(10094, 'Trường tiểu học Phú Thịnh B', 'VL095', 'Tỉnh Vĩnh Long', '86', null, 1),
(10095, 'Trường tiểu học Bình Ninh', 'VL096', 'Tỉnh Vĩnh Long', '86', null, 1),
(10096, 'Trường tiểu học Ngãi Tứ A', 'VL097', 'Tỉnh Vĩnh Long', '86', null, 1),
(10097, 'Trường tiểu học Ngãi Tứ B', 'VL098', 'Tỉnh Vĩnh Long', '86', null, 1),
(10098, 'Trường tiểu học Loan Mỹ B', 'VL099', 'Tỉnh Vĩnh Long', '86', null, 1),
(10099, 'Trường tiểu học Thạch Thia', 'VL100', 'Tỉnh Vĩnh Long', '86', null, 1),
(10100, 'Trường tiểu học Hòa Bình A', 'VL101', 'Tỉnh Vĩnh Long', '86', null, 1),
(10101, 'Trường tiểu học Hòa Bình C', 'VL102', 'Tỉnh Vĩnh Long', '86', null, 1),
(10102, 'Trường tiểu học Hựu Thành A', 'VL103', 'Tỉnh Vĩnh Long', '86', null, 1),
(10103, 'Trường tiểu học Hựu Thành B', 'VL104', 'Tỉnh Vĩnh Long', '86', null, 1),
(10104, 'Trường tiểu học Lục Sĩ Thành A', 'VL105', 'Tỉnh Vĩnh Long', '86', null, 1),
(10105, 'Trường tiểu học Phú Thành', 'VL106', 'Tỉnh Vĩnh Long', '86', null, 1),
(10106, 'Trường tiểu học Tân Mỹ A', 'VL107', 'Tỉnh Vĩnh Long', '86', null, 1),
(10107, 'Trường tiểu học Tân Mỹ B', 'VL108', 'Tỉnh Vĩnh Long', '86', null, 1),
(10108, 'Trường tiểu học Trà Côn A', 'VL109', 'Tỉnh Vĩnh Long', '86', null, 1),
(10109, 'Trường tiểu học Trà Côn C', 'VL110', 'Tỉnh Vĩnh Long', '86', null, 1),
(10110, 'Trường tiểu học Thới Hòa A', 'VL111', 'Tỉnh Vĩnh Long', '86', null, 1),
(10111, 'Trường tiểu học Thới Hòa B', 'VL112', 'Tỉnh Vĩnh Long', '86', null, 1),
(10112, 'Trường tiểu học Thuận Thới A', 'VL113', 'Tỉnh Vĩnh Long', '86', null, 1),
(10113, 'Trường tiểu học Thuận Thới B', 'VL114', 'Tỉnh Vĩnh Long', '86', null, 1),
(10114, 'Trường tiểu học Tích Thiện A', 'VL115', 'Tỉnh Vĩnh Long', '86', null, 1),
(10115, 'Trường tiểu học Thiện Mỹ A', 'VL116', 'Tỉnh Vĩnh Long', '86', null, 1),
(10116, 'Trường tiểu học Vĩnh Xuân', 'VL117', 'Tỉnh Vĩnh Long', '86', null, 1),
(10117, 'Trường tiểu học Thị Trấn Trà Ôn', 'VL118', 'Tỉnh Vĩnh Long', '86', null, 1),
(10118, 'Trường tiểu học Nhơn Bình A', 'VL119', 'Tỉnh Vĩnh Long', '86', null, 1),
(10119, 'Trường tiểu học Nhơn Bình B', 'VL120', 'Tỉnh Vĩnh Long', '86', null, 1),
(10120, 'Trường tiểu học Xuân Hiệp A', 'VL121', 'Tỉnh Vĩnh Long', '86', null, 1),
(10121, 'Trường tiểu học Xuân Hiệp B', 'VL122', 'Tỉnh Vĩnh Long', '86', null, 1),
(10122, 'Trường tiểu học Lê Thánh Tông', 'VL123', 'Tỉnh Vĩnh Long', '86', null, 1),
(10123, 'Trường tiểu học Phan Bội Châu', 'VL124', 'Tỉnh Vĩnh Long', '86', null, 1),
(10124, 'Trường tiểu học Trần Bình Trọng', 'VL125', 'Tỉnh Vĩnh Long', '86', null, 1),
(10125, 'Trường tiểu học Mỹ Hòa C', 'VL126', 'Tỉnh Vĩnh Long', '86', null, 1),
(10126, 'Trường tiểu học Nguyễn Văn Trỗi', 'VL127', 'Tỉnh Vĩnh Long', '86', null, 1),
(10127, 'Trường tiểu học Lý Thường Kiệt', 'VL128', 'Tỉnh Vĩnh Long', '86', null, 1),
(10128, 'Trường tiểu học Võ Thị Sáu', 'VL129', 'Tỉnh Vĩnh Long', '86', null, 1),
(10129, 'Trường tiểu học Nguyễn Thị Minh Khai', 'VL130', 'Tỉnh Vĩnh Long', '86', null, 1),
(10130, 'Trường tiểu học Phù Ly', 'VL131', 'Tỉnh Vĩnh Long', '86', null, 1),
(10131, 'Trường tiểu học Phan Văn Năm', 'VL132', 'Tỉnh Vĩnh Long', '86', null, 1),
(10132, 'Trường tiểu học Thoại Ngọc Hầu', 'VL133', 'Tỉnh Vĩnh Long', '86', null, 1),
(10133, 'Trường tiểu học Phan Văn Đáng', 'VL134', 'Tỉnh Vĩnh Long', '86', null, 1),
(10134, 'Trường tiểu học Tân Hưng', 'VL135', 'Tỉnh Vĩnh Long', '86', null, 1),
(10135, 'Trường tiểu học Tân An Thạnh A', 'VL136', 'Tỉnh Vĩnh Long', '86', null, 1),
(10136, 'Trường tiểu học Tân An Thạnh B', 'VL137', 'Tỉnh Vĩnh Long', '86', null, 1),
(10137, 'Trường tiểu học Tân Lược', 'VL138', 'Tỉnh Vĩnh Long', '86', null, 1),
(10138, 'Trường tiểu học Tân Bình', 'VL139', 'Tỉnh Vĩnh Long', '86', null, 1),
(10139, 'Trường tiểu học Tân Thành A', 'VL140', 'Tỉnh Vĩnh Long', '86', null, 1),
(10140, 'Trường tiểu học Tân Quới A', 'VL141', 'Tỉnh Vĩnh Long', '86', null, 1),
(10141, 'Trường tiểu học Thành Đông A', 'VL142', 'Tỉnh Vĩnh Long', '86', null, 1),
(10142, 'Trường tiểu học Thành Lợi A', 'VL143', 'Tỉnh Vĩnh Long', '86', null, 1),
(10143, 'Trường tiểu học Thành Lợi C', 'VL144', 'Tỉnh Vĩnh Long', '86', null, 1),
(10144, 'Trường tiểu học Thành Trung A', 'VL145', 'Tỉnh Vĩnh Long', '86', null, 1),
(10145, 'Trường tiểu học Thành Trung B', 'VL146', 'Tỉnh Vĩnh Long', '86', null, 1),
(10146, 'Trường tiểu học Mỹ Thuận A', 'VL147', 'Tỉnh Vĩnh Long', '86', null, 1),
(10147, 'Trường tiểu học Mỹ Thuận B', 'VL148', 'Tỉnh Vĩnh Long', '86', null, 1),
(10148, 'Trường tiểu học Nguyễn Văn Thảnh A', 'VL149', 'Tỉnh Vĩnh Long', '86', null, 1),
(10149, 'Mầm non Vĩnh Xuân', 'VL150', 'Tỉnh Vĩnh Long', '86', null, 1),
(10150, 'Mầm Non Hựu Thành', 'VL151', 'Tỉnh Vĩnh Long', '86', null, 1),
(10151, 'Mầm Non Thới Hòa', 'VL152', 'Tỉnh Vĩnh Long', '86', null, 1),
(10152, 'Mầm non Trà Côn', 'VL153', 'Tỉnh Vĩnh Long', '86', null, 1),
(10153, 'Mầm non Thiện Mỹ', 'VL154', 'Tỉnh Vĩnh Long', '86', null, 1),
(10154, 'Mầm non Tích Thiện', 'VL155', 'Tỉnh Vĩnh Long', '86', null, 1),
(10155, 'Mầm Non Tuổi Thơ (Tân Quới)', 'VL156', 'Tỉnh Vĩnh Long', '86', null, 1),
(10156, 'Mẫu giáo Hoa Mai', 'VL157', 'Tỉnh Vĩnh Long', '86', null, 1),
(10157, 'Mẫu giáo Măng Non', 'VL158', 'Tỉnh Vĩnh Long', '86', null, 1),
(10158, 'Mầm non Mỹ Thuận', 'VL159', 'Tỉnh Vĩnh Long', '86', null, 1),
(10159, 'Mầm Non Nguyễn Văn Thảnh', 'VL160', 'Tỉnh Vĩnh Long', '86', null, 1),
(10160, 'Mầm non Hoa Phượng', 'VL161', 'Tỉnh Vĩnh Long', '86', null, 1),
(10161, 'Mẫu giáo Hướng Dương', 'VL162', 'Tỉnh Vĩnh Long', '86', null, 1),
(10162, 'Mầm non Tân Hưng', 'VL163', 'Tỉnh Vĩnh Long', '86', null, 1),
(10163, 'Mầm non Thành Trung', 'VL164', 'Tỉnh Vĩnh Long', '86', null, 1),
(10164, 'Mầm non Tân Thành', 'VL165', 'Tỉnh Vĩnh Long', '86', null, 1),
(10165, 'Mầm non Tuổi Thơ', 'VL166', 'Tỉnh Vĩnh Long', '86', null, 1),
(10166, 'Mầm non Sơn Ca (Tân Lược)', 'VL167', 'Tỉnh Vĩnh Long', '86', null, 1),
(10167, 'Mầm non Thực hành Măng non Phường 9', 'VL168', 'Tỉnh Vĩnh Long', '86', null, 1),
(10168, 'Mầm non Bé By Ngoan', 'VL169', 'Tỉnh Vĩnh Long', '86', null, 1),
(10169, 'Mầm non Hoa Sen Thành Phố Tỉnh Vĩnh Long', 'VL170', 'Tỉnh Vĩnh Long', '86', null, 1),
(10170, 'Nhà trẻ Trinh Vương', 'VL171', 'Tỉnh Vĩnh Long', '86', null, 1),
(10171, 'Nhóm trẻ Gia Bảo', 'VL172', 'Tỉnh Vĩnh Long', '86', null, 1),
(10172, 'Mầm non Phú Đức', 'VL173', 'Tỉnh Vĩnh Long', '86', null, 1),
(10173, 'Mầm Non Thanh Đức', 'VL174', 'Tỉnh Vĩnh Long', '86', null, 1),
(10174, 'Mầm non An Bình', 'VL175', 'Tỉnh Vĩnh Long', '86', null, 1),
(10175, 'Mầm non Họa Mi thị trấn Long Hồ', 'VL176', 'Tỉnh Vĩnh Long', '86', null, 1),
(10176, 'Mầm non Bình Hòa Phước', 'VL177', 'Tỉnh Vĩnh Long', '86', null, 1),
(10177, 'Mầm non Đồng Phú', 'VL178', 'Tỉnh Vĩnh Long', '86', null, 1),
(10178, 'Mầm non Long Phước', 'VL179', 'Tỉnh Vĩnh Long', '86', null, 1),
(10179, 'Mẫu giáo Hoa Hồng Lộc Hòa', 'VL180', 'Tỉnh Vĩnh Long', '86', null, 1),
(10180, 'Mẫu giáo Phú Quới', 'VL181', 'Tỉnh Vĩnh Long', '86', null, 1),
(10181, 'Mầm non Phước Hậu', 'VL182', 'Tỉnh Vĩnh Long', '86', null, 1),
(10182, 'Mầm non  Long An', 'VL183', 'Tỉnh Vĩnh Long', '86', null, 1),
(10183, 'Mầm Non Hòa Ninh', 'VL184', 'Tỉnh Vĩnh Long', '86', null, 1),
(10184, 'Mầm non Thạnh Quới', 'VL185', 'Tỉnh Vĩnh Long', '86', null, 1),
(10185, 'Mầm Non Thị trấn Long Hồ', 'VL186', 'Tỉnh Vĩnh Long', '86', null, 1),
(10186, 'Mầm non Lộc Hòa', 'VL187', 'Tỉnh Vĩnh Long', '86', null, 1),
(10187, 'Mầm non Tân Hạnh', 'VL188', 'Tỉnh Vĩnh Long', '86', null, 1),
(10188, 'Mầm non Hòa Phú', 'VL189', 'Tỉnh Vĩnh Long', '86', null, 1),
(10189, 'Mầm non khu công nghiệp Hòa Phú', 'VL190', 'Tỉnh Vĩnh Long', '86', null, 1),
(10190, 'Mầm non Hồng Ân', 'VL191', 'Tỉnh Vĩnh Long', '86', null, 1),
(10191, 'Mầm non Họa Mi Hòa Phú', 'VL192', 'Tỉnh Vĩnh Long', '86', null, 1),
(10192, 'Mẫu giáo Măng Non I (Mỹ Phước)', 'VL193', 'Tỉnh Vĩnh Long', '86', null, 1),
(10193, 'Mầm non Măng Non II', 'VL194', 'Tỉnh Vĩnh Long', '86', null, 1),
(10194, 'Mầm non Oanh Vũ I', 'VL195', 'Tỉnh Vĩnh Long', '86', null, 1),
(10195, 'Mầm Non Oanh Vũ II (Tân Long Hội)', 'VL196', 'Tỉnh Vĩnh Long', '86', null, 1),
(10196, 'Mầm non Sơn Ca I (Hòa Tịnh)', 'VL197', 'Tỉnh Vĩnh Long', '86', null, 1),
(10197, 'Mầm non Sơn Ca I (Bình Phước)', 'VL198', 'Tỉnh Vĩnh Long', '86', null, 1),
(10198, 'Mầm non Tuổi Thơ I', 'VL199', 'Tỉnh Vĩnh Long', '86', null, 1),
(10199, 'Mầm non Tuổi Thơ II (An Phước)', 'VL200', 'Tỉnh Vĩnh Long', '86', null, 1),
(10200, 'Mầm non Tuổi Thơ III (Nhơn Phú)', 'VL201', 'Tỉnh Vĩnh Long', '86', null, 1),
(10201, 'Mầm non Tuổi Thơ IV (Chánh An)', 'VL202', 'Tỉnh Vĩnh Long', '86', null, 1),
(10202, 'Mầm Non Sơn Ca III', 'VL203', 'Tỉnh Vĩnh Long', '86', null, 1),
(10203, 'Mầm Non Oanh Vũ III (Tân An Hội)', 'VL204', 'Tỉnh Vĩnh Long', '86', null, 1),
(10204, 'Mầm Non Thị Trấn Cái Nhum', 'VL205', 'Tỉnh Vĩnh Long', '86', null, 1),
(10205, 'Mầm Non Hiếu Phụng', 'VL206', 'Tỉnh Vĩnh Long', '86', null, 1),
(10206, 'Mầm non Hiếu Nhơn', 'VL207', 'Tỉnh Vĩnh Long', '86', null, 1),
(10207, 'Mẫu giáo Hiếu Thành', 'VL208', 'Tỉnh Vĩnh Long', '86', null, 1),
(10208, 'Mầm Non Trung Hiếu', 'VL209', 'Tỉnh Vĩnh Long', '86', null, 1),
(10209, 'Mẫu giáo Trung Thành', 'VL210', 'Tỉnh Vĩnh Long', '86', null, 1),
(10210, 'Mẫu giáo Trung Nghĩa', 'VL211', 'Tỉnh Vĩnh Long', '86', null, 1),
(10211, 'Mẫu giáo Trung Hiệp', 'VL212', 'Tỉnh Vĩnh Long', '86', null, 1),
(10212, 'Mẫu giáo Tân Quới Trung', 'VL213', 'Tỉnh Vĩnh Long', '86', null, 1),
(10213, 'Mầm non Quới An', 'VL214', 'Tỉnh Vĩnh Long', '86', null, 1),
(10214, 'Mẫu giáo Quới Thiện', 'VL215', 'Tỉnh Vĩnh Long', '86', null, 1),
(10215, 'Mầm Non Thanh Bình', 'VL216', 'Tỉnh Vĩnh Long', '86', null, 1),
(10216, 'Mầm non Trung An', 'VL217', 'Tỉnh Vĩnh Long', '86', null, 1),
(10217, 'Mầm non Trịnh Liên Hoa', 'VL218', 'Tỉnh Vĩnh Long', '86', null, 1),
(10218, 'Mầm Non Trung Thành Tây', 'VL219', 'Tỉnh Vĩnh Long', '86', null, 1),
(10219, 'Mẫu giáo Trung Chánh', 'VL220', 'Tỉnh Vĩnh Long', '86', null, 1),
(10220, 'Mầm Non Hiếu Nghĩa', 'VL221', 'Tỉnh Vĩnh Long', '86', null, 1),
(10221, 'Mầm non Tân An Luông', 'VL222', 'Tỉnh Vĩnh Long', '86', null, 1),
(10222, 'Mầm non Thị Trấn Vũng Liêm', 'VL223', 'Tỉnh Vĩnh Long', '86', null, 1),
(10223, 'Mầm non Sơn Ca', 'VL224', 'Tỉnh Vĩnh Long', '86', null, 1),
(10224, 'Mầm Non Mỹ Lộc', 'VL225', 'Tỉnh Vĩnh Long', '86', null, 1),
(10225, 'Mầm non Bông Sen', 'VL226', 'Tỉnh Vĩnh Long', '86', null, 1),
(10226, 'Mẫu giáo Hoa Đào', 'VL227', 'Tỉnh Vĩnh Long', '86', null, 1),
(10227, 'Mầm non Họa Mi', 'VL228', 'Tỉnh Vĩnh Long', '86', null, 1),
(10228, 'Mầm non Tuổi Xanh', 'VL229', 'Tỉnh Vĩnh Long', '86', null, 1),
(10229, 'Mầm non Hoa Hồng', 'VL230', 'Tỉnh Vĩnh Long', '86', null, 1),
(10230, 'Mầm non Hoa Sen', 'VL231', 'Tỉnh Vĩnh Long', '86', null, 1),
(10231, 'Mầm non Măng Non', 'VL232', 'Tỉnh Vĩnh Long', '86', null, 1),
(10232, 'Mầm non Tuổi Thơ', 'VL233', 'Tỉnh Vĩnh Long', '86', null, 1),
(10233, 'Mầm non Hoa Lan', 'VL234', 'Tỉnh Vĩnh Long', '86', null, 1),
(10234, 'Mầm non Vành Khuyên', 'VL235', 'Tỉnh Vĩnh Long', '86', null, 1),
(10235, 'Mầm non Hướng Dương', 'VL236', 'Tỉnh Vĩnh Long', '86', null, 1),
(10236, 'Mầm non Kim Đồng', 'VL237', 'Tỉnh Vĩnh Long', '86', null, 1),
(10237, 'Mầm non Sao Mai', 'VL238', 'Tỉnh Vĩnh Long', '86', null, 1),
(10238, 'Mầm non Hoa Mai', 'VL239', 'Tỉnh Vĩnh Long', '86', null, 1),
(10239, 'Mầm Non Rạng Đông', 'VL240', 'Tỉnh Vĩnh Long', '86', null, 1),
(10240, 'Mầm Non Cái Ngang', 'VL241', 'Tỉnh Vĩnh Long', '86', null, 1),
(10241, 'Mầm non Hoa Sen (Đ.Bình)', 'VL242', 'Tỉnh Vĩnh Long', '86', null, 1),
(10242, 'Mầm non Mỹ Hòa', 'VL243', 'Tỉnh Vĩnh Long', '86', null, 1),
(10243, 'Mầm Non Hoa Lan', 'VL244', 'Tỉnh Vĩnh Long', '86', null, 1),
(10244, 'Mầm Non Khai Trí', 'VL245', 'Tỉnh Vĩnh Long', '86', null, 1),
(10245, 'Mầm non Hoa Hồng', 'VL246', 'Tỉnh Vĩnh Long', '86', null, 1),
(10246, 'Mầm non Họa Mi (Thuận An)', 'VL247', 'Tỉnh Vĩnh Long', '86', null, 1),
(10247, 'Mầm non Hoa Hồng 2', 'VL248', 'Tỉnh Vĩnh Long', '86', null, 1),
(10248, 'Mầm non Sao Mai (Đông Thành)', 'VL249', 'Tỉnh Vĩnh Long', '86', null, 1),
(10249, 'Mầm non Sen Hồng', 'VL250', 'Tỉnh Vĩnh Long', '86', null, 1),
(10250, 'Mầm non Đông Thạnh', 'VL251', 'Tỉnh Vĩnh Long', '86', null, 1),
(10251, 'Mầm non Bình Minh', 'VL252', 'Tỉnh Vĩnh Long', '86', null, 1),
(10252, 'Mầm Non Hoàng Lam', 'VL253', 'Tỉnh Vĩnh Long', '86', null, 1),
(10253, 'Mầm Non Thuận Thới', 'VL254', 'Tỉnh Vĩnh Long', '86', null, 1),
(10254, 'Mầm Non Xuân Hiệp', 'VL255', 'Tỉnh Vĩnh Long', '86', null, 1),
(10255, 'Mầm Non Hòa Bình', 'VL256', 'Tỉnh Vĩnh Long', '86', null, 1),
(10256, 'Mầm Non Lục Sĩ Thành', 'VL257', 'Tỉnh Vĩnh Long', '86', null, 1),
(10257, 'Mầm non Tân Mỹ', 'VL258', 'Tỉnh Vĩnh Long', '86', null, 1),
(10258, 'Mầm Non Nhơn Bình', 'VL259', 'Tỉnh Vĩnh Long', '86', null, 1),
(10259, 'Mầm Non Phú Thành', 'VL260', 'Tỉnh Vĩnh Long', '86', null, 1),
(10260, 'Mầm non Ánh Dương Thị trấn Trà Ôn', 'VL261', 'Tỉnh Vĩnh Long', '86', null, 1),
(10261, 'Mầm non Vĩnh Xuân', 'VL262', 'Tỉnh Vĩnh Long', '86', null, 1),
(10262, 'Mầm Non Hựu Thành', 'VL263', 'Tỉnh Vĩnh Long', '86', null, 1),
(10263, 'Mầm Non Thới Hòa', 'VL264', 'Tỉnh Vĩnh Long', '86', null, 1),
(10264, 'Mầm non Trà Côn', 'VL265', 'Tỉnh Vĩnh Long', '86', null, 1),
(10265, 'Mầm non Thiện Mỹ', 'VL266', 'Tỉnh Vĩnh Long', '86', null, 1),
(10266, 'Mầm non Tích Thiện', 'VL267', 'Tỉnh Vĩnh Long', '86', null, 1),
(10267, 'Mầm Non Tuổi Thơ (Tân Quới)', 'VL268', 'Tỉnh Vĩnh Long', '86', null, 1),
(10268, 'Mẫu giáo Hoa Mai', 'VL269', 'Tỉnh Vĩnh Long', '86', null, 1),
(10269, 'Mẫu giáo Măng Non', 'VL270', 'Tỉnh Vĩnh Long', '86', null, 1),
(10270, 'Mầm non Mỹ Thuận', 'VL271', 'Tỉnh Vĩnh Long', '86', null, 1),
(10271, 'Mầm Non Nguyễn Văn Thảnh', 'VL272', 'Tỉnh Vĩnh Long', '86', null, 1),
(10272, 'Mầm non Hoa Phượng', 'VL273', 'Tỉnh Vĩnh Long', '86', null, 1),
(10273, 'Mẫu giáo Hướng Dương', 'VL274', 'Tỉnh Vĩnh Long', '86', null, 1),
(10274, 'Mầm non Tân Hưng', 'VL275', 'Tỉnh Vĩnh Long', '86', null, 1),
(10275, 'Mầm non Thành Trung', 'VL276', 'Tỉnh Vĩnh Long', '86', null, 1),
(10276, 'Mầm non Tân Thành', 'VL277', 'Tỉnh Vĩnh Long', '86', null, 1),
(10277, 'Mầm non Tuổi Thơ', 'VL278', 'Tỉnh Vĩnh Long', '86', null, 1),
(10278, 'Mầm non Sơn Ca (Tân Lược)', 'VL279', 'Tỉnh Vĩnh Long', '86', null, 1),
(10279, 'Trường TH Thị trấn Chợ Lách', '083001', 'Tỉnh Bến Tre', '83', null, 1),
(10280, 'Trường TH Thị trấn Giồng Trôm', '083002', 'Tỉnh Bến Tre', '83', null, 1),
(10281, 'Trường TH Chu Văn An', '083003', 'Tỉnh Bến Tre', '83', null, 1),
(10282, 'Trường TH Trần Hoàn Vũ', '083004', 'Tỉnh Bến Tre', '83', null, 1),
(10283, 'Trường TH Tân Bình', '083005', 'Tỉnh Bến Tre', '83', null, 1),
(10284, 'Trường TH Tân Thạch A', '083006', 'Tỉnh Bến Tre', '83', null, 1),
(10285, 'Trường TH An Thới', '083007', 'Tỉnh Bến Tre', '83', null, 1),
(10286, 'Trường TH Thị trấn Thạnh Phú', '083008', 'Tỉnh Bến Tre', '83', null, 1),
(10287, 'Trường TH An Bình Tây', '083009', 'Tỉnh Bến Tre', '83', null, 1)
;
-- Dữ liệu thật sau khi đổ dữ liệu Tỉnh Vĩnh Long bắt đầu từ id 10_288 trở đi
alter sequence nhahocduong_organization_id_seq restart 10288;

DROP TABLE if exists NHAHOCDUONG_PATIENT cascade ;
create table NHAHOCDUONG_PATIENT(
                                    id  bigserial primary key not null,
                                    full_name varchar not null,
                                    health_insurance_number varchar,
                                    gender integer not null,
                                    birthdate date not null,
                                    ethnic varchar not null,
                                    code varchar not null,
                                    area_type varchar,
                                    phone_number varchar,
                                    address_line varchar,
                                    school_class varchar,
                                    national_id_num varchar,
                                    care_taker varchar,
                                    organization bigint references nhahocduong_organization(id),
                                    status boolean not null default true
);
insert into nhahocduong_patient (id, full_name, health_insurance_number, gender, birthdate, ethnic, area_type, phone_number, address_line, school_class, national_id_num, care_taker, organization)
values

(175, 'Nguyễn Văn Sỹ', 'DN5797939589580', 1, '2023-05-28', '0', 'Nông thôn', '09090901234', 'Bắc Ninh', '2A1', '061079013187777', 'Nguyễn Văn Thủy', 3),
(178, 'Nguyễn Tuấn Minh', '1awwr', 2, '2023-05-29', '', 'Thành thị', '09090901234', 'Hà Nội', '1A1', '1234', 'nghh', 3),
(119, 'Phan Huy Khang', 'DN5797915126119', 1, '2014-01-01', 'Kinh', 'Thành thị', '0966934212', '', '3A1', '064029013138', 'Phan Khang Huy', 1),
(149, 'Nguyễn Sỹ Hải', 'DN5797935812845', 1, '2014-02-11', 'Kinh', 'Ngoại ô', '0969737185', '', '3A1', '061089013182', '', 4),
(121, 'Đặng Thị Mỹ Dung', 'DN5797911454271', 2, '2014-06-15', 'Kinh', 'Thành thị', '0932790158', null, '3A1', '071089013185', null, 1),
(144, 'Phạm Văn Hải', 'DN5797916279377', 1, '2014-11-30', 'Kinh', 'Ngoại ô', '0345625588', '', '3A1', '064029016187', 'nhupkj0', 7),
(182, 'Đặng Trung Nghĩaa', 'sv1221214353650', 1, '2015-06-01', '0', 'Ngoại ô', '09090901234', 'Tây Ninh', '1A2', '0302555554540', 'Đặng Giám Hộ', 5),
(101, 'Phan Huy Ích', 'DN5797915311385', 1, '2015-01-01', 'Kinh', 'Thành thị', '0375728325', '280 Cách Mạng Tháng 8, Phường 12, Quận 10, TP.HCM', '2A1', '064029013182', 'Phan Khang Huy', 1),
(141, 'Phan Văn Hải', 'DN5797933625648', 1, '2015-01-01', 'Kinh', 'Ngoại ô', '0355155531', '', '2A2', '064029013185', 'Phan Khang Huyy', 1),
(3, 'Lê Danh Pha', 'DN5797936457968', 1, '2015-01-20', 'Kinh', 'Nông thôn', '0367717714', null, '2A1', '061029013188', null, 3),
(159, 'Hồ Văn Hải', 'DN5790205089639', 1, '2015-02-01', 'Kinh', 'Thành thị', '0335945634', '', '2A1', '061029011191', '', 3),
(154, 'Nguyễn Thị Vân', 'DN5797934814868', 2, '2015-06-07', 'Kinh', 'Ngoại ô', '0393189216', 'TP HCM', '2A2', '063089013186', 'ae', 3),
(162, 'Nguyễn Thị Ngọc Ánh', 'DN5790038205689', 2, '2015-06-07', 'Kinh', 'Ngoại ô', '0987614395', '', '2A2', '061089013191', 'qwe', 1),
(110, 'Nguyễn Lê Phan', 'DN5797937465398', 1, '2015-06-16', 'Kinh', 'Ngoại ô', '0357900960', null, '2A3', '061059013190', null, 4),
(126, 'Nguyễn Trần Anh Tú', 'DN5797914443703', 1, '2015-10-30', 'Kinh', 'Nông thôn', '0359414455', null, '2A3', '063089013389', null, 5),
(102, 'Phan Văn Đăng', 'DN5797911017992', 1, '2016-01-01', 'Kinh', 'Ngoại ô', '0865866619', null, '1A2', '064029013586', null, 1),
(131, 'Phan Huy Khang', 'DN5797911178482', 1, '2016-01-01', 'Kinh', 'Thành thị', '0382145484', null, '1A2', '064029013160', null, 1),
(158, 'Nguyễn Sỹ Hải', 'DN5797939470581', 1, '2016-01-01', 'Kinh', 'Ngoại ô', '0979855397', 'TP HCM', '1A2', '061089013185', 'Nguyen Van Toan', 1),
(172, 'Phạm Trường Quân', 'DN5797912359603', 1, '2016-05-30', 'Kinh', 'Ngoại ô', '09090901234', 'Bình Định', '1A1', '064029014189', '', 6),
(167, 'Nguyễn Tuấn Tú', 'DN5797933636011', 1, '2016-06-01', 'Kinh', 'Ngoại ô', '0335042469', 'TP HCM', '1A1', '063089023188', '', 1),
(173, 'Lê Thị Nhã Phương', 'DN5797916132525', 2, '2016-06-01', 'Kinh', 'Thành thị', '0983089241', 'TP HCM', '1A1', '061039013184', 'nghh', 1),
(168, 'Nguyễn Tuấn Hải', 'DN5797916122500', 1, '2016-06-02', 'Kinh', 'Ngoại ô', '0964800230', 'TP HCM', '1A1', '063089013191', 'ngh', 1),
(1, 'Nguyễn Văn Lạc', 'DN5797910626461', 1, '2016-11-11', 'Kinh', 'Nông thôn', '0365720169', null, '1A3', '063089043190', null, 1),
(2, 'Nguyễn Thị Thủy', 'DN5797910926420', 2, '2016-12-11', 'Kinh', 'Ngoại ô', '0399971111', null, '1A2', '063089003395', null, 1),
(169, 'Lê Văn Viễn', 'DN5798022703183', 1, '2016-06-02', 'Kinh', 'Nông thôn', '0327993479', '', '1A1', '061039013186', 'nghh', 1),
(166, 'Nguyễn Tuấn Minh', 'DN5797910126460', 1, '2016-06-08', 'Kinh', 'Nông thôn', '0359733643', '', '1A3', '063089023193', '', 5),
(161, 'Đặng Trần Khánh', 'DN5797910326660', 1, '2016-06-09', 'Kinh', 'Thành thị', '0366044857', '', '1A3', '', '', 1),
(171, 'Hồ Thị Tường Vy', 'DN5797910826460', 2, '2016-06-10', 'Kinh', 'Thành thị', '0938324834', 'TP HCM', '1A1', '041089013189', '', 1),
(157, 'Nguyễn Văn Đăng', 'DN5797910624460', 1, '2016-06-15', 'Kinh', 'Thành thị', '0345996073', 'Hà Nội', '1A1', '064024013191', 'addddd', 1),
(156, 'Phạm Văn Đồng', 'DN5797910426420', 1, '2016-06-16', 'Kinh', 'Ngoại ô', '0978542629', 'Đà Nẵng', '1A2', '071089013183', '', 7),
(163, 'Nguyễn Văn Kha', 'DN5797910525460', 1, '2016-07-08', 'Kinh', 'Ngoại ô', '0968069651', 'TP HCM', '1A3', '063089033194', 'Nguyễn Văn Khá', 7),
(160, 'Nguyễn Văn Sỹ', 'DN5797910632460', 1, '2016-08-08', 'Kinh', 'Thành thị', '0367048333', '', '1A2', '063089053192', '', 5),
(122, 'Nguyễn Trần Anh Thơ', 'DN5797910426460', 1, '2016-11-11', 'Kinh', 'Nông thôn', '0979791765', null, '1A1', '063089012187', null, 7),
(147, 'Lê Công Pha', 'DN5797910826663', 1, '2016-11-11', 'Kinh', 'Nông thôn', '0388941310', null, '1A2', '061029013184', null, 1),
(151, 'Lê Phan', 'DN5797910226663', 1, '2016-11-11', 'Kinh', 'Nông thôn', '0332146609', 'Tuyên Quang', '1A1', '061029013189', '213213', 7),
(155, 'Hoàng Đôn Thiện Hiếu', 'DN5797910326420', 1, '2016-06-16', 'Kinh', 'Thành thị', '09090901234', '', '1A1', '041089013187', '', 1),
(170, 'Lê Quang Tấn', 'DN5797910926421', 1, '2016-11-11', 'Kinh', 'Ngoại ô', '09090901234', 'Tỉnh Vĩnh Long', '1A1', '061029013383', 'adad', 4)
;
alter sequence nhahocduong_patient_id_seq restart 1000;

-- DROP TABLE if exists NHAHOCDUONG_TEETH_CONDITION cascade ;
-- create table NHAHOCDUONG_TEETH_CONDITION(
--     id bigint primary key not null,
--     label varchar not null,
--     description varchar not null
-- );
-- insert into NHAHOCDUONG_TEETH_CONDITION (id, label, description)
-- VALUES (0, '0', 'Bình thường'),
--        (1, '1', 'Sâu'),
--        (2, '2', 'Sâu trám lại'),
--        (3, '3', 'Trám tốt'),
--        (4, '4', 'Mất do sâu'),
--        (5, '5', 'Mất lý do khác'),
--        (6, '6', 'Bít hố rãnh'),
--        (7, '7', 'Trụ, cầu'),
--        (8, '8', 'Chưa mọc'),
--        (9, '9', 'Loại trừ')
-- ;

DROP TABLE if exists NHAHOCDUONG_TEETH_RECORD cascade ;
create table NHAHOCDUONG_TEETH_RECORD(
    id bigserial primary key not null,
    record jsonb not null
);
insert into nhahocduong_teeth_record
values
    (1, '{"61": {"problem": "1", "locations": ["X", "Nh"], "treatment": "1"}, "72": {"problem": "2", "locations": ["T", "Nh"], "treatment": "6"}}')
,   (2, '{"61": {"problem": "1", "locations": ["X", "Nh"], "treatment": "1"}, "72": {"problem": "2", "locations": ["T", "Nh"], "treatment": "6"}}')
,   (3, '{"61": {"problem": "1", "locations": ["X", "Nh"], "treatment": "1"}, "72": {"problem": "2", "locations": ["T", "Nh"], "treatment": "6"}}')
;
alter sequence nhahocduong_teeth_record_id_seq restart 100;
-- MẢNG BÁM

-- DROP TABLE if exists NHAHOCDUONG_PLAqUE_CONDITION cascade ;
-- create table NHAHOCDUONG_PLAqUE_CONDITION(
--     id bigint primary key not null,
--     label varchar not null,
--     description varchar not null
-- );
-- insert into NHAHOCDUONG_PLAqUE_CONDITION (id, label, description)
-- VALUES (-1, 'X', 'Không có răng'),
--        (0, '0', 'Không có mảng bám'),
--        (1, '1', 'Mảng bám 1/3 cổ răng/vết dính'),
--        (2, '2', 'Mảng bám > 2/3 răng');

DROP TABLE if exists NHAHOCDUONG_PLAqUE_RECORD cascade ;
create table NHAHOCDUONG_PLAQUE_RECORD(
    id bigserial primary key not null,
    "17-16n" varchar not null,
    "11n" varchar not null,
    "26-27n" varchar not null,
    "47-46t" varchar not null,
    "31n" varchar not null,
    "36-37t" varchar not null
);
insert into nhahocduong_plaque_record(id, "17-16n", "11n", "26-27n", "47-46t", "31n", "36-37t")
values
    (1, '0', '0', '0', '1', '0', '1')
    ,(2, '0', '4', '0', '1', '1', '1')
    ,(3, '1', '2', '0', '2', '1', '0')
;
alter sequence nhahocduong_plaque_record_id_seq restart 100;
-- VÔI RĂNG
-- DROP TABLE if exists NHAHOCDUONG_TARTAR_CONDITION cascade ;
-- create table NHAHOCDUONG_TARTAR_CONDITION(
--     id bigint primary key not null,
--     label varchar not null,
--     description varchar not null
-- );
-- insert into NHAHOCDUONG_TARTAR_CONDITION (id, label, description)
-- VALUES (-1, 'X', 'Không có răng'),
--        (0, '0', 'Không có vôi răng'),
--        (1, '1', 'Vôi răng 1/3 cổ răng'),
--        (2, '2', 'Vôi răng > 2/3 răng');

DROP TABLE if exists NHAHOCDUONG_TARTAR_RECORD cascade ;
create table NHAHOCDUONG_TARTAR_RECORD(
    id bigserial primary key not null,
    "17-16n" varchar not null,
    "11n" varchar not null,
    "26-27n" varchar not null,
    "47-46t" varchar not null,
    "31n" varchar not null,
    "36-37t" varchar not null
);
insert into nhahocduong_tartar_record(id, "17-16n", "11n", "26-27n", "47-46t", "31n", "36-37t")
values
    (1, '0', '0', '0', '1', '0', '1')
    ,(2, '0', '4', '0', '1', '1', '1')
    ,(3, '1', '2', '0', '2', '1', '0')
;
alter sequence nhahocduong_tartar_record_id_seq restart 100;

DROP TABLE if exists NHAHOCDUONG_EXAM cascade ;
create table NHAHOCDUONG_EXAM(
    id bigserial primary key not null,
    patient_id bigint references nhahocduong_patient(id),
    dentist_id bigint references nhahocduong_dentist(id),
    organization_id bigint references nhahocduong_organization(id),
    class varchar not null,
    year varchar not null,
    profile_number bigint unique,
    date date,
    teeth_record_id bigint references nhahocduong_teeth_record(id) unique ,
    plaque_record_id bigint references nhahocduong_plaque_record(id) unique ,
    tartar_record_id bigint references nhahocduong_tartar_record(id) unique,
    status boolean not null default true,
    created_date timestamp,
    updated_date timestamp,
    created_by varchar,
    updated_by varchar
--     treatment_record jsonb
--     prescription jsonb,
--     exam_place varchar not null,
--     diagnosis varchar
);
insert into nhahocduong_exam(id, patient_id, dentist_id, organization_id, class, year, teeth_record_id, plaque_record_id, tartar_record_id)
values
    (1, 1, 2, 1, '1A', '2023', 1, 1, 1)
    ,(2, 2, 2, 1, '1B', '2023', 2, 2, 2)
    ,(3, 3, 1, 1, '1C', '2023', 3, 3, 3)
;
alter sequence nhahocduong_exam_id_seq restart 100;

drop table if exists nhahocduong_disease cascade;
create table nhahocduong_disease(
    id bigserial primary key not null,
    code varchar ,
    name varchar not null
);
insert into nhahocduong_disease
values
    (1, 'X1', 'Cao huyết áp'),
    (2, 'X2', 'Tiểu đường'),
    (3, 'X3', 'Tim mạch'),
    (4, 'X4', 'Viêm khớp'),
    (5, 'X5', 'Bệnh thận'),
    (6, 'X6', 'Bệnh dạ dày'),
    (7, 'X7', 'Bệnh viêm khớp dạng thấp'),
    (8, 'X8', 'Bệnh lý khác')
;
alter sequence nhahocduong_disease_id_seq restart 100;

drop table if exists nhahocduong_exam_disease cascade;
create table nhahocduong_exam_disease (
    exam_id bigint references nhahocduong_exam(id),
    disease_id bigint references nhahocduong_disease(id)
);
insert into nhahocduong_exam_disease
values
    (1, 1),
    (1, 1),
    (2, 5)
;

drop table if exists nhahocduong_patient_disease cascade;
create table nhahocduong_patient_disease (
    patient_id bigint references nhahocduong_patient(id),
    disease_id bigint references nhahocduong_disease(id)
);
insert into nhahocduong_patient_disease
values
    (1, 1),
    (1, 1),
    (2, 5)
;

drop table if exists nhahocduong_medication cascade;
create table nhahocduong_medication (
    id bigserial primary key not null,
    code varchar unique ,
    name varchar not null,
    unit varchar
);
insert into nhahocduong_medication (id, code, name, unit)
values
    (1, '01', 'Fuji 7', 'Hộp'),
    (2, '02', 'Fuji 9', 'Hộp'),
    (3, '03', 'Clinpro sealant', 'Hộp'),
    (4, '04', 'Duraphat sealant', 'Hộp'),
    (5, '05', 'Composite', 'Ống'),
    (6, '06', 'Thuốc tê Lidocaine', 'Ống'),
    (7, '07', 'Thuốc tê xoa', 'Hộp'),
    (8, '08', 'Thuốc tê xịt', 'Chai'),
    (9, '09', 'V-Varnish', 'Que'),
    (10, '10', 'Duraphat 22600 ppmF', 'Que'),
    (11, '11', '3M ESPE Varnish 5%', 'Que')
;
alter sequence nhahocduong_medication_id_seq restart 100;

DROP TABLE if exists NHAHOCDUONG_TREATMENT_RECORD cascade ;
create table NHAHOCDUONG_TREATMENT_RECORD(
      id bigserial primary key not null,
      treatment_service varchar not null,
      dentist_name varchar,
      diagnosis varchar,
      tooth varchar not null,
      prescription jsonb,
      exam bigserial not null references NHAHOCDUONG_EXAM(id),
      status boolean not null default true,
      created_date timestamp,
      updated_date timestamp,
      created_by varchar,
      updated_by varchar
);

-- drop table if exists nhahocduong_prescription_item cascade;
-- create table nhahocduong_prescription_item (
--     id bigserial primary key not null ,
--     exam_id bigint references nhahocduong_exam(id) ,
--     medication_id bigint references nhahocduong_medication(id) not null ,
--     quantity integer not null
-- );
-- insert into nhahocduong_prescription_item
-- values (1, 1, 10, 1)
--     ,(2, 2, 6, 1)
--     ,(3, 3, 2, 1)
-- ;
-- alter sequence nhahocduong_prescription_item_id_seq restart 100;

-- Tạm thời lưu thông tin thuốc trong 'phiếu khám' (exam) thay vì exam -> treatment -> medication
-- TODO khi nào có điều kiện thì sửa lại
-- drop table if exists nhahocduong_exam_medication;
-- create table nhahocduong_exam_medication (
--     exam_id bigint not null references nhahocduong_exam(id),
--     medication_id bigint not null references nhahocduong_medication(id)
-- );



-- drop table if exists nhahocduong_treatment cascade;
-- create table nhahocduong_treatment (
--     id bigserial primary key not null,
--     date date
-- );

-- drop table if exists nhahocduong_treatment_medication cascade;
-- create table nhahocduong_treatment_medication (
--     treatment_id bigint not null references nhahocduong_treatment(id),
--     medication_id bigint not null references nhahocduong_medication(id)
-- );  --
