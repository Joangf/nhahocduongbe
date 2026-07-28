package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.ExamSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.AssessmentUpdateDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ImageUpdateDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamService;

@DisplayName("ExamController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class ExamControllerTest {

  @Mock ExamService examService;
  @InjectMocks ExamController controller;

  private ExamDTO mockExamDto() {
    return mock(ExamDTO.class);
  }

  @Nested
  @DisplayName("GET /api/patients/{id}/exams")
  class GetExamsByPatient {

    @Test
    @DisplayName("Trả về danh sách exam theo patientId")
    void shouldReturnExamsByPatient() {
      when(examService.getExamsByPatientIdAndStatus(1L, true)).thenReturn(List.of(mockExamDto()));
      assertThat(controller.getExamsByPatientId(1L, true)).hasSize(1);
    }
  }

  @Nested
  @DisplayName("GET /api/patients/{id}/exams/{examId}")
  class GetExamById {

    @Test
    @DisplayName("Trả về exam theo patientId và examId")
    void shouldReturnExamById() {
      ExamDTO dto = mockExamDto();
      when(examService.getExamByIdAndPatientIdAndStatus(10L, 1L, true)).thenReturn(dto);
      assertThat(controller.getExamByExamId(1L, 10L, true)).isSameAs(dto);
    }
  }

  @Nested
  @DisplayName("POST /api/patients/{id}/exams")
  class CreateExam {

    @Test
    @DisplayName("Tạo exam mới")
    void shouldCreateExam() {
      ExamDTO input = mockExamDto();
      ExamDTO created = mockExamDto();
      when(examService.createExam(input)).thenReturn(created);

      var result = controller.createExam(1L, input);

      verify(input).setPatientId(1L);
      assertThat(result).isSameAs(created);
    }
  }

  @Nested
  @DisplayName("PUT /api/patients/{id}/exams")
  class UpdateExam {

    @Test
    @DisplayName("Cập nhật exam")
    void shouldUpdateExam() {
      ExamDTO input = mockExamDto();
      ExamDTO updated = mockExamDto();
      when(examService.updateExam(input)).thenReturn(updated);

      var result = controller.updateExam(1L, input);

      verify(input).setPatientId(1L);
      assertThat(result).isSameAs(updated);
    }
  }

  @Nested
  @DisplayName("DELETE /api/exams/{id}")
  class DeleteExam {

    @Test
    @DisplayName("Xóa exam")
    void shouldDeleteExam() {
      when(examService.delete(1L)).thenReturn(true);
      assertThat(controller.deleteExam(1L)).isTrue();
    }
  }

  @Nested
  @DisplayName("GET /api/patients/{id}/exams/search")
  class Search {

    @Test
    @DisplayName("Tìm kiếm exam với criteria")
    void shouldSearchExams() {
      ExamSearchCriteria criteria = new ExamSearchCriteria();
      Page<ExamDTO> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
      when(examService.search(any(ExamSearchCriteria.class), any())).thenReturn(page);

      var result = controller.search(criteria, 1L, PageRequest.of(0, 10));

      assertThat(result.getContent()).isEmpty();
      assertThat(criteria.getPatientId()).isEqualTo(1L);
    }
  }

  @Nested
  @DisplayName("GET /api/exams/re-exams")
  class GetReExams {

    @Test
    @DisplayName("Trả về danh sách tái khám")
    void shouldReturnReExams() {
      when(examService.getReExams()).thenReturn(List.of());
      assertThat(controller.getReExams()).isEmpty();
    }
  }

  @Nested
  @DisplayName("PATCH /api/exams/{examId}/assessment")
  class UpdateAssessment {

    @Test
    @DisplayName("Cập nhật đánh giá bệnh lý")
    void shouldUpdateAssessment() {
      AssessmentUpdateDTO dto = mock(AssessmentUpdateDTO.class);
      ExamDTO result = mockExamDto();
      when(examService.updateAssessment(1L, dto)).thenReturn(result);

      assertThat(controller.updateAssessment(1L, dto)).isSameAs(result);
    }
  }

  @Nested
  @DisplayName("PATCH /api/exams/{examId}/images")
  class UpdateImages {

    @Test
    @DisplayName("Cập nhật ảnh")
    void shouldUpdateImages() {
      ImageUpdateDTO dto = mock(ImageUpdateDTO.class);
      ExamDTO result = mockExamDto();
      when(examService.updateImages(1L, dto)).thenReturn(result);

      assertThat(controller.updateImages(1L, dto)).isSameAs(result);
    }
  }

  @Nested
  @DisplayName("DELETE /api/exams/{examId}/images/{side}")
  class ClearImage {

    @Test
    @DisplayName("Xóa ảnh")
    void shouldClearImage() {
      ExamDTO result = mockExamDto();
      when(examService.clearImage(1L, "before")).thenReturn(result);

      assertThat(controller.clearImage(1L, "before")).isSameAs(result);
    }
  }
}
