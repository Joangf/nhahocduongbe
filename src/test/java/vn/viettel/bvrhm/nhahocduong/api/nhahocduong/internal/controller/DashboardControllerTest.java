package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.StudentCountBySchoolDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.YearlyStudentCountDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.DashboardService;

@DisplayName("DashboardController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

  @Mock DashboardService dashboardService;
  @InjectMocks DashboardController controller;

  @Nested
  @DisplayName("GET /api/dashboard/campaign-stats")
  class GetCampaignStats {

    @Test
    @DisplayName("Trả về campaign stats từ service")
    void shouldReturnCampaignStats() {
      Map<String, Object> expected = Map.of("total", 5, "active", 2);
      when(dashboardService.getCampaignStats()).thenReturn(expected);

      var result = controller.getCampaignStats();

      assertThat(result).isEqualTo(expected);
      verify(dashboardService).getCampaignStats();
    }
  }

  @Nested
  @DisplayName("GET /api/dashboard/stats")
  class GetStats {

    @Test
    @DisplayName("Trả về dashboard stats từ service")
    void shouldReturnStats() {
      Map<String, Object> expected = Map.of("totalExams", 100, "cariesCount", 30);
      when(dashboardService.getStats()).thenReturn(expected);

      var result = controller.getStats();

      assertThat(result).isEqualTo(expected);
      verify(dashboardService).getStats();
    }
  }

  @Nested
  @DisplayName("GET /api/dashboard/student-count")
  class GetStudentCountByYear {

    @Test
    @DisplayName("Trả về danh sách student count theo năm")
    void shouldReturnStudentCountByYear() {
      List<StudentCountBySchoolDTO> expected = List.of();
      when(dashboardService.getStudentCountByYear(1L)).thenReturn(expected);

      var result = controller.getStudentCountByYear(1L);

      assertThat(result).isEqualTo(expected);
      verify(dashboardService).getStudentCountByYear(1L);
    }
  }

  @Nested
  @DisplayName("GET /api/dashboard/student-count-history")
  class GetStudentCountHistory {

    @Test
    @DisplayName("Trả về student count history")
    void shouldReturnStudentCountHistory() {
      List<YearlyStudentCountDTO> expected = List.of();
      when(dashboardService.getStudentCountHistory()).thenReturn(expected);

      var result = controller.getStudentCountHistory();

      assertThat(result).isEqualTo(expected);
      verify(dashboardService).getStudentCountHistory();
    }
  }
}
