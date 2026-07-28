package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TreatmentRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.TreatmentRecordService;

@DisplayName("TreatmentRecordController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class TreatmentRecordControllerTest {

  @Mock TreatmentRecordService treatmentRecordService;
  @InjectMocks TreatmentRecordController controller;

  @Nested
  @DisplayName("GET treatmentRecord")
  class GetTreatmentRecord {

    @Test
    @DisplayName("Trả về treatment records theo examId")
    void shouldReturnTreatmentRecords() {
      when(treatmentRecordService.getTreatmentRecordsByExamId(10L)).thenReturn(List.of());

      var result = controller.getTreatmentRecordByPatientIdAndExamId(1L, 10L);

      assertThat(result).isEmpty();
      verify(treatmentRecordService).getTreatmentRecordsByExamId(10L);
    }
  }

  @Nested
  @DisplayName("POST treatmentRecord")
  class UpsertTreatmentRecord {

    @Test
    @DisplayName("Upsert treatment records")
    void shouldUpsert() {
      List<TreatmentRecordDTO> input = List.of(mock(TreatmentRecordDTO.class));
      when(treatmentRecordService.upsertTreatmentRecordsByExamIdAndPatientId(10L, 1L, input))
          .thenReturn(input);

      var result = controller.upsertTreatmentRecordByPatientIdAndExamId(1L, 10L, input);

      assertThat(result).hasSize(1);
    }
  }

  @Nested
  @DisplayName("DELETE treatmentRecord")
  class DeleteTreatmentRecord {

    @Test
    @DisplayName("Xóa treatment record")
    void shouldDelete() {
      when(treatmentRecordService.deleteTreatmentRecord(10L, 1L, 99L)).thenReturn(true);

      assertThat(controller.deleteTreatmentRecord(1L, 10L, 99L)).isTrue();
    }
  }
}
