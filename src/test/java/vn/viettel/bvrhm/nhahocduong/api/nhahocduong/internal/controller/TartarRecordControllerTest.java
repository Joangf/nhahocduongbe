package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TartarRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.TartarRecordService;

@DisplayName("TartarRecordController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class TartarRecordControllerTest {

  @Mock TartarRecordService tartarRecordService;
  @Mock ExamService examService;
  @InjectMocks TartarRecordController controller;

  @Nested
  @DisplayName("GET /api/patients/{id}/exams/{examId}/tartarRecord")
  class GetTartarRecord {

    @Test
    @DisplayName("Trả về tartar record")
    void shouldReturnTartarRecord() {
      TartarRecordDTO dto = mock(TartarRecordDTO.class);
      when(tartarRecordService.getTartarRecordByPatientIdAndExamId(1L, 10L)).thenReturn(dto);

      assertThat(controller.getTartarRecord(1L, 10L)).isSameAs(dto);
    }
  }

  @Nested
  @DisplayName("POST /api/patients/{id}/exams/{examId}/tartarRecord")
  class UpsertTartarRecord {

    @Test
    @DisplayName("Upsert tartar record và cập nhật exam")
    void shouldUpsertAndUpdateExam() {
      TartarRecordDTO input = mock(TartarRecordDTO.class);
      TartarRecordDTO saved = mock(TartarRecordDTO.class);
      when(saved.id()).thenReturn(88L);
      when(tartarRecordService.upsertTartarRecord(input)).thenReturn(saved);

      var result = controller.upsertTartarRecordOfExam(1L, 10L, input);

      verify(examService).updateTartarRecordIdOfExam(10L, 88L);
      assertThat(result).isSameAs(saved);
    }
  }
}
