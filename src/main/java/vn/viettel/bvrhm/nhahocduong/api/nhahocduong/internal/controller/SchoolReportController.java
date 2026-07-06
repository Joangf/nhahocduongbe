package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.SchoolReportService;

@RestController
@RequestMapping("/api")
public class SchoolReportController {

  @Autowired private SchoolReportService schoolReportService;

  @GetMapping("/schools/export/excel")
  public ResponseEntity<byte[]> exportAllSchoolsExcel() {
    byte[] excelBytes = schoolReportService.exportAllSchoolsExcel();
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=Tong_hop_cac_truong.xlsx")
        .contentType(MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(excelBytes);
  }

  @GetMapping("/schools/{schoolId}/students/export/excel")
  public ResponseEntity<byte[]> exportSchoolStudentsExcel(
      @PathVariable Long schoolId,
      @RequestParam(defaultValue = "") String schoolName) {
    byte[] excelBytes = schoolReportService.exportSchoolStudentsExcel(schoolId, schoolName);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=DS_HocSinh_" + schoolId + ".xlsx")
        .contentType(MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(excelBytes);
  }
}
