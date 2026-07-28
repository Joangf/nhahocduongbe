package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.OrganizationRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;

@DisplayName("SchoolReportServiceImpl — Unit Tests")
@ExtendWith(MockitoExtension.class)
class SchoolReportServiceImplTest {

  @Mock OrganizationRepository organizationRepository;
  @Mock PatientRepository patientRepository;
  @InjectMocks SchoolReportServiceImpl service;

  @Nested
  @DisplayName("TC-01 exportAllSchoolsExcel()")
  class ExportAllSchoolsExcel {

    @Test
    @DisplayName("Trả về Excel byte array khi có dữ liệu")
    void shouldReturnExcelBytes() {
      java.util.List<Object[]> mockData = java.util.Collections.singletonList(
          new Object[]{1L, "Trường A", 100L, 80L, 0.8});
      when(organizationRepository.findSchoolStatsRaw()).thenReturn(mockData);

      byte[] result = service.exportAllSchoolsExcel();

      assertThat(result).isNotEmpty();
      // Excel files start with PK (ZIP magic number for OOXML)
      assertThat(result).startsWith((byte) 0x50, (byte) 0x4B);
    }
  }

  @Nested
  @DisplayName("TC-02 exportSchoolStudentsExcel()")
  class ExportSchoolStudentsExcel {

    @Test
    @DisplayName("Trả về Excel byte array cho một trường")
    void shouldReturnExcelBytes() {
      java.util.List<Object[]> mockData = java.util.Arrays.asList(
          new Object[]{1L, "Nguyễn Văn A", "HS001", "5A", "0909000001",
              10L, java.sql.Date.valueOf(java.time.LocalDate.of(2026, 6, 15)),
              "Phòng khám A", "EXAMINED"},
          new Object[]{2L, "Trần Thị B", "HS002", "5B", "0909000002",
              null, null, null, "NOT_EXAMINED"});
      when(patientRepository.findStudentsWithExamStatusBySchoolId(1L))
          .thenReturn(mockData);

      byte[] result = service.exportSchoolStudentsExcel(1L, "Trường TH A");

      assertThat(result).isNotEmpty();
      assertThat(result).startsWith((byte) 0x50, (byte) 0x4B);
    }

    @Test
    @DisplayName("Trả về Excel khi không có học sinh")
    void shouldReturnExcelWhenNoData() {
      when(patientRepository.findStudentsWithExamStatusBySchoolId(99L))
          .thenReturn(java.util.Collections.emptyList());

      byte[] result = service.exportSchoolStudentsExcel(99L, "Trường rỗng");

      assertThat(result).isNotEmpty();
    }
  }
}
