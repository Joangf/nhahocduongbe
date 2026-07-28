package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TartarRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TartarRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TartarRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.TartarRecordMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.TartarRecordRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.TartarRecordServiceImpl;

@DisplayName("TartarRecordServiceImpl — Unit Tests")
@ExtendWith(MockitoExtension.class)
class TartarRecordServiceImplUnitTest {

  @Mock TartarRecordRepository tartarRecordRepository;
  @Mock ExamRepository examRepository;
  @Mock TartarRecordMapper tartarRecordMapper;
  @InjectMocks TartarRecordServiceImpl service;

  @Nested
  @DisplayName("TC-01 getTartarRecordByPatientIdAndExamId()")
  class GetTartarRecordByPatientAndExam {

    @Test
    @DisplayName("Trả về null khi exam không tồn tại")
    void shouldReturnNullWhenExamNotFound() {
      when(examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(1L, true))
          .thenReturn(List.of());

      TartarRecordDTO result = service.getTartarRecordByPatientIdAndExamId(1L, 99L);

      assertThat(result).isNull();
    }

    @Test
    @DisplayName("Trả về DTO khi tìm thấy exam có tartar record")
    void shouldReturnDtoWhenExamHasRecord() {
      Exam exam = new Exam();
      exam.setId(1L);
      vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TartarRecord record =
          new vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TartarRecord();
      record.setId(10L);
      exam.setTartarRecord(record);
      TartarRecordDTO dto = mock(TartarRecordDTO.class);

      when(examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(1L, true))
          .thenReturn(List.of(exam));
      when(tartarRecordMapper.toDto(record)).thenReturn(dto);

      TartarRecordDTO result = service.getTartarRecordByPatientIdAndExamId(1L, 1L);

      assertThat(result).isNotNull();
    }
  }

  @Nested
  @DisplayName("TC-02 upsertTartarRecord()")
  class UpsertTartarRecord {

    @Test
    @DisplayName("Lưu và trả về DTO")
    void shouldSaveAndReturnDto() {
      TartarRecordDTO inputDto = mock(TartarRecordDTO.class);
      TartarRecord entity = new TartarRecord();
      TartarRecord saved = new TartarRecord();
      saved.setId(1L);
      TartarRecordDTO outputDto = mock(TartarRecordDTO.class);

      when(tartarRecordMapper.toEntity(inputDto)).thenReturn(entity);
      when(tartarRecordRepository.save(entity)).thenReturn(saved);
      when(tartarRecordMapper.toDto(saved)).thenReturn(outputDto);

      TartarRecordDTO result = service.upsertTartarRecord(inputDto);

      assertThat(result).isNotNull();
    }
  }
}
