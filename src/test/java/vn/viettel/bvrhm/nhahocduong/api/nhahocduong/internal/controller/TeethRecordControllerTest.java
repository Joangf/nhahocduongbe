package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TeethRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.TeethRecordService;

@DisplayName("TeethRecordController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class TeethRecordControllerTest {

  @Mock TeethRecordService teethRecordService;
  @Mock ExamService examService;
  @InjectMocks TeethRecordController controller;

  private TeethRecordDTO mockDto() {
    return mock(TeethRecordDTO.class);
  }

  @Nested
  @DisplayName("GET /api/patients/{id}/exams/{examId}/teethRecord")
  class GetTeethRecord {

    @Test
    @DisplayName("Trả về teeth record theo patient và exam")
    void shouldReturnTeethRecord() {
      TeethRecordDTO dto = mockDto();
      when(teethRecordService.getTeethRecordByPatientIdAndExamId(1L, 10L)).thenReturn(dto);
      assertThat(controller.getTeethRecord(1L, 10L)).isSameAs(dto);
    }
  }

  @Nested
  @DisplayName("POST /api/patients/{id}/exams/{examId}/teethRecord")
  class UpsertTeethRecord {

    @Test
    @DisplayName("Upsert teeth record và cập nhật exam")
    void shouldUpsertAndUpdateExam() {
      TeethRecordDTO input = mockDto();
      TeethRecordDTO saved = mockDto();
      when(saved.id()).thenReturn(77L);
      when(teethRecordService.upsertTeethRecord(input)).thenReturn(saved);

      var result = controller.upsertTeethRecord(1L, 10L, input);

      verify(examService).updateTeethRecordIdOfExam(10L, 77L);
      assertThat(result).isSameAs(saved);
    }
  }

  @Nested
  @DisplayName("POST /api/teethRecord")
  class CreateTeethRecord {

    @Test
    @DisplayName("Tạo teeth record mới")
    void shouldCreateTeethRecord() {
      TeethRecordDTO input = mockDto();
      TeethRecordDTO saved = mockDto();
      when(teethRecordService.upsertTeethRecord(input)).thenReturn(saved);

      assertThat(controller.createTeethRecord(input)).isSameAs(saved);
    }
  }

  @Nested
  @DisplayName("GET /api/teethRecord/{id}")
  class GetTeethRecordById {

    @Test
    @DisplayName("Trả về teeth record theo id")
    void shouldGetById() {
      TeethRecordDTO dto = mockDto();
      when(teethRecordService.getTeethRecordById(1L)).thenReturn(dto);
      assertThat(controller.getTeethRecordById(1L)).isSameAs(dto);
    }
  }
}
