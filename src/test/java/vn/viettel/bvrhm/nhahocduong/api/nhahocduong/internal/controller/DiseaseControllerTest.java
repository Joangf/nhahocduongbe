package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DiseaseDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.DiseaseServiceImpl;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamServiceImpl;

@DisplayName("DiseaseController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class DiseaseControllerTest {

  @Mock ExamServiceImpl examService;
  @Mock DiseaseServiceImpl diseaseService;
  @InjectMocks DiseaseController controller;

  @Nested
  @DisplayName("GET /api/patients/{id}/exams/{examId}/chronicConditions")
  class GetChronicDiseaseList {

    @Test
    @DisplayName("Trả về danh sách mã bệnh mãn tính")
    void shouldReturnChronicDiseaseCodes() {
      when(examService.getChronicDiseasesCodesByExamId(10L)).thenReturn(List.of("D01", "D02"));

      var result = controller.getChronicDiseaseListOfUserExam(1L, 10L);

      assertThat(result).containsExactly("D01", "D02");
    }
  }

  @Nested
  @DisplayName("POST /api/patients/{id}/exams/{examId}/chronicConditions")
  class UpdateChronicDiseaseList {

    @Test
    @DisplayName("Cập nhật danh sách mã bệnh mãn tính")
    void shouldUpdateChronicDiseaseCodes() {
      List<String> input = List.of("D01");
      when(examService.updateChronicDiseasesCodesByExamId(10L, input)).thenReturn(List.of("D01"));

      var result = controller.updateChronicDiseaseListOfUserExam(1L, 10L, input);

      assertThat(result).containsExactly("D01");
    }
  }

  @Nested
  @DisplayName("GET /api/diseases")
  class GetAllDiseases {

    @Test
    @DisplayName("Trả về tất cả bệnh")
    void shouldReturnAllDiseases() {
      DiseaseDTO dto = mock(DiseaseDTO.class);
      when(diseaseService.getAllDiseases()).thenReturn(List.of(dto));

      var result = controller.getAllDiseases();

      assertThat(result).hasSize(1);
    }
  }
}
