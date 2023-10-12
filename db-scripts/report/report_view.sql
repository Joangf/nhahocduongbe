CREATE
OR REPLACE VIEW nhahocduong_report.report_view AS(
	SELECT * FROM nhahocduong_report.nhahocduong_report
	UNION ALL
	SELECT * FROM nhahocduong_report.app_view
);