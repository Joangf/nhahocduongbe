package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.PatientSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PatientDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.PatientService;

@DisplayName("PatientController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

  @Mock PatientService patientService;
  @InjectMocks PatientController controller;

  @Mock HttpServletResponse response;

  private PatientDTO mockPatientDto() {
    return mock(PatientDTO.class);
  }

  @Nested
  @DisplayName("POST /api/patient")
  class CreatePatient {

    @Test
    @DisplayName("Tạo bệnh nhân mới")
    void shouldCreatePatient() {
      PatientDTO input = mockPatientDto();
      PatientDTO created = mockPatientDto();
      when(patientService.createPatient(input)).thenReturn(created);

      assertThat(controller.createPatient(input)).isSameAs(created);
    }
  }

  @Nested
  @DisplayName("GET /api/patient/search")
  class SearchPatients {

    @Test
    @DisplayName("Tìm kiếm bệnh nhân")
    void shouldSearchPatients() {
      PatientSearchCriteria criteria = new PatientSearchCriteria();
      Page<PatientDTO> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
      when(patientService.getPatientsByCondition(any(), any())).thenReturn(page);

      var result = controller.getPatientsByCondition(criteria, PageRequest.of(0, 10));
      assertThat(result.getContent()).isEmpty();
    }
  }

  @Nested
  @DisplayName("PUT /api/patient/{id}")
  class UpdatePatient {

    @Test
    @DisplayName("Cập nhật bệnh nhân")
    void shouldUpdatePatient() {
      PatientDTO dto = mockPatientDto();
      when(patientService.updatePatient(dto, 1L)).thenReturn(dto);
      assertThat(controller.updatePatient(dto, 1L)).isSameAs(dto);
    }
  }

  @Nested
  @DisplayName("GET /api/patient/{id}")
  class GetPatientById {

    @Test
    @DisplayName("Trả về bệnh nhân theo id")
    void shouldGetById() {
      PatientDTO dto = mockPatientDto();
      when(patientService.getPatientById(1L)).thenReturn(dto);
      assertThat(controller.getPatientById(1L)).isSameAs(dto);
    }
  }

  @Nested
  @DisplayName("DELETE /api/patient/{id}")
  class DeletePatient {

    @Test
    @DisplayName("Xóa bệnh nhân")
    void shouldDeletePatient() {
      when(patientService.deletePatientById(1L)).thenReturn(true);
      assertThat(controller.deletePatientById(1L)).isTrue();
    }
  }

  @Nested
  @DisplayName("GET /api/patient")
  class GetAllPatients {

    @Test
    @DisplayName("Trả về danh sách bệnh nhân phân trang")
    void shouldGetAllPatients() {
      Page<PatientDTO> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
      when(patientService.getAllPatients(any())).thenReturn(page);

      var result = controller.getPatientsAll(PageRequest.of(0, 10));
      assertThat(result.getContent()).isEmpty();
    }
  }

  @Nested
  @DisplayName("POST /api/patient/excel")
  class ImportExcel {

    @Test
    @DisplayName("Import bệnh nhân từ Excel")
    void shouldImportFromExcel() throws IOException {
      MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/octet-stream", new byte[0]);
      when(patientService.importPatientsFromExcel(file)).thenReturn(List.of());

      assertThat(controller.importPatientsFromExcel(file)).isEmpty();
    }
  }

  @Nested
  @DisplayName("GET /api/patient/excel")
  class ExportExcel {

    @Test
    @DisplayName("Export bệnh nhân ra Excel")
    void shouldExportExcel() throws IOException {
      byte[] data = new byte[]{1, 2, 3};
      when(patientService.exportPatients(response)).thenReturn(data);

      assertThat(controller.exportPatients(response)).isEqualTo(data);
    }
  }

  @Nested
  @DisplayName("GET /api/patient/excel/template")
  class GetTemplate {

    @Test
    @DisplayName("Tải file template Excel")
    void shouldGetTemplate() throws IOException {
      byte[] data = new byte[]{4, 5, 6};
      when(patientService.generateExcelTemplateFile(response)).thenReturn(data);

      assertThat(controller.getTemplateFile(response)).isEqualTo(data);
    }
  }
}
