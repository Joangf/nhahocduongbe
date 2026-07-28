package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PlaqueRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.PlaqueRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.PlaqueRecordMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PlaqueRecordRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.PlaqueRecordServiceImpl;

@DisplayName("PlaqueRecordServiceImpl — Unit Tests")
@ExtendWith(MockitoExtension.class)
class PlaqueRecordServiceImplUnitTest {

  @Mock PlaqueRecordRepository plaqueRecordRepository;
  @Mock ExamRepository examRepository;
  @Mock PlaqueRecordMapper plaqueRecordMapper;
  @InjectMocks PlaqueRecordServiceImpl service;

  @Nested
  @DisplayName("TC-01 getPlaqueRecordByPatientIdAndExamId()")
  class GetPlaqueRecordByPatientAndExam {

    @Test
    @DisplayName("Trả về null khi exam không tồn tại")
    void shouldReturnNullWhenExamNotFound() {
      when(examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(1L, true))
          .thenReturn(List.of());

      PlaqueRecordDTO result = service.getPlaqueRecordByPatientIdAndExamId(1L, 99L);

      assertThat(result).isNull();
    }

    @Test
    @DisplayName("Trả về DTO khi tìm thấy exam có plaque record")
    void shouldReturnDtoWhenExamHasRecord() {
      Exam exam = new Exam();
      exam.setId(1L);
      PlaqueRecord record = new PlaqueRecord();
      record.setId(10L);
      exam.setPlaqueRecord(record);
      PlaqueRecordDTO dto = mock(PlaqueRecordDTO.class);

      when(examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(1L, true))
          .thenReturn(List.of(exam));
      when(plaqueRecordMapper.toDto(record)).thenReturn(dto);

      PlaqueRecordDTO result = service.getPlaqueRecordByPatientIdAndExamId(1L, 1L);

      assertThat(result).isNotNull();
    }
  }

  @Nested
  @DisplayName("TC-02 upsertPlaqueRecord()")
  class UpsertPlaqueRecord {

    @Test
    @DisplayName("Lưu và trả về DTO")
    void shouldSaveAndReturnDto() {
      PlaqueRecordDTO inputDto = mock(PlaqueRecordDTO.class);
      var entity = new PlaqueRecord();
      var saved = new PlaqueRecord();
      saved.setId(1L);
      PlaqueRecordDTO outputDto = mock(PlaqueRecordDTO.class);

      when(plaqueRecordMapper.toEntity(inputDto)).thenReturn(entity);
      when(plaqueRecordRepository.save(entity)).thenReturn(saved);
      when(plaqueRecordMapper.toDto(saved)).thenReturn(outputDto);

      PlaqueRecordDTO result = service.upsertPlaqueRecord(inputDto);

      assertThat(result).isNotNull();
    }
  }
}
