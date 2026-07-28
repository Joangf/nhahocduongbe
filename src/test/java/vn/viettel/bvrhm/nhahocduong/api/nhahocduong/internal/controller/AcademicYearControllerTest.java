package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.AcademicYearDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TransitionResultDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.YearTransitionRequest;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.AcademicYearService;

@DisplayName("AcademicYearController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class AcademicYearControllerTest {

  @Mock AcademicYearService academicYearService;
  @InjectMocks AcademicYearController controller;

  @Nested
  @DisplayName("CRUD")
  class Crud {

    @Test
    @DisplayName("GET — trả về tất cả năm học")
    void shouldGetAll() {
      when(academicYearService.getAll()).thenReturn(List.of());
      assertThat(controller.getAll().getBody()).isEmpty();
    }

    @Test
    @DisplayName("GET /{id} — trả về năm học theo id")
    void shouldGetById() {
      AcademicYearDTO dto = mock(AcademicYearDTO.class);
      when(academicYearService.getById(1L)).thenReturn(dto);
      assertThat(controller.getById(1L).getBody()).isSameAs(dto);
    }

    @Test
    @DisplayName("GET /current — trả về năm học hiện tại")
    void shouldGetCurrentYear() {
      AcademicYearDTO dto = mock(AcademicYearDTO.class);
      when(academicYearService.getCurrentYear()).thenReturn(dto);
      assertThat(controller.getCurrentYear().getBody()).isSameAs(dto);
    }

    @Test
    @DisplayName("POST — tạo năm học mới")
    void shouldCreate() {
      AcademicYearDTO input = mock(AcademicYearDTO.class);
      AcademicYearDTO created = mock(AcademicYearDTO.class);
      when(academicYearService.create(input)).thenReturn(created);
      assertThat(controller.create(input).getBody()).isSameAs(created);
    }

    @Test
    @DisplayName("PUT /{id} — cập nhật năm học")
    void shouldUpdate() {
      AcademicYearDTO dto = mock(AcademicYearDTO.class);
      when(academicYearService.update(1L, dto)).thenReturn(dto);
      assertThat(controller.update(1L, dto).getBody()).isSameAs(dto);
    }

    @Test
    @DisplayName("DELETE /{id} — xóa năm học, trả về 204")
    void shouldDelete() {
      var result = controller.delete(1L);
      assertThat(result.getStatusCode().value()).isEqualTo(204);
      verify(academicYearService).delete(1L);
    }
  }

  @Nested
  @DisplayName("Transition")
  class Transition {

    @Test
    @DisplayName("POST /validate/{id} — kiểm tra trước chuyển năm")
    void shouldValidate() {
      when(academicYearService.validateBeforeTransition(1L)).thenReturn(List.of());
      assertThat(controller.validateBeforeTransition(1L).getBody()).isEmpty();
    }

    @Test
    @DisplayName("POST /transition — chuyển năm mới")
    void shouldTransition() {
      YearTransitionRequest req = mock(YearTransitionRequest.class);
      TransitionResultDTO dto = mock(TransitionResultDTO.class);
      when(academicYearService.transitionToNewYear(req)).thenReturn(dto);
      assertThat(controller.transition(req).getBody()).isSameAs(dto);
    }

    @Test
    @DisplayName("POST /rollback/{sessionId} — rollback chuyển năm")
    void shouldRollback() {
      TransitionResultDTO dto = mock(TransitionResultDTO.class);
      when(academicYearService.rollbackTransition("session-1")).thenReturn(dto);
      assertThat(controller.rollback("session-1").getBody()).isSameAs(dto);
    }

    @Test
    @DisplayName("GET /history — lịch sử chuyển năm")
    void shouldGetHistory() {
      when(academicYearService.getTransitionHistory()).thenReturn(List.of(Map.of()));
      assertThat(controller.getTransitionHistory().getBody()).hasSize(1);
    }
  }
}
