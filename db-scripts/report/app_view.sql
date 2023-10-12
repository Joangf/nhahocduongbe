-- tao view nay truoc
CREATE
OR REPLACE VIEW nhahocduong_report.app_view AS(
	WITH rvv AS (
		SELECT generate_series(11, 18)::text AS id
		UNION
		SELECT generate_series(21, 28)::text AS id
		UNION
		SELECT generate_series(31, 38)::text AS id
		UNION
		SELECT generate_series(41, 48)::text AS id
	), ttr_sau AS (
		SELECT id, JSONB_OBJECT_KEYS(record) AS teeth
		FROM nhahocduong.nhahocduong_teeth_record, JSONB_EACH(record)
		WHERE value->>'problem'='1'
	), ttr_mat AS (
		SELECT id, JSONB_OBJECT_KEYS(record) AS teeth
		FROM nhahocduong.nhahocduong_teeth_record, JSONB_EACH(record)
		WHERE value->>'problem'='4' OR value->>'problem'='5'
	), ttr_tram AS (
		SELECT id, JSONB_OBJECT_KEYS(record) AS teeth
		FROM nhahocduong.nhahocduong_teeth_record, JSONB_EACH(record)
		WHERE value->>'problem'='2' OR value->>'problem'='3'
	), pi AS (
		SELECT id, ROUND((pl."17-16n"::integer + pl."11n"::integer + pl."26-27n"::integer + pl."36-37t"::integer + pl."31n"::integer + pl."47-46t"::integer)/6, 2) as pi
		FROM nhahocduong.nhahocduong_plaque_record pl
	), ttr_tram_bit AS (
		SELECT id, JSONB_OBJECT_KEYS(record) AS teeth
		FROM nhahocduong.nhahocduong_teeth_record, JSONB_EACH(record)
		WHERE value->>'problem'='6'
	)
	SELECT e.date AS ngay_kham,
		e.exam_place::character varying AS y_bs_kham,
		o.name AS ten_truong_hoc,
		e.profile_number AS ma_so,
		p.full_name AS ho_va_ten,
		EXTRACT(YEAR FROM p.birthdate)::integer AS nam_sinh,
		(EXTRACT(YEAR FROM e.date)-EXTRACT(YEAR FROM p.birthdate))::integer AS tuoi,
		p.health_insurance_number AS so_the_bhyt,
		(
			CASE
			WHEN p.gender=1 THEN 'nam'::character varying
			WHEN p.gender=2 THEN 'nu'::character varying
			END
		) AS gioi,
		p.ethnic AS dan_toc,
		p.area_type AS dia_du,
		(CASE WHEN ed.disease_id IS NULL THEN 'khong'::character varying ELSE 'co'::character varying END) AS benh_man_tinh,
		d.name AS ghi_ro,
		(CASE WHEN t.id IS NULL THEN 'khong'::character varying ELSE 'co'::character varying END) AS tinhtrangsaur,
		(
			CASE WHEN
			(
				SELECT COUNT(teeth) FROM ttr_sau
				WHERE id = e.teeth_record_id AND teeth IN (SELECT * FROM rvv)
			)=0 THEN 'khong'::character varying ELSE 'co'::character varying END
		) AS rangvinhvien_co_bi_sau,
		(
			CASE WHEN
			(
				SELECT COUNT(teeth) FROM ttr_sau
				WHERE id = e.teeth_record_id AND teeth='16'
			)=0 THEN 'khong'::character varying ELSE 'co'::character varying END
		) AS r16,
		(
			CASE WHEN
			(
				SELECT COUNT(teeth) FROM ttr_sau
				WHERE id = e.teeth_record_id AND teeth='26'
			)=0 THEN 'khong'::character varying ELSE 'co'::character varying END
		) AS r26,
		(
			CASE WHEN
			(
				SELECT COUNT(teeth) FROM ttr_sau
				WHERE id = e.teeth_record_id AND teeth='36'
			)=0 THEN 'khong'::character varying ELSE 'co'::character varying END
		) AS r36,
		(
			CASE WHEN
			(
				SELECT COUNT(teeth) FROM ttr_sau
				WHERE id = e.teeth_record_id AND teeth='46'
			)=0 THEN 'khong'::character varying ELSE 'co'::character varying END) AS r46,
		(
			CASE WHEN
			(
				SELECT COUNT(teeth) FROM ttr_sau
				WHERE id = e.teeth_record_id
				AND teeth IN (select * from rvv) AND teeth NOT IN ('16', '26', '36', '46')
			)=0 THEN 'khong'::character varying ELSE 'co'::character varying END) AS rvv_khac,
		 (
			 SELECT STRING_AGG(teeth, ',')::character varying AS ghi_ro_ten_rang_vinh_vien_sau FROM ttr_sau
			 WHERE id = e.teeth_record_id AND teeth IN (select * from rvv)
		 ),
		 (
			 SELECT COUNT(teeth)::integer AS soluongrvv_bi_sau FROM ttr_sau
			 WHERE id = e.teeth_record_id AND teeth IN (select * from rvv)
		 ),
		 (
			 SELECT COUNT(teeth)::integer AS soluongrvv_bi_mat FROM ttr_mat
			 WHERE id = e.teeth_record_id AND teeth IN (select * from rvv)
		 ),
		 (
			 SELECT COUNT(teeth)::integer AS soluongrvv_da_tram FROM ttr_tram
			 WHERE id = e.teeth_record_id AND teeth IN (select * from rvv)
		 ),
		 (
			 CASE WHEN
			 (
				 SELECT COUNT(teeth) FROM ttr_sau
				 WHERE id = e.teeth_record_id AND teeth NOT IN (SELECT * FROM rvv)
			 )=0 THEN 'khong'::character varying ELSE 'co'::character varying END
		 ) AS rangsua_co_bi_sau,
		 (
			 SELECT COUNT(teeth)::integer AS soluongrangsua_bi_sau FROM ttr_sau
			 WHERE id = e.teeth_record_id AND teeth NOT IN (select * from rvv)
		 ),
		 (
			 SELECT STRING_AGG(teeth, ',')::character varying AS ghi_ro_ten_rang_sua_sau FROM ttr_sau
			 WHERE id = e.teeth_record_id AND teeth NOT IN (select * from rvv)
		 ),
		 (
			 SELECT COUNT(teeth)::integer AS soluongrangsua_bi_mat FROM ttr_mat
			 WHERE id = e.teeth_record_id AND teeth NOT IN (select * from rvv)
		 ),
		 (
			 SELECT STRING_AGG(teeth, ',')::character varying AS ghi_ro_ten_rang_sua_mat FROM ttr_mat
			 WHERE id = e.teeth_record_id AND teeth NOT IN (select * from rvv)
		 ),
		 (
			 SELECT COUNT(teeth)::integer AS soluongrangsua_bi_tram FROM ttr_tram
			 WHERE id = e.teeth_record_id AND teeth NOT IN (select * from rvv)
		 ),
		 (
			 SELECT STRING_AGG(teeth, ',')::character varying AS ghi_ro_ten_rang_sua_da_tram FROM ttr_tram
			 WHERE id = e.teeth_record_id AND teeth NOT IN (select * from rvv)
		 ),
		 pl."17-16n"::integer AS r1716_ngoai,
		 pl."11n"::integer AS r11_ngoai,
		 pl."26-27n"::integer AS r2627_ngoai,
		 pl."36-37t"::integer AS r3637_trong,
		 pl."31n"::integer AS r31_ngoai,
		 pl."47-46t"::integer AS r4746_trong,
		 pi.pi::double precision as chi_so_mang_bam,
		 (
			 CASE
			 WHEN pi>=0 AND pi<1 THEN 'tot'::character varying
			 WHEN pi>=1 AND pi<2 THEN 'trung binh'::character varying
			 WHEN pi>=2 THEN 'kem'::character varying
			 END
		 ) AS danh_gia_mang_bam,
		 (
			CASE WHEN
			(
				SELECT COUNT(teeth) FROM ttr_tram
				WHERE id = e.teeth_record_id
			)=0 THEN 'khong'::character varying ELSE 'co'::character varying END) AS co_tram_rang,
		 (
			 SELECT COUNT(teeth)::integer AS so_rang_duoc_tram FROM ttr_tram
			 WHERE id = e.teeth_record_id
		 ),
		 (
			 SELECT STRING_AGG(teeth, ',')::character varying AS ghi_ro_rang_tram FROM ttr_tram
			 WHERE id = e.teeth_record_id
		 ),
		 (
			CASE WHEN
			(
				SELECT COUNT(teeth) FROM ttr_tram_bit
				WHERE id = e.teeth_record_id
			)=0 THEN 'khong'::character varying ELSE 'co'::character varying END) AS co_tram_bit_ho_ranh,
		 (
			 SELECT COUNT(teeth)::integer AS so_rang_duoc_tram_bit FROM ttr_tram_bit
			 WHERE id = e.teeth_record_id
		 ),
		 (
			 SELECT STRING_AGG(teeth, ',')::character varying AS ghi_ro_rang_tram_bit FROM ttr_tram_bit
			 WHERE id = e.teeth_record_id
		 ),
		 LEFT(e.class, 1)::integer as khoi
	FROM nhahocduong.nhahocduong_exam e
	LEFT JOIN nhahocduong.nhahocduong_organization o ON e.organization_id = o.id
	LEFT JOIN nhahocduong.nhahocduong_patient p ON e.patient_id = p.id
	LEFT JOIN nhahocduong.nhahocduong_exam_disease ed ON e.id = ed.exam_id
	LEFT JOIN nhahocduong.nhahocduong_disease d ON ed.disease_id = d.id
	LEFT JOIN nhahocduong.nhahocduong_teeth_record t ON e.teeth_record_id = t.id
	LEFT JOIN nhahocduong.nhahocduong_plaque_record pl ON e.plaque_record_id = pl.id
	LEFT JOIN pi ON pi.id = e.plaque_record_id
);
