package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamCampaignDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamScheduleDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.StudentExamStatusDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamCampaignService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamScheduleService;

@DisplayName("ExamCampaignController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class ExamCampaignControllerTest {

  @Mock ExamCampaignService examCampaignService;
  @Mock ExamScheduleService examScheduleService;
  @InjectMocks ExamCampaignController controller;

  @Nested
  @DisplayName("CRUD Campaigns")
  class CampaignCrud {

    @Test
    @DisplayName("GET — trả về tất cả campaigns")
    void shouldGetAllCampaigns() {
      when(examCampaignService.getAllActiveCampaigns()).thenReturn(List.of());
      assertThat(controller.getAllCampaigns()).isEmpty();
    }

    @Test
    @DisplayName("GET /{id} — trả về campaign theo id")
    void shouldGetCampaignById() {
      ExamCampaignDTO dto = mock(ExamCampaignDTO.class);
      when(examCampaignService.getCampaignById(1L)).thenReturn(dto);
      assertThat(controller.getCampaignById(1L)).isSameAs(dto);
    }

    @Test
    @DisplayName("POST — tạo campaign")
    void shouldCreateCampaign() {
      ExamCampaignDTO input = mock(ExamCampaignDTO.class);
      ExamCampaignDTO created = mock(ExamCampaignDTO.class);
      when(examCampaignService.createCampaign(input)).thenReturn(created);
      assertThat(controller.createCampaign(input)).isSameAs(created);
    }

    @Test
    @DisplayName("PUT /{id} — cập nhật campaign")
    void shouldUpdateCampaign() {
      ExamCampaignDTO dto = mock(ExamCampaignDTO.class);
      when(examCampaignService.updateCampaign(1L, dto)).thenReturn(dto);
      assertThat(controller.updateCampaign(1L, dto)).isSameAs(dto);
    }

    @Test
    @DisplayName("DELETE /{id} — xóa campaign")
    void shouldDeleteCampaign() {
      when(examCampaignService.deleteCampaign(1L)).thenReturn(true);
      assertThat(controller.deleteCampaign(1L)).isTrue();
    }
  }

  @Nested
  @DisplayName("Schedules")
  class Schedules {

    @Test
    @DisplayName("GET /{campaignId}/schedules — trả về danh sách lịch")
    void shouldGetSchedulesByCampaign() {
      when(examScheduleService.getSchedulesByCampaignId(1L)).thenReturn(List.of());
      assertThat(controller.getSchedulesByCampaignId(1L)).isEmpty();
    }

    @Test
    @DisplayName("POST /{campaignId}/schedules — thêm/cập nhật lịch")
    void shouldAddOrUpdateSchedule() {
      ExamScheduleDTO dto = mock(ExamScheduleDTO.class);
      when(examScheduleService.addOrUpdateSchedule(1L, dto)).thenReturn(dto);
      assertThat(controller.addOrUpdateSchedule(1L, dto)).isSameAs(dto);
    }

    @Test
    @DisplayName("DELETE /{campaignId}/schedules/{scheduleId} — xóa lịch")
    void shouldDeleteSchedule() {
      when(examScheduleService.deleteSchedule(1L, 2L)).thenReturn(true);
      assertThat(controller.deleteSchedule(1L, 2L)).isTrue();
    }
  }

  @Nested
  @DisplayName("Students & Notify")
  class StudentsAndNotify {

    @Test
    @DisplayName("GET /{campaignId}/students — trả về danh sách học sinh")
    void shouldGetStudentsByCampaign() {
      when(examCampaignService.getStudentsByCampaignId(1L)).thenReturn(List.of());
      assertThat(controller.getStudentsByCampaignId(1L)).isEmpty();
    }

    @Test
    @DisplayName("POST /{campaignId}/notify — thông báo cho nha sĩ")
    void shouldNotifyDentists() {
      when(examCampaignService.notifyDentists(1L)).thenReturn(3);

      Map<String, Object> result = controller.notifyDentists(1L);

      assertThat(result).containsEntry("notifiedCount", 3);
      assertThat(result).containsEntry("message", "Đã gửi thông báo cho 3 bác sĩ");
    }
  }
}
