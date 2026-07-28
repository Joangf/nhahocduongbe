package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.StudentExamReportService;

@DisplayName("StudentExamReportController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class StudentExamReportControllerTest {

  @Mock StudentExamReportService studentExamReportService;
  @InjectMocks StudentExamReportController controller;

  @Nested
  @DisplayName("GET /api/exams/{examId}/report/pdf")
  class ExportExamReportPdf {

    @Test
    @DisplayName("Export phiếu khám PDF")
    void shouldExportPdf() {
      byte[] pdfData = new byte[]{0x25, 0x50, 0x44, 0x46}; // %PDF
      when(studentExamReportService.generateExamReportPdf(1L)).thenReturn(pdfData);

      var response = controller.exportExamReportPdf(1L);

      assertThat(response.getStatusCode().value()).isEqualTo(200);
      assertThat(response.getBody()).isEqualTo(pdfData);
      assertThat(response.getHeaders().getContentType().toString()).isEqualTo("application/pdf");
      assertThat(response.getHeaders().getContentDisposition().getFilename())
          .isEqualTo("phieu_kham_1.pdf");
    }
  }
}
