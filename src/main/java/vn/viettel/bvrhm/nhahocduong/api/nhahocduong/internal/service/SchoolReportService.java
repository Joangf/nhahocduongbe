package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

public interface SchoolReportService {
  byte[] exportAllSchoolsExcel();

  byte[] exportSchoolStudentsExcel(Long schoolId, String schoolName);
}
