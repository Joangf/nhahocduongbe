package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.StudentExamReportService;

@RestController
@RequestMapping("/api")
public class StudentExamReportController {

  @Autowired private StudentExamReportService studentExamReportService;

  @GetMapping("/exams/{examId}/report/pdf")
  public ResponseEntity<byte[]> exportExamReportPdf(@PathVariable Long examId) {
    byte[] pdfBytes = studentExamReportService.generateExamReportPdf(examId);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=phieu_kham_" + examId + ".pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .body(pdfBytes);
  }
}
