package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PlaqueRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.PlaqueRecordService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamServiceImpl;

@DisplayName("PlaqueRecordController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class PlaqueRecordControllerTest {

  @Mock PlaqueRecordService plaqueRecordService;
  @Mock ExamServiceImpl examService;
  @InjectMocks PlaqueRecordController controller;

  @Nested
  @DisplayName("GET /api/patients/{id}/exams/{examId}/plaqueRecord")
  class GetPlaqueRecord {

    @Test
    @DisplayName("Trả về plaque record")
    void shouldReturnPlaqueRecord() {
      PlaqueRecordDTO dto = mock(PlaqueRecordDTO.class);
      when(plaqueRecordService.getPlaqueRecordByPatientIdAndExamId(1L, 10L)).thenReturn(dto);

      assertThat(controller.getPlaqueRecord(1L, 10L)).isSameAs(dto);
    }
  }

  @Nested
  @DisplayName("POST /api/patients/{id}/exams/{examId}/plaqueRecord")
  class UpsertPlaqueRecord {

    @Test
    @DisplayName("Upsert plaque record và cập nhật exam")
    void shouldUpsertAndUpdateExam() {
      PlaqueRecordDTO input = mock(PlaqueRecordDTO.class);
      PlaqueRecordDTO saved = mock(PlaqueRecordDTO.class);
      when(saved.id()).thenReturn(99L);
      when(plaqueRecordService.upsertPlaqueRecord(input)).thenReturn(saved);

      var result = controller.upsertPlaqueRecordOfExam(1L, 10L, input);

      verify(examService).updatePlaqueRecordIdOfExam(10L, 99L);
      assertThat(result).isSameAs(saved);
    }
  }
}
