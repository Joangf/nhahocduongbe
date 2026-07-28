package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.SchoolReportService;

@DisplayName("SchoolReportController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class SchoolReportControllerTest {

  @Mock SchoolReportService schoolReportService;
  @InjectMocks SchoolReportController controller;

  @Nested
  @DisplayName("GET /api/schools/export/excel")
  class ExportAllSchoolsExcel {

    @Test
    @DisplayName("Export tất cả trường ra Excel")
    void shouldExportAllSchools() {
      byte[] data = new byte[]{0x50, 0x4B, 0x03, 0x04};
      when(schoolReportService.exportAllSchoolsExcel()).thenReturn(data);

      var response = controller.exportAllSchoolsExcel();

      assertThat(response.getStatusCode().value()).isEqualTo(200);
      assertThat(response.getBody()).isEqualTo(data);
      assertThat(response.getHeaders().getContentDisposition().getFilename())
          .isEqualTo("Tong_hop_cac_truong.xlsx");
    }
  }

  @Nested
  @DisplayName("GET /api/schools/{schoolId}/students/export/excel")
  class ExportSchoolStudentsExcel {

    @Test
    @DisplayName("Export học sinh của trường ra Excel")
    void shouldExportSchoolStudents() {
      byte[] data = new byte[]{0x50, 0x4B, 0x03, 0x04};
      when(schoolReportService.exportSchoolStudentsExcel(1L, "Trường A")).thenReturn(data);

      var response = controller.exportSchoolStudentsExcel(1L, "Trường A");

      assertThat(response.getStatusCode().value()).isEqualTo(200);
      assertThat(response.getBody()).isEqualTo(data);
      assertThat(response.getHeaders().getContentDisposition().getFilename())
          .isEqualTo("DS_HocSinh_1.xlsx");
    }

    @Test
    @DisplayName("Export học sinh với schoolName rỗng")
    void shouldExportWithEmptySchoolName() {
      byte[] data = new byte[]{};
      when(schoolReportService.exportSchoolStudentsExcel(2L, "")).thenReturn(data);

      var response = controller.exportSchoolStudentsExcel(2L, "");

      assertThat(response.getStatusCode().value()).isEqualTo(200);
    }
  }
}
