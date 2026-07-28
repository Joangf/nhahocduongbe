package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.CampaignStatus;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamCampaignDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.StudentExamStatusDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Dentist;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamCampaign;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamSchedule;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.ExamCampaignMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamCampaignRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamScheduleRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.NotificationService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExamCampaignServiceImpl Unit Tests")
class ExamCampaignServiceImplTest {

  @Mock private ExamCampaignRepository examCampaignRepository;
  @Mock private ExamCampaignMapper examCampaignMapper;
  @Mock private PatientRepository patientRepository;
  @Mock private ExamScheduleRepository examScheduleRepository;
  @Mock private NotificationService notificationService;

  @InjectMocks private ExamCampaignServiceImpl examCampaignService;

  private ExamCampaign createMockCampaign(Long id, String name, Boolean status) {
    ExamCampaign campaign = new ExamCampaign();
    campaign.setId(id);
    campaign.setName(name);
    campaign.setStartDate(LocalDate.of(2026, 9, 1));
    campaign.setEndDate(LocalDate.of(2026, 12, 31));
    campaign.setCampaignStatus(CampaignStatus.IN_PROGRESS);
    campaign.setStatus(status);
    return campaign;
  }

  private ExamCampaignDTO createMockCampaignDTO(Long id, String name) {
    return new ExamCampaignDTO(
        id,
        name,
        CampaignStatus.IN_PROGRESS,
        LocalDate.of(2026, 9, 1),
        LocalDate.of(2026, 12, 31),
        "Đợt khám định kỳ",
        true
    );
  }

  @Nested
  @DisplayName("CRUD Operations Tests (TC-CAMP-01)")
  class CrudTests {

    @Test
    @DisplayName("getAllActiveCampaigns() — returns mapped list of active campaigns")
    void getAllActiveCampaigns_returnsMappedList() {
      ExamCampaign campaign = createMockCampaign(1L, "Đợt 1", true);
      ExamCampaignDTO dto = createMockCampaignDTO(1L, "Đợt 1");
      when(examCampaignRepository.findAllByStatusOrderByIdDesc(true)).thenReturn(List.of(campaign));
      when(examCampaignMapper.toDtoList(List.of(campaign))).thenReturn(List.of(dto));

      List<ExamCampaignDTO> result = examCampaignService.getAllActiveCampaigns();

      assertThat(result).hasSize(1).containsExactly(dto);
      verify(examCampaignRepository, times(1)).findAllByStatusOrderByIdDesc(true);
    }

    @Test
    @DisplayName("getCampaignById() — existing ID returns mapped DTO")
    void getCampaignById_existingId_returnsDto() {
      ExamCampaign campaign = createMockCampaign(1L, "Đợt 1", true);
      ExamCampaignDTO dto = createMockCampaignDTO(1L, "Đợt 1");
      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(campaign));
      when(examCampaignMapper.toDto(campaign)).thenReturn(dto);

      ExamCampaignDTO result = examCampaignService.getCampaignById(1L);

      assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("getCampaignById() — not found throws 404 NOT_FOUND")
    void getCampaignById_notFound_throws404() {
      when(examCampaignRepository.findByIdAndStatus(99L, true)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> examCampaignService.getCampaignById(99L))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("404 NOT_FOUND")
          .hasMessageContaining("Campaign not found with id: 99");
    }

    @Test
    @DisplayName("TC-CAMP-01: createCampaign() — saves new campaign with status=true")
    void createCampaign_validDto_savesWithStatusTrue() {
      ExamCampaignDTO inputDto = createMockCampaignDTO(null, "Đợt mới");
      ExamCampaign entity = createMockCampaign(null, "Đợt mới", true);
      ExamCampaign savedEntity = createMockCampaign(10L, "Đợt mới", true);
      ExamCampaignDTO expectedDto = createMockCampaignDTO(10L, "Đợt mới");

      when(examCampaignMapper.toEntity(inputDto)).thenReturn(entity);
      when(examCampaignRepository.save(entity)).thenReturn(savedEntity);
      when(examCampaignMapper.toDto(savedEntity)).thenReturn(expectedDto);

      ExamCampaignDTO result = examCampaignService.createCampaign(inputDto);

      assertThat(result).isEqualTo(expectedDto);
      assertThat(entity.getStatus()).isTrue();
      verify(examCampaignRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("updateCampaign() — existing campaign updates and saves")
    void updateCampaign_existingId_updatesAndReturnsDto() {
      ExamCampaignDTO updateDto = createMockCampaignDTO(1L, "Đợt 1 - cập nhật");
      ExamCampaign existingEntity = createMockCampaign(1L, "Đợt 1", true);
      ExamCampaign savedEntity = createMockCampaign(1L, "Đợt 1 - cập nhật", true);
      ExamCampaignDTO expectedDto = createMockCampaignDTO(1L, "Đợt 1 - cập nhật");

      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(existingEntity));
      when(examCampaignRepository.save(existingEntity)).thenReturn(savedEntity);
      when(examCampaignMapper.toDto(savedEntity)).thenReturn(expectedDto);

      ExamCampaignDTO result = examCampaignService.updateCampaign(1L, updateDto);

      assertThat(result).isEqualTo(expectedDto);
      verify(examCampaignMapper, times(1)).partialUpdate(updateDto, existingEntity);
      verify(examCampaignRepository, times(1)).save(existingEntity);
    }

    @Test
    @DisplayName("deleteCampaign() — existing ID sets status=false and returns true")
    void deleteCampaign_existingId_setsStatusFalseAndReturnsTrue() {
      ExamCampaign campaign = createMockCampaign(1L, "Đợt 1", true);
      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(campaign));

      boolean deleted = examCampaignService.deleteCampaign(1L);

      assertThat(deleted).isTrue();
      assertThat(campaign.getStatus()).isFalse();
      verify(examCampaignRepository, times(1)).save(campaign);
    }

    @Test
    @DisplayName("deleteCampaign() — not found returns false without save")
    void deleteCampaign_notFound_returnsFalse() {
      when(examCampaignRepository.findByIdAndStatus(99L, true)).thenReturn(Optional.empty());

      boolean deleted = examCampaignService.deleteCampaign(99L);

      assertThat(deleted).isFalse();
      verify(examCampaignRepository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("Student Tracking Tests (TC-CAMP-02)")
  class StudentTrackingTests {

    @Test
    @DisplayName("TC-CAMP-02: getStudentsByCampaignId() — returns student list with EXAMINED / NOT_EXAMINED status")
    void getStudentsByCampaignId_existingCampaign_returnsExaminedAndUnexaminedList() {
      Object[] row1 = new Object[] {
          100L, "Nguyen Van A", "HS001", "10A1", "0901",
          50L, Date.valueOf(LocalDate.of(2026, 10, 1)), "Phòng khám", "EXAMINED"
      };
      Object[] row2 = new Object[] {
          101L, "Tran Thi B", "HS002", "10A1", "0902",
          null, null, null, "NOT_EXAMINED"
      };

      when(patientRepository.findStudentExamStatusByCampaignId(1L))
          .thenReturn(List.of(row1, row2));

      List<StudentExamStatusDTO> results = examCampaignService.getStudentsByCampaignId(1L);

      assertThat(results).hasSize(2);
      assertThat(results.get(0).getPatientId()).isEqualTo(100L);
      assertThat(results.get(0).getStatus()).isEqualTo("EXAMINED");
      assertThat(results.get(0).getExamDate()).isEqualTo(LocalDate.of(2026, 10, 1));

      assertThat(results.get(1).getPatientId()).isEqualTo(101L);
      assertThat(results.get(1).getStatus()).isEqualTo("NOT_EXAMINED");
      assertThat(results.get(1).getExamId()).isNull();
    }
  }

  @Nested
  @DisplayName("Dentist Notification Tests (TC-CAMP-03)")
  class NotificationTests {

    @Test
    @DisplayName("TC-CAMP-03: notifyDentists() — creates notifications for assigned dentists and returns count")
    void notifyDentists_validCampaignAndSchedules_sendsNotificationsAndReturnsCount() {
      ExamCampaign campaign = createMockCampaign(1L, "Đợt khám nha khoa", true);
      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(campaign));

      Organization org = new Organization();
      org.setName("Trường THPT Chu Văn An");

      Dentist dentist1 = new Dentist();
      dentist1.setId(10L);
      dentist1.setUserId(500L);

      Dentist dentist2 = new Dentist();
      dentist2.setId(11L);
      dentist2.setUserId(501L);

      ExamSchedule schedule = new ExamSchedule();
      schedule.setId(1L);
      schedule.setCampaign(campaign);
      schedule.setOrganization(org);
      schedule.setSchoolClass("10A1");
      schedule.setExamDate(LocalDate.of(2026, 10, 15));
      schedule.setDentists(Set.of(dentist1, dentist2));

      when(examScheduleRepository.findByCampaignIdAndStatus(1L, true))
          .thenReturn(List.of(schedule));

      int count = examCampaignService.notifyDentists(1L);

      assertThat(count).isEqualTo(2);
      verify(notificationService, times(1)).createNotificationForDentist(
          eq(500L), eq(1L), eq("Đợt khám nha khoa"), anyList());
      verify(notificationService, times(1)).createNotificationForDentist(
          eq(501L), eq(1L), eq("Đợt khám nha khoa"), anyList());
    }

    @Test
    @DisplayName("notifyDentists() — campaign not found throws 404 NOT_FOUND")
    void notifyDentists_campaignNotFound_throws404() {
      when(examCampaignRepository.findByIdAndStatus(99L, true)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> examCampaignService.notifyDentists(99L))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("404 NOT_FOUND");

      verify(notificationService, never()).createNotificationForDentist(any(), any(), any(), anyList());
    }

    @Test
    @DisplayName("notifyDentists() — campaign has no schedules throws 400 BAD_REQUEST")
    void notifyDentists_noSchedules_throws400() {
      ExamCampaign campaign = createMockCampaign(1L, "Đợt khám", true);
      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(campaign));
      when(examScheduleRepository.findByCampaignIdAndStatus(1L, true)).thenReturn(Collections.emptyList());

      assertThatThrownBy(() -> examCampaignService.notifyDentists(1L))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("400 BAD_REQUEST")
          .hasMessageContaining("Không có lịch khám nào trong đợt này");
    }

    @Test
    @DisplayName("notifyDentists() — no dentists assigned throws 400 BAD_REQUEST")
    void notifyDentists_noDentistsAssigned_throws400() {
      ExamCampaign campaign = createMockCampaign(1L, "Đợt khám", true);
      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(campaign));

      ExamSchedule schedule = new ExamSchedule();
      schedule.setDentists(Collections.emptySet());

      when(examScheduleRepository.findByCampaignIdAndStatus(1L, true)).thenReturn(List.of(schedule));

      assertThatThrownBy(() -> examCampaignService.notifyDentists(1L))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("400 BAD_REQUEST")
          .hasMessageContaining("Không có bác sĩ nào được phân công trong đợt này");
    }
  }
}
