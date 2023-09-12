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
    classes      jsonb,
    created_date date,
    updated_date date,
    deleted_date date
);
insert into NHAHOCDUONG_ORGANIZATION (id, name, code, address, area_code, classes)
VALUES
(3, 'Trường tiểu học Trung Hiệp A', 'TEST01', 'Xã Trung Hiệp, Huyện Vũng Liêm, Vĩnh Long', '29683', '{"1": ["1A1", "1A2"], "2": ["2A1", "2A2", "2A3"], "3": ["3A1", "3A2", "3A3"], "4": ["4A1", "4A2", "4A3"], "5": ["5A1", "5A2", "5A3"]}'),
(4, 'Trường tiểu học Trung Hiệp B', 'TEST02','Ấp Trung Trị, Xã Trung Hiệp, Huyện Vũng Liêm, Vĩnh Long', '29683', '{"1": ["1A1", "1A2", "1A3"], "2": ["2A1", "2A2"], "3": ["3A1", "3A2", "3A3"], "4": ["4A1", "4A2", "4A3"], "5": ["5A1", "5A2", "5A3"]}'),
(1, 'Trường tiểu học Tân Hạnh A', 'TEST03', 'Ấp Tân Bình, Xã Tân Hạnh, Huyện Long Hồ, Vĩnh Long', '29593', '{"1": ["1A1"], "2": ["2A1", "2A2"], "3": ["3A1", "3A2", "3A3"], "4": ["4A1", "4A2"], "5": ["5A1", "5A2"]}'),
(6, 'Trường tiểu học Tân Hạnh B', 'TEST04', 'Ấp Tân Hoà, Xã Tân Hạnh, Huyện Long Hồ, Vĩnh Long', '29593', '{"1": ["1A1", "1A2", "1A3"], "2": ["2A1", "2A2", "2A3"], "3": ["3A1", "3A2", "3A3"], "4": ["4A1", "4A2", "4A3"], "5": ["5A1", "5A2", "5A3"]}'),
(2, 'Trường tiểu học An Phước B', 'TEST05', 'Phú An, Xã An Phước, Huyện Mang Thít, Vĩnh Long', '29629', '{"1": ["1A1", "1A2", "1A3"], "2": ["2A1", "2A2", "2A3"], "3": ["3A1", "3A2", "3A3"], "4": ["4A1", "4A2", "4A3"], "5": ["5A1", "5A2", "5A3"]}'),
(7, 'Trường tiểu học An Phước A', 'TEST06', 'Thành phố Hà Nội', '01', '{}'),
(5, 'Trường tiểu học Tân Hạnh C', 'TEST07', 'Tỉnh Hà Giang', '02', '{"1": ["1A1", "1A2", "1A3"], "2": ["2A1", "2A2", "2A3"], "3": ["3A1", "3A2"], "4": ["4A1", "4A2", "4A3"], "5": ["5A1", "5A2", "5A3"]}')
;
-- Dữ liệu thật bắt đầu từ id 10_000 trở đi
alter sequence nhahocduong_organization_id_seq restart 10000;
-- Đổ dữ liệu thật tỉnh Vĩnh Long
insert into NHAHOCDUONG_ORGANIZATION (id, name, code, address, area_code, classes)
values
(10000, 'Trường tiểu học Nguyễn Du', 'VL001', 'Vĩnh Long', '86', null),
(10001, 'Trường tiểu học Hùng Vương', 'VL002', 'Vĩnh Long', '86', null),
(10002, 'Trường tiểu học Nguyễn Huệ', 'VL003', 'Vĩnh Long', '86', null),
(10003, 'Trường tiểu học Sư phạm Thực hành', 'VL004', 'Vĩnh Long', '86', null),
(10004, 'Trường tiểu học Lê Lợi', 'VL005', 'Vĩnh Long', '86', null),
(10005, 'Trường tiểu học Trần Đại Nghĩa', 'VL006', 'Vĩnh Long', '86', null),
(10006, 'Trường tiểu học Trần Quốc Tuấn', 'VL007', 'Vĩnh Long', '86', null),
(10007, 'Trường tiểu học Thiềng Đức', 'VL008', 'Vĩnh Long', '86', null),
(10008, 'Trường tiểu học Trương Định', 'VL009', 'Vĩnh Long', '86', null),
(10009, 'Trường tiểu học Chu Văn An', 'VL010', 'Vĩnh Long', '86', null),
(10010, 'Trường tiểu học Phạm Hùng', 'VL011', 'Vĩnh Long', '86', null),
(10011, 'Trường tiểu học Nguyễn Hữu Huân', 'VL012', 'Vĩnh Long', '86', null),
(10012, 'Trường tiểu học Nguyễn Trung Trực', 'VL013', 'Vĩnh Long', '86', null),
(10013, 'Trường tiểu học Lê Hồng Phong', 'VL014', 'Vĩnh Long', '86', null),
(10014, 'Trường tiểu học Lý Thường Kiệt', 'VL015', 'Vĩnh Long', '86', null),
(10015, 'Trường tiểu học An Bình B', 'VL016', 'Vĩnh Long', '86', null),
(10016, 'Trường tiểu học Bình Hòa Phước A', 'VL017', 'Vĩnh Long', '86', null),
(10017, 'Trường tiểu học Trương Văn Ba', 'VL018', 'Vĩnh Long', '86', null),
(10018, 'Trường tiểu học Đồng Phú A', 'VL019', 'Vĩnh Long', '86', null),
(10019, 'Trường tiểu học Long Phước B', 'VL020', 'Vĩnh Long', '86', null),
(10020, 'Trường tiểu học Phước Hậu A', 'VL021', 'Vĩnh Long', '86', null),
(10021, 'Trường tiểu học Phước Hậu C', 'VL022', 'Vĩnh Long', '86', null),
(10022, 'Trường tiểu học Tân Hạnh B', 'VL023', 'Vĩnh Long', '86', null),
(10023, 'Trường tiểu học Tân Hạnh C', 'VL024', 'Vĩnh Long', '86', null),
(10024, 'Trường tiểu học Lộc Hòa A', 'VL025', 'Vĩnh Long', '86', null),
(10025, 'Trường tiểu học Lộc Hòa B', 'VL026', 'Vĩnh Long', '86', null),
(10026, 'Trường tiểu học Hòa Phú A', 'VL027', 'Vĩnh Long', '86', null),
(10027, 'Trường tiểu học Phú Quới A', 'VL028', 'Vĩnh Long', '86', null),
(10028, 'Trường tiểu học Phú Quới C', 'VL029', 'Vĩnh Long', '86', null),
(10029, 'Trường tiểu học Thạnh Quới A', 'VL030', 'Vĩnh Long', '86', null),
(10030, 'Trường tiểu học Long An A', 'VL031', 'Vĩnh Long', '86', null),
(10031, 'Trường tiểu học Phú Đức A', 'VL032', 'Vĩnh Long', '86', null),
(10032, 'Trường tiểu học Phú Đức C', 'VL033', 'Vĩnh Long', '86', null),
(10033, 'Trường tiểu học Thanh Đức B', 'VL034', 'Vĩnh Long', '86', null),
(10034, 'Trường tiểu học Thanh Đức C', 'VL035', 'Vĩnh Long', '86', null),
(10035, 'Trường tiểu học A Thị Trấn LH', 'VL036', 'Vĩnh Long', '86', null),
(10036, 'Trường tiểu học Chánh An A', 'VL037', 'Vĩnh Long', '86', null),
(10037, 'Trường tiểu học An Phước A', 'VL038', 'Vĩnh Long', '86', null),
(10038, 'Trường tiểu học Bình Phước A', 'VL039', 'Vĩnh Long', '86', null),
(10039, 'Trường tiểu học Bình Phước C', 'VL040', 'Vĩnh Long', '86', null),
(10040, 'Trường tiểu học Hòa Tịnh A', 'VL041', 'Vĩnh Long', '86', null),
(10041, 'Trường tiểu học Hòa Tịnh B', 'VL042', 'Vĩnh Long', '86', null),
(10042, 'Trường tiểu học Long Mỹ', 'VL043', 'Vĩnh Long', '86', null),
(10043, 'Trường tiểu học Mỹ An A', 'VL044', 'Vĩnh Long', '86', null),
(10044, 'Trường tiểu học Mỹ An B', 'VL045', 'Vĩnh Long', '86', null),
(10045, 'Trường tiểu học Mỹ Phước A', 'VL046', 'Vĩnh Long', '86', null),
(10046, 'Trường tiểu học Nhơn Phú A', 'VL047', 'Vĩnh Long', '86', null),
(10047, 'Trường tiểu học Nhơn Phú C', 'VL048', 'Vĩnh Long', '86', null),
(10048, 'Trường tiểu học Tân Long Hội A', 'VL049', 'Vĩnh Long', '86', null),
(10049, 'Trường tiểu học Thị Trấn Cái Nhum ', 'VL050', 'Vĩnh Long', '86', null),
(10050, 'Trường tiểu học Chánh Hội A', 'VL051', 'Vĩnh Long', '86', null),
(10051, 'Trường tiểu học Tân An Hội A', 'VL052', 'Vĩnh Long', '86', null),
(10052, 'Trường tiểu học Tân Long A', 'VL053', 'Vĩnh Long', '86', null),
(10053, 'Trường tiểu học Tân An Luông', 'VL054', 'Vĩnh Long', '86', null),
(10054, 'Trường tiểu học Hiếu Phụng A', 'VL055', 'Vĩnh Long', '86', null),
(10055, 'Trường tiểu học Huỳnh Văn Lời', 'VL056', 'Vĩnh Long', '86', null),
(10056, 'Trường tiểu học Nguyễn Văn Quỳ', 'VL057', 'Vĩnh Long', '86', null),
(10057, 'Trường tiểu học Hiếu Nhơn B', 'VL058', 'Vĩnh Long', '86', null),
(10058, 'Trường tiểu học Hiếu Thành', 'VL059', 'Vĩnh Long', '86', null),
(10059, 'Trường tiểu học Đỗ Quang Mười', 'VL060', 'Vĩnh Long', '86', null),
(10060, 'Trường tiểu học Trung An A', 'VL061', 'Vĩnh Long', '86', null),
(10061, 'Trường tiểu học Nguyễn Trung Kiên', 'VL062', 'Vĩnh Long', '86', null),
(10062, 'Trường tiểu học Trung Thành A', 'VL063', 'Vĩnh Long', '86', null),
(10063, 'Trường tiểu học Nguyễn Văn Thời', 'VL064', 'Vĩnh Long', '86', null),
(10064, 'Trường tiểu học Đặng Thị Chính', 'VL065', 'Vĩnh Long', '86', null),
(10065, 'Trường tiểu học Trung Thành Đông A', 'VL066', 'Vĩnh Long', '86', null),
(10066, 'Trường tiểu học Thị trấn Vũng Liêm', 'VL067', 'Vĩnh Long', '86', null),
(10067, 'Trường tiểu học Trung Thành Tây A', 'VL068', 'Vĩnh Long', '86', null),
(10068, 'Trường tiểu học Trung Hiệp A', 'VL069', 'Vĩnh Long', '86', null),
(10069, 'Trường tiểu học Trung Chánh A', 'VL070', 'Vĩnh Long', '86', null),
(10070, 'Trường tiểu học Tân Quới Trung A', 'VL071', 'Vĩnh Long', '86', null),
(10071, 'Trường tiểu học Nguyễn Văn Kết', 'VL072', 'Vĩnh Long', '86', null),
(10072, 'Trường tiểu học Lê Văn Cư', 'VL073', 'Vĩnh Long', '86', null),
(10073, 'Trường tiểu học Võ Ngọc Tốt', 'VL074', 'Vĩnh Long', '86', null),
(10074, 'Trường tiểu học Thanh Bình B', 'VL075', 'Vĩnh Long', '86', null),
(10075, 'Trường tiểu học  Lưu Văn Liệt', 'VL076', 'Vĩnh Long', '86', null),
(10076, 'Trường tiểu học Tường Lộc A', 'VL077', 'Vĩnh Long', '86', null),
(10077, 'Trường tiểu học Tường Lộc B', 'VL078', 'Vĩnh Long', '86', null),
(10078, 'Trường tiểu học Mỹ Thạnh Trung A', 'VL079', 'Vĩnh Long', '86', null),
(10079, 'Trường tiểu học Mỹ Thạnh Trung B', 'VL080', 'Vĩnh Long', '86', null),
(10080, 'Trường tiểu học Hòa Lộc A', 'VL081', 'Vĩnh Long', '86', null),
(10081, 'Trường tiểu học Hòa Lộc B', 'VL082', 'Vĩnh Long', '86', null),
(10082, 'Trường tiểu học Hòa Hiệp', 'VL083', 'Vĩnh Long', '86', null),
(10083, 'Trường tiểu học Hòa Thạnh', 'VL084', 'Vĩnh Long', '86', null),
(10084, 'Trường tiểu học Hậu Lộc', 'VL085', 'Vĩnh Long', '86', null),
(10085, 'Trường tiểu học Tân Lộc', 'VL086', 'Vĩnh Long', '86', null),
(10086, 'Trường tiểu học Phú Lộc', 'VL087', 'Vĩnh Long', '86', null),
(10087, 'Trường tiểu học Cái Ngang', 'VL088', 'Vĩnh Long', '86', null),
(10088, 'Trường tiểu học Mỹ Lộc', 'VL089', 'Vĩnh Long', '86', null),
(10089, 'Trường tiểu học Song Phú A', 'VL090', 'Vĩnh Long', '86', null),
(10090, 'Trường tiểu học Song Phú B', 'VL091', 'Vĩnh Long', '86', null),
(10091, 'Trường tiểu học Tô Hùng Vĩ', 'VL092', 'Vĩnh Long', '86', null),
(10092, 'Trường tiểu học Long Phú', 'VL093', 'Vĩnh Long', '86', null),
(10093, 'Trường tiểu học Phú Thịnh A', 'VL094', 'Vĩnh Long', '86', null),
(10094, 'Trường tiểu học Phú Thịnh B', 'VL095', 'Vĩnh Long', '86', null),
(10095, 'Trường tiểu học Bình Ninh', 'VL096', 'Vĩnh Long', '86', null),
(10096, 'Trường tiểu học Ngãi Tứ A', 'VL097', 'Vĩnh Long', '86', null),
(10097, 'Trường tiểu học Ngãi Tứ B', 'VL098', 'Vĩnh Long', '86', null),
(10098, 'Trường tiểu học Loan Mỹ B', 'VL099', 'Vĩnh Long', '86', null),
(10099, 'Trường tiểu học Thạch Thia', 'VL100', 'Vĩnh Long', '86', null),
(10100, 'Trường tiểu học Hòa Bình A', 'VL101', 'Vĩnh Long', '86', null),
(10101, 'Trường tiểu học Hòa Bình C', 'VL102', 'Vĩnh Long', '86', null),
(10102, 'Trường tiểu học Hựu Thành A', 'VL103', 'Vĩnh Long', '86', null),
(10103, 'Trường tiểu học Hựu Thành B', 'VL104', 'Vĩnh Long', '86', null),
(10104, 'Trường tiểu học Lục Sĩ Thành A', 'VL105', 'Vĩnh Long', '86', null),
(10105, 'Trường tiểu học Phú Thành', 'VL106', 'Vĩnh Long', '86', null),
(10106, 'Trường tiểu học Tân Mỹ A', 'VL107', 'Vĩnh Long', '86', null),
(10107, 'Trường tiểu học Tân Mỹ B', 'VL108', 'Vĩnh Long', '86', null),
(10108, 'Trường tiểu học Trà Côn A', 'VL109', 'Vĩnh Long', '86', null),
(10109, 'Trường tiểu học Trà Côn C', 'VL110', 'Vĩnh Long', '86', null),
(10110, 'Trường tiểu học Thới Hòa A', 'VL111', 'Vĩnh Long', '86', null),
(10111, 'Trường tiểu học Thới Hòa B', 'VL112', 'Vĩnh Long', '86', null),
(10112, 'Trường tiểu học Thuận Thới A', 'VL113', 'Vĩnh Long', '86', null),
(10113, 'Trường tiểu học Thuận Thới B', 'VL114', 'Vĩnh Long', '86', null),
(10114, 'Trường tiểu học Tích Thiện A', 'VL115', 'Vĩnh Long', '86', null),
(10115, 'Trường tiểu học Thiện Mỹ A', 'VL116', 'Vĩnh Long', '86', null),
(10116, 'Trường tiểu học Vĩnh Xuân', 'VL117', 'Vĩnh Long', '86', null),
(10117, 'Trường tiểu học Thị Trấn Trà Ôn', 'VL118', 'Vĩnh Long', '86', null),
(10118, 'Trường tiểu học Nhơn Bình A', 'VL119', 'Vĩnh Long', '86', null),
(10119, 'Trường tiểu học Nhơn Bình B', 'VL120', 'Vĩnh Long', '86', null),
(10120, 'Trường tiểu học Xuân Hiệp A', 'VL121', 'Vĩnh Long', '86', null),
(10121, 'Trường tiểu học Xuân Hiệp B', 'VL122', 'Vĩnh Long', '86', null),
(10122, 'Trường tiểu học Lê Thánh Tông', 'VL123', 'Vĩnh Long', '86', null),
(10123, 'Trường tiểu học Phan Bội Châu', 'VL124', 'Vĩnh Long', '86', null),
(10124, 'Trường tiểu học Trần Bình Trọng', 'VL125', 'Vĩnh Long', '86', null),
(10125, 'Trường tiểu học Mỹ Hòa C', 'VL126', 'Vĩnh Long', '86', null),
(10126, 'Trường tiểu học Nguyễn Văn Trỗi', 'VL127', 'Vĩnh Long', '86', null),
(10127, 'Trường tiểu học Lý Thường Kiệt', 'VL128', 'Vĩnh Long', '86', null),
(10128, 'Trường tiểu học Võ Thị Sáu', 'VL129', 'Vĩnh Long', '86', null),
(10129, 'Trường tiểu học Nguyễn Thị Minh Khai', 'VL130', 'Vĩnh Long', '86', null),
(10130, 'Trường tiểu học Phù Ly', 'VL131', 'Vĩnh Long', '86', null),
(10131, 'Trường tiểu học Phan Văn Năm', 'VL132', 'Vĩnh Long', '86', null),
(10132, 'Trường tiểu học Thoại Ngọc Hầu', 'VL133', 'Vĩnh Long', '86', null),
(10133, 'Trường tiểu học Phan Văn Đáng', 'VL134', 'Vĩnh Long', '86', null),
(10134, 'Trường tiểu học Tân Hưng', 'VL135', 'Vĩnh Long', '86', null),
(10135, 'Trường tiểu học Tân An Thạnh A', 'VL136', 'Vĩnh Long', '86', null),
(10136, 'Trường tiểu học Tân An Thạnh B', 'VL137', 'Vĩnh Long', '86', null),
(10137, 'Trường tiểu học Tân Lược', 'VL138', 'Vĩnh Long', '86', null),
(10138, 'Trường tiểu học Tân Bình', 'VL139', 'Vĩnh Long', '86', null),
(10139, 'Trường tiểu học Tân Thành A', 'VL140', 'Vĩnh Long', '86', null),
(10140, 'Trường tiểu học Tân Quới A', 'VL141', 'Vĩnh Long', '86', null),
(10141, 'Trường tiểu học Thành Đông A', 'VL142', 'Vĩnh Long', '86', null),
(10142, 'Trường tiểu học Thành Lợi A', 'VL143', 'Vĩnh Long', '86', null),
(10143, 'Trường tiểu học Thành Lợi C', 'VL144', 'Vĩnh Long', '86', null),
(10144, 'Trường tiểu học Thành Trung A', 'VL145', 'Vĩnh Long', '86', null),
(10145, 'Trường tiểu học Thành Trung B', 'VL146', 'Vĩnh Long', '86', null),
(10146, 'Trường tiểu học Mỹ Thuận A', 'VL147', 'Vĩnh Long', '86', null),
(10147, 'Trường tiểu học Mỹ Thuận B', 'VL148', 'Vĩnh Long', '86', null),
(10148, 'Trường tiểu học Nguyễn Văn Thảnh A', 'VL149', 'Vĩnh Long', '86', null),
(10149, 'Mầm non Vĩnh Xuân', 'VL150', 'Vĩnh Long', '86', null),
(10150, 'Mầm Non Hựu Thành', 'VL151', 'Vĩnh Long', '86', null),
(10151, 'Mầm Non Thới Hòa', 'VL152', 'Vĩnh Long', '86', null),
(10152, 'Mầm non Trà Côn', 'VL153', 'Vĩnh Long', '86', null),
(10153, 'Mầm non Thiện Mỹ', 'VL154', 'Vĩnh Long', '86', null),
(10154, 'Mầm non Tích Thiện', 'VL155', 'Vĩnh Long', '86', null),
(10155, 'Mầm Non Tuổi Thơ (Tân Quới)', 'VL156', 'Vĩnh Long', '86', null),
(10156, 'Mẫu giáo Hoa Mai', 'VL157', 'Vĩnh Long', '86', null),
(10157, 'Mẫu giáo Măng Non', 'VL158', 'Vĩnh Long', '86', null),
(10158, 'Mầm non Mỹ Thuận', 'VL159', 'Vĩnh Long', '86', null),
(10159, 'Mầm Non Nguyễn Văn Thảnh', 'VL160', 'Vĩnh Long', '86', null),
(10160, 'Mầm non Hoa Phượng', 'VL161', 'Vĩnh Long', '86', null),
(10161, 'Mẫu giáo Hướng Dương', 'VL162', 'Vĩnh Long', '86', null),
(10162, 'Mầm non Tân Hưng', 'VL163', 'Vĩnh Long', '86', null),
(10163, 'Mầm non Thành Trung', 'VL164', 'Vĩnh Long', '86', null),
(10164, 'Mầm non Tân Thành', 'VL165', 'Vĩnh Long', '86', null),
(10165, 'Mầm non Tuổi Thơ', 'VL166', 'Vĩnh Long', '86', null),
(10166, 'Mầm non Sơn Ca (Tân Lược)', 'VL167', 'Vĩnh Long', '86', null),
(10167, 'Mầm non Thực hành Măng non Phường 9', 'VL168', 'Vĩnh Long', '86', null),
(10168, 'Mầm non Bé By Ngoan', 'VL169', 'Vĩnh Long', '86', null),
(10169, 'Mầm non Hoa Sen Thành Phố Vĩnh Long', 'VL170', 'Vĩnh Long', '86', null),
(10170, 'Nhà trẻ Trinh Vương', 'VL171', 'Vĩnh Long', '86', null),
(10171, 'Nhóm trẻ Gia Bảo', 'VL172', 'Vĩnh Long', '86', null),
(10172, 'Mầm non Phú Đức', 'VL173', 'Vĩnh Long', '86', null),
(10173, 'Mầm Non Thanh Đức', 'VL174', 'Vĩnh Long', '86', null),
(10174, 'Mầm non An Bình', 'VL175', 'Vĩnh Long', '86', null),
(10175, 'Mầm non Họa Mi thị trấn Long Hồ', 'VL176', 'Vĩnh Long', '86', null),
(10176, 'Mầm non Bình Hòa Phước', 'VL177', 'Vĩnh Long', '86', null),
(10177, 'Mầm non Đồng Phú', 'VL178', 'Vĩnh Long', '86', null),
(10178, 'Mầm non Long Phước', 'VL179', 'Vĩnh Long', '86', null),
(10179, 'Mẫu giáo Hoa Hồng Lộc Hòa', 'VL180', 'Vĩnh Long', '86', null),
(10180, 'Mẫu giáo Phú Quới', 'VL181', 'Vĩnh Long', '86', null),
(10181, 'Mầm non Phước Hậu', 'VL182', 'Vĩnh Long', '86', null),
(10182, 'Mầm non  Long An', 'VL183', 'Vĩnh Long', '86', null),
(10183, 'Mầm Non Hòa Ninh', 'VL184', 'Vĩnh Long', '86', null),
(10184, 'Mầm non Thạnh Quới', 'VL185', 'Vĩnh Long', '86', null),
(10185, 'Mầm Non Thị trấn Long Hồ', 'VL186', 'Vĩnh Long', '86', null),
(10186, 'Mầm non Lộc Hòa', 'VL187', 'Vĩnh Long', '86', null),
(10187, 'Mầm non Tân Hạnh', 'VL188', 'Vĩnh Long', '86', null),
(10188, 'Mầm non Hòa Phú', 'VL189', 'Vĩnh Long', '86', null),
(10189, 'Mầm non khu công nghiệp Hòa Phú', 'VL190', 'Vĩnh Long', '86', null),
(10190, 'Mầm non Hồng Ân', 'VL191', 'Vĩnh Long', '86', null),
(10191, 'Mầm non Họa Mi Hòa Phú', 'VL192', 'Vĩnh Long', '86', null),
(10192, 'Mẫu giáo Măng Non I (Mỹ Phước)', 'VL193', 'Vĩnh Long', '86', null),
(10193, 'Mầm non Măng Non II', 'VL194', 'Vĩnh Long', '86', null),
(10194, 'Mầm non Oanh Vũ I', 'VL195', 'Vĩnh Long', '86', null),
(10195, 'Mầm Non Oanh Vũ II (Tân Long Hội)', 'VL196', 'Vĩnh Long', '86', null),
(10196, 'Mầm non Sơn Ca I (Hòa Tịnh)', 'VL197', 'Vĩnh Long', '86', null),
(10197, 'Mầm non Sơn Ca I (Bình Phước)', 'VL198', 'Vĩnh Long', '86', null),
(10198, 'Mầm non Tuổi Thơ I', 'VL199', 'Vĩnh Long', '86', null),
(10199, 'Mầm non Tuổi Thơ II (An Phước)', 'VL200', 'Vĩnh Long', '86', null),
(10200, 'Mầm non Tuổi Thơ III (Nhơn Phú)', 'VL201', 'Vĩnh Long', '86', null),
(10201, 'Mầm non Tuổi Thơ IV (Chánh An)', 'VL202', 'Vĩnh Long', '86', null),
(10202, 'Mầm Non Sơn Ca III', 'VL203', 'Vĩnh Long', '86', null),
(10203, 'Mầm Non Oanh Vũ III (Tân An Hội)', 'VL204', 'Vĩnh Long', '86', null),
(10204, 'Mầm Non Thị Trấn Cái Nhum', 'VL205', 'Vĩnh Long', '86', null),
(10205, 'Mầm Non Hiếu Phụng', 'VL206', 'Vĩnh Long', '86', null),
(10206, 'Mầm non Hiếu Nhơn', 'VL207', 'Vĩnh Long', '86', null),
(10207, 'Mẫu giáo Hiếu Thành', 'VL208', 'Vĩnh Long', '86', null),
(10208, 'Mầm Non Trung Hiếu', 'VL209', 'Vĩnh Long', '86', null),
(10209, 'Mẫu giáo Trung Thành', 'VL210', 'Vĩnh Long', '86', null),
(10210, 'Mẫu giáo Trung Nghĩa', 'VL211', 'Vĩnh Long', '86', null),
(10211, 'Mẫu giáo Trung Hiệp', 'VL212', 'Vĩnh Long', '86', null),
(10212, 'Mẫu giáo Tân Quới Trung', 'VL213', 'Vĩnh Long', '86', null),
(10213, 'Mầm non Quới An', 'VL214', 'Vĩnh Long', '86', null),
(10214, 'Mẫu giáo Quới Thiện', 'VL215', 'Vĩnh Long', '86', null),
(10215, 'Mầm Non Thanh Bình', 'VL216', 'Vĩnh Long', '86', null),
(10216, 'Mầm non Trung An', 'VL217', 'Vĩnh Long', '86', null),
(10217, 'Mầm non Trịnh Liên Hoa', 'VL218', 'Vĩnh Long', '86', null),
(10218, 'Mầm Non Trung Thành Tây', 'VL219', 'Vĩnh Long', '86', null),
(10219, 'Mẫu giáo Trung Chánh', 'VL220', 'Vĩnh Long', '86', null),
(10220, 'Mầm Non Hiếu Nghĩa', 'VL221', 'Vĩnh Long', '86', null),
(10221, 'Mầm non Tân An Luông', 'VL222', 'Vĩnh Long', '86', null),
(10222, 'Mầm non Thị Trấn Vũng Liêm', 'VL223', 'Vĩnh Long', '86', null),
(10223, 'Mầm non Sơn Ca', 'VL224', 'Vĩnh Long', '86', null),
(10224, 'Mầm Non Mỹ Lộc', 'VL225', 'Vĩnh Long', '86', null),
(10225, 'Mầm non Bông Sen', 'VL226', 'Vĩnh Long', '86', null),
(10226, 'Mẫu giáo Hoa Đào', 'VL227', 'Vĩnh Long', '86', null),
(10227, 'Mầm non Họa Mi', 'VL228', 'Vĩnh Long', '86', null),
(10228, 'Mầm non Tuổi Xanh', 'VL229', 'Vĩnh Long', '86', null),
(10229, 'Mầm non Hoa Hồng', 'VL230', 'Vĩnh Long', '86', null),
(10230, 'Mầm non Hoa Sen', 'VL231', 'Vĩnh Long', '86', null),
(10231, 'Mầm non Măng Non', 'VL232', 'Vĩnh Long', '86', null),
(10232, 'Mầm non Tuổi Thơ', 'VL233', 'Vĩnh Long', '86', null),
(10233, 'Mầm non Hoa Lan', 'VL234', 'Vĩnh Long', '86', null),
(10234, 'Mầm non Vành Khuyên', 'VL235', 'Vĩnh Long', '86', null),
(10235, 'Mầm non Hướng Dương', 'VL236', 'Vĩnh Long', '86', null),
(10236, 'Mầm non Kim Đồng', 'VL237', 'Vĩnh Long', '86', null),
(10237, 'Mầm non Sao Mai', 'VL238', 'Vĩnh Long', '86', null),
(10238, 'Mầm non Hoa Mai', 'VL239', 'Vĩnh Long', '86', null),
(10239, 'Mầm Non Rạng Đông', 'VL240', 'Vĩnh Long', '86', null),
(10240, 'Mầm Non Cái Ngang', 'VL241', 'Vĩnh Long', '86', null),
(10241, 'Mầm non Hoa Sen (Đ.Bình)', 'VL242', 'Vĩnh Long', '86', null),
(10242, 'Mầm non Mỹ Hòa', 'VL243', 'Vĩnh Long', '86', null),
(10243, 'Mầm Non Hoa Lan', 'VL244', 'Vĩnh Long', '86', null),
(10244, 'Mầm Non Khai Trí', 'VL245', 'Vĩnh Long', '86', null),
(10245, 'Mầm non Hoa Hồng', 'VL246', 'Vĩnh Long', '86', null),
(10246, 'Mầm non Họa Mi (Thuận An)', 'VL247', 'Vĩnh Long', '86', null),
(10247, 'Mầm non Hoa Hồng 2', 'VL248', 'Vĩnh Long', '86', null),
(10248, 'Mầm non Sao Mai (Đông Thành)', 'VL249', 'Vĩnh Long', '86', null),
(10249, 'Mầm non Sen Hồng', 'VL250', 'Vĩnh Long', '86', null),
(10250, 'Mầm non Đông Thạnh', 'VL251', 'Vĩnh Long', '86', null),
(10251, 'Mầm non Bình Minh', 'VL252', 'Vĩnh Long', '86', null),
(10252, 'Mầm Non Hoàng Lam', 'VL253', 'Vĩnh Long', '86', null),
(10253, 'Mầm Non Thuận Thới', 'VL254', 'Vĩnh Long', '86', null),
(10254, 'Mầm Non Xuân Hiệp', 'VL255', 'Vĩnh Long', '86', null),
(10255, 'Mầm Non Hòa Bình', 'VL256', 'Vĩnh Long', '86', null),
(10256, 'Mầm Non Lục Sĩ Thành', 'VL257', 'Vĩnh Long', '86', null),
(10257, 'Mầm non Tân Mỹ', 'VL258', 'Vĩnh Long', '86', null),
(10258, 'Mầm Non Nhơn Bình', 'VL259', 'Vĩnh Long', '86', null),
(10259, 'Mầm Non Phú Thành', 'VL260', 'Vĩnh Long', '86', null),
(10260, 'Mầm non Ánh Dương Thị trấn Trà Ôn', 'VL261', 'Vĩnh Long', '86', null),
(10261, 'Mầm non Vĩnh Xuân', 'VL262', 'Vĩnh Long', '86', null),
(10262, 'Mầm Non Hựu Thành', 'VL263', 'Vĩnh Long', '86', null),
(10263, 'Mầm Non Thới Hòa', 'VL264', 'Vĩnh Long', '86', null),
(10264, 'Mầm non Trà Côn', 'VL265', 'Vĩnh Long', '86', null),
(10265, 'Mầm non Thiện Mỹ', 'VL266', 'Vĩnh Long', '86', null),
(10266, 'Mầm non Tích Thiện', 'VL267', 'Vĩnh Long', '86', null),
(10267, 'Mầm Non Tuổi Thơ (Tân Quới)', 'VL268', 'Vĩnh Long', '86', null),
(10268, 'Mẫu giáo Hoa Mai', 'VL269', 'Vĩnh Long', '86', null),
(10269, 'Mẫu giáo Măng Non', 'VL270', 'Vĩnh Long', '86', null),
(10270, 'Mầm non Mỹ Thuận', 'VL271', 'Vĩnh Long', '86', null),
(10271, 'Mầm Non Nguyễn Văn Thảnh', 'VL272', 'Vĩnh Long', '86', null),
(10272, 'Mầm non Hoa Phượng', 'VL273', 'Vĩnh Long', '86', null),
(10273, 'Mẫu giáo Hướng Dương', 'VL274', 'Vĩnh Long', '86', null),
(10274, 'Mầm non Tân Hưng', 'VL275', 'Vĩnh Long', '86', null),
(10275, 'Mầm non Thành Trung', 'VL276', 'Vĩnh Long', '86', null),
(10276, 'Mầm non Tân Thành', 'VL277', 'Vĩnh Long', '86', null),
(10277, 'Mầm non Tuổi Thơ', 'VL278', 'Vĩnh Long', '86', null),
(10278, 'Mầm non Sơn Ca (Tân Lược)', 'VL279', 'Vĩnh Long', '86', null)
;
-- Dữ liệu thật sau khi đổ dữ liệu Vĩnh Long bắt đầu từ id 10_279 trở đi
alter sequence nhahocduong_organization_id_seq restart 10279;

DROP TABLE if exists NHAHOCDUONG_PATIENT cascade ;
create table NHAHOCDUONG_PATIENT(
                                    id  bigserial primary key not null,
                                    full_name varchar not null,
                                    health_insurance_number varchar,
                                    gender integer not null,
                                    birthdate date not null,
                                    ethnic varchar not null,
                                    area_type varchar,
                                    phone_number varchar,
                                    address_line varchar,
                                    school_class varchar,
                                    national_id_num varchar,
                                    care_taker varchar,
                                    organization bigint references nhahocduong_organization(id)
);
insert into nhahocduong_patient (id, full_name, health_insurance_number, gender, birthdate, ethnic, area_type, phone_number, address_line, school_class, national_id_num, care_taker, organization)
values

(175, 'Nguyễn Văn Sỹ', 'DN5797939589580', 1, '2023-05-28', '0', 'Nông thôn', '09090901234', 'Bắc Ninh', '2A1', '061079013187777', 'Nguyễn Văn Thủy', 3),
(178, 'Nguyễn Tuấn Minh', '1awwr', 2, '2023-05-29', '', 'Thành thị', '09090901234', 'Hà Nội', '1A1', '1234', 'nghh', 3),
(119, 'Phan Huy Khang', 'DN5797915126119', 1, '2014-01-01', 'Kinh', 'Thành thị', '0966934212', '', '3A1', '064029013138', 'Phan Khang Huy', 2),
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
(131, 'Phan Huy Khang', 'DN5797911178482', 1, '2016-01-01', 'Kinh', 'Thành thị', '0382145484', null, '1A2', '064029013160', null, 2),
(158, 'Nguyễn Sỹ Hải', 'DN5797939470581', 1, '2016-01-01', 'Kinh', 'Ngoại ô', '0979855397', 'TP HCM', '1A2', '061089013185', 'Nguyen Van Toan', 1),
(172, 'Phạm Trường Quân', 'DN5797912359603', 1, '2016-05-30', 'Kinh', 'Ngoại ô', '09090901234', 'Bình Định', '1A1', '064029014189', '', 6),
(167, 'Nguyễn Tuấn Tú', 'DN5797933636011', 1, '2016-06-01', 'Kinh', 'Ngoại ô', '0335042469', 'TP HCM', '1A1', '063089023188', '', 1),
(173, 'Lê Thị Nhã Phương', 'DN5797916132525', 2, '2016-06-01', 'Kinh', 'Thành thị', '0983089241', 'TP HCM', '1A1', '061039013184', 'nghh', 1),
(168, 'Nguyễn Tuấn Hải', 'DN5797916122500', 1, '2016-06-02', 'Kinh', 'Ngoại ô', '0964800230', 'TP HCM', '1A1', '063089013191', 'ngh', 1),
(1, 'Nguyễn Văn Lạc', 'DN5797910626461', 1, '2016-11-11', 'Kinh', 'Nông thôn', '0365720169', null, '1A3', '063089043190', null, 1),
(2, 'Nguyễn Thị Thủy', 'DN5797910926420', 2, '2016-12-11', 'Kinh', 'Ngoại ô', '0399971111', null, '1A2', '063089003395', null, 2),
(169, 'Lê Văn Viễn', 'DN5798022703183', 1, '2016-06-02', 'Kinh', 'Nông thôn', '0327993479', '', '1A1', '061039013186', 'nghh', 1),
(166, 'Nguyễn Tuấn Minh', 'DN5797910126460', 1, '2016-06-08', 'Kinh', 'Nông thôn', '0359733643', '', '1A3', '063089023193', '', 5),
(161, 'Đặng Trần Khánh', 'DN5797910326660', 1, '2016-06-09', 'Kinh', 'Thành thị', '0366044857', '', '1A3', '', '', 1),
(171, 'Hồ Thị Tường Vy', 'DN5797910826460', 2, '2016-06-10', 'Kinh', 'Thành thị', '0938324834', 'TP HCM', '1A1', '041089013189', '', 2),
(157, 'Nguyễn Văn Đăng', 'DN5797910624460', 1, '2016-06-15', 'Kinh', 'Thành thị', '0345996073', 'Hà Nội', '1A1', '064024013191', 'addddd', 1),
(156, 'Phạm Văn Đồng', 'DN5797910426420', 1, '2016-06-16', 'Kinh', 'Ngoại ô', '0978542629', 'Đà Nẵng', '1A2', '071089013183', '', 7),
(163, 'Nguyễn Văn Kha', 'DN5797910525460', 1, '2016-07-08', 'Kinh', 'Ngoại ô', '0968069651', 'TP HCM', '1A3', '063089033194', 'Nguyễn Văn Khá', 7),
(160, 'Nguyễn Văn Sỹ', 'DN5797910632460', 1, '2016-08-08', 'Kinh', 'Thành thị', '0367048333', '', '1A2', '063089053192', '', 5),
(122, 'Nguyễn Trần Anh Thơ', 'DN5797910426460', 1, '2016-11-11', 'Kinh', 'Nông thôn', '0979791765', null, '1A1', '063089012187', null, 7),
(147, 'Lê Công Pha', 'DN5797910826663', 1, '2016-11-11', 'Kinh', 'Nông thôn', '0388941310', null, '1A2', '061029013184', null, 1),
(151, 'Lê Phan', 'DN5797910226663', 1, '2016-11-11', 'Kinh', 'Nông thôn', '0332146609', 'Tuyên Quang', '1A1', '061029013189', '213213', 7),
(155, 'Hoàng Đôn Thiện Hiếu', 'DN5797910326420', 1, '2016-06-16', 'Kinh', 'Thành thị', '09090901234', '', '1A1', '041089013187', '', 1),
(170, 'Lê Quang Tấn', 'DN5797910926421', 1, '2016-11-11', 'Kinh', 'Ngoại ô', '09090901234', 'Vĩnh Long', '1A1', '061029013383', 'adad', 4)
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
create table NHAHOCDUONG_PLAqUE_RECORD(
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
    tartar_record_id bigint references nhahocduong_tartar_record(id) unique ,
    prescription jsonb,
    treatment_record jsonb,
    exam_place varchar not null,
    diagnosis varchar
);
insert into nhahocduong_exam(id, patient_id, dentist_id, organization_id, class, year, teeth_record_id, plaque_record_id, tartar_record_id, prescription, exam_place, diagnosis)
values
    (1, 1, 2, 1, '1A', '2023', 1, 1, 1, '[{"quantity": 1, "medicationCode": "1"}, {"quantity": 2, "medicationCode": "2"}]', 'YTTH', 'Binh thuong')
    ,(2, 2, 2, 1, '1B', '2023', 2, 2, 2, '[{"quantity": 1, "medicationCode": "1"}, {"quantity": 2, "medicationCode": "2"}]', 'TYT/TTYT', 'Sau rang')
    ,(3, 3, 1, 1, '1C', '2023', 3, 3, 3, '[{"quantity": 1, "medicationCode": "1"}, {"quantity": 2, "medicationCode": "2"}]', 'YTTH', 'Nha chu' )
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
    (1, 2),
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
    (1, 2),
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
--     ,(3, 3, 2, 2)
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
