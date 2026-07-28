package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TreatmentRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TreatmentRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.TreatmentRecordMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.TreatmentRecordRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.TreatmentRecordServiceImpl;

@DisplayName("TreatmentRecordServiceImpl — Unit Tests")
@ExtendWith(MockitoExtension.class)
class TreatmentRecordServiceImplUnitTest {

  @Mock ExamService examService;
  @Mock TreatmentRecordRepository treatmentRecordRepository;
  @Mock TreatmentRecordMapper treatmentRecordMapper;
  @InjectMocks TreatmentRecordServiceImpl service;

  @Nested
  @DisplayName("TC-01 getTreatmentRecordsByExamId()")
  class GetTreatmentRecordsByExamId {

    @Test
    @DisplayName("Trả về danh sách treatment records của exam")
    void shouldReturnTreatmentRecords() {
      TreatmentRecord entity = new TreatmentRecord();
      TreatmentRecordDTO dto = new TreatmentRecordDTO();

      when(treatmentRecordRepository.findByExamIdAndStatus(1L, true))
          .thenReturn(List.of(entity));
      when(treatmentRecordMapper.toListDto(List.of(entity))).thenReturn(List.of(dto));

      List<TreatmentRecordDTO> result = service.getTreatmentRecordsByExamId(1L);

      assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Trả về list rỗng khi không có treatment record")
    void shouldReturnEmptyList() {
      when(treatmentRecordRepository.findByExamIdAndStatus(1L, true))
          .thenReturn(List.of());

      List<TreatmentRecordDTO> result = service.getTreatmentRecordsByExamId(1L);

      assertThat(result).isEmpty();
    }
  }

  @Nested
  @DisplayName("TC-02 upsertTreatmentRecordsByExamIdAndPatientId()")
  class UpsertTreatmentRecords {

    @Test
    @DisplayName("Upsert thành công khi exam tồn tại")
    void shouldUpsertSuccessfully() {
      ExamDTO examDTO = new ExamDTO();
      examDTO.setId(1L);
      TreatmentRecordDTO inputDto = new TreatmentRecordDTO();
      inputDto.setExamId(1L);
      TreatmentRecord entity = new TreatmentRecord();
      TreatmentRecord saved = new TreatmentRecord();
      saved.setId(10L);
      TreatmentRecordDTO outputDto = new TreatmentRecordDTO();
      outputDto.setId(10L);

      when(examService.getExamByIdAndPatientIdAndStatus(1L, 1L, true))
          .thenReturn(examDTO);
      when(treatmentRecordMapper.toListEntity(List.of(inputDto))).thenReturn(List.of(entity));
      when(treatmentRecordRepository.findByIdIsIn(anyList())).thenReturn(List.of());
      when(treatmentRecordRepository.saveAll(anyList()))
          .thenReturn(List.of(saved));
      when(treatmentRecordMapper.toListDto(List.of(saved))).thenReturn(List.of(outputDto));

      List<TreatmentRecordDTO> result =
          service.upsertTreatmentRecordsByExamIdAndPatientId(1L, 1L, List.of(inputDto));

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Ném 404 khi exam không tồn tại")
    void shouldThrowWhenExamNotFound() {
      when(examService.getExamByIdAndPatientIdAndStatus(1L, 1L, true))
          .thenReturn(null);

      assertThatThrownBy(() ->
          service.upsertTreatmentRecordsByExamIdAndPatientId(1L, 1L, List.of()))
          .isInstanceOf(ResponseStatusException.class);
    }
  }

  @Nested
  @DisplayName("TC-03 deleteTreatmentRecord()")
  class DeleteTreatmentRecord {

    @Test
    @DisplayName("Ném 404 khi exam không tồn tại")
    void shouldThrowWhenExamNotFound() {
      when(examService.getExamByIdAndPatientIdAndStatus(1L, 1L, true))
          .thenReturn(null);

      assertThatThrownBy(() -> service.deleteTreatmentRecord(1L, 1L, 99L))
          .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("Xóa thành công treatment record")
    void shouldDeleteSuccessfully() {
      ExamDTO examDTO = new ExamDTO();
      examDTO.setId(1L);
      TreatmentRecordDTO existingRecord = new TreatmentRecordDTO();
      existingRecord.setId(99L);
      examDTO.setTreatmentRecords(List.of(existingRecord));
      TreatmentRecord entity = new TreatmentRecord();

      when(examService.getExamByIdAndPatientIdAndStatus(1L, 1L, true)).thenReturn(examDTO);
      when(treatmentRecordRepository.getReferenceById(99L)).thenReturn(entity);

      boolean result = service.deleteTreatmentRecord(1L, 1L, 99L);

      assertThat(result).isTrue();
      assertThat(entity.getStatus()).isFalse();
      verify(treatmentRecordRepository).save(entity);
    }
  }
}
