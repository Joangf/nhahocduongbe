package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;

@DisplayName("StudentExamReportServiceImpl — Unit Tests")
@ExtendWith(MockitoExtension.class)
class StudentExamReportServiceImplTest {

  @Mock ExamRepository examRepository;
  @Mock PatientRepository patientRepository;
  @InjectMocks StudentExamReportServiceImpl service;

  @Nested
  @DisplayName("TC-01 generateExamReportPdf() — error paths")
  class GenerateReportErrorPaths {

    @Test
    @DisplayName("Ném 404 khi exam không tồn tại")
    void shouldThrowWhenExamNotFound() {
      when(examRepository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.generateExamReportPdf(99L))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("Không tìm thấy phiếu khám");
    }
  }
}
