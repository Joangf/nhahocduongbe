package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TeethRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TeethRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.TeethRecordMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.TeethRecordRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.TeethRecordServiceImpl;

@DisplayName("TeethRecordServiceImpl — Unit Tests")
@ExtendWith(MockitoExtension.class)
class TeethRecordServiceImplUnitTest {

  @Mock TeethRecordRepository teethRecordRepository;
  @Mock ExamRepository examRepository;
  @Mock TeethRecordMapper teethRecordMapper;
  @InjectMocks TeethRecordServiceImpl service;

  @Nested
  @DisplayName("TC-01 getTeethRecordById()")
  class GetTeethRecordById {

    @Test
    @DisplayName("Trả về DTO khi tìm thấy")
    void shouldReturnDtoWhenFound() {
      TeethRecord entity = new TeethRecord();
      entity.setId(1L);
      TeethRecordDTO dto = new TeethRecordDTO(1L, null);

      when(teethRecordRepository.findById(1L)).thenReturn(Optional.of(entity));
      when(teethRecordMapper.toDto(entity)).thenReturn(dto);

      TeethRecordDTO result = service.getTeethRecordById(1L);

      assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Trả về null khi không tìm thấy")
    void shouldReturnNullWhenNotFound() {
      when(teethRecordRepository.findById(99L)).thenReturn(Optional.empty());
      when(teethRecordMapper.toDto(null)).thenReturn(null);

      TeethRecordDTO result = service.getTeethRecordById(99L);

      assertThat(result).isNull();
    }
  }

  @Nested
  @DisplayName("TC-02 getTeethRecordByPatientIdAndExamId()")
  class GetTeethRecordByPatientAndExam {

    @Test
    @DisplayName("Trả về null khi exam không tồn tại")
    void shouldReturnNullWhenExamNotFound() {
      when(examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(1L, true))
          .thenReturn(List.of());

      TeethRecordDTO result = service.getTeethRecordByPatientIdAndExamId(1L, 99L);

      assertThat(result).isNull();
    }

    @Test
    @DisplayName("Trả về DTO khi tìm thấy exam có teeth record")
    void shouldReturnDtoWhenExamHasTeethRecord() {
      Exam exam = new Exam();
      exam.setId(1L);
      TeethRecord teethRecord = new TeethRecord();
      teethRecord.setId(10L);
      exam.setTeethRecord(teethRecord);
      TeethRecordDTO dto = new TeethRecordDTO(10L, null);

      when(examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(1L, true))
          .thenReturn(List.of(exam));
      when(teethRecordMapper.toDto(teethRecord)).thenReturn(dto);

      TeethRecordDTO result = service.getTeethRecordByPatientIdAndExamId(1L, 1L);

      assertThat(result.id()).isEqualTo(10L);
    }
  }

  @Nested
  @DisplayName("TC-03 upsertTeethRecord()")
  class UpsertTeethRecord {

    @Test
    @DisplayName("Lưu và trả về DTO")
    void shouldSaveAndReturnDto() {
      TeethRecordDTO inputDto = new TeethRecordDTO(null, null);
      TeethRecord entity = new TeethRecord();
      entity.setId(1L);
      TeethRecordDTO outputDto = new TeethRecordDTO(1L, null);

      when(teethRecordMapper.toEntity(inputDto)).thenReturn(entity);
      when(teethRecordMapper.toDto(entity)).thenReturn(outputDto);

      TeethRecordDTO result = service.upsertTeethRecord(inputDto);

      assertThat(result.id()).isEqualTo(1L);
      verify(teethRecordRepository).save(entity);
    }
  }
}
