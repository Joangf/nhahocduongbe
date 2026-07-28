package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamScheduleDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Dentist;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamCampaign;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamSchedule;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.ExamScheduleMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.DentistRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamCampaignRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamScheduleRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.OrganizationRepository;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.User;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExamScheduleServiceImpl Unit Tests")
class ExamScheduleServiceImplTest {

  @Mock private ExamScheduleRepository examScheduleRepository;
  @Mock private ExamCampaignRepository examCampaignRepository;
  @Mock private OrganizationRepository organizationRepository;
  @Mock private DentistRepository dentistRepository;
  @Mock private UserRepository userRepository;
  @Mock private ExamScheduleMapper examScheduleMapper;

  @InjectMocks private ExamScheduleServiceImpl examScheduleService;

  private ExamCampaign createMockCampaign(Long id) {
    ExamCampaign c = new ExamCampaign();
    c.setId(id);
    c.setName("Đợt 1");
    return c;
  }

  private Organization createMockOrganization(Long id) {
    Organization org = new Organization();
    org.setId(id);
    org.setName("THPT Chu Văn An");
    return org;
  }

  private Dentist createMockDentist(Long id, Long userId) {
    Dentist d = new Dentist();
    d.setId(id);
    d.setUserId(userId);
    d.setTitle("BS CKI");
    return d;
  }

  private User createMockUser(Long id, Boolean status, Boolean registerStatus) {
    User u = new User();
    u.setId(id);
    u.setStatus(status);
    u.setRegisterStatus(registerStatus);
    return u;
  }

  @Nested
  @DisplayName("getSchedulesByCampaignId() Tests")
  class GetSchedulesTests {

    @Test
    @DisplayName("getSchedulesByCampaignId() — returns DTO list when campaign exists")
    void getSchedulesByCampaignId_existingCampaign_returnsList() {
      ExamCampaign campaign = createMockCampaign(1L);
      ExamSchedule schedule = new ExamSchedule();
      ExamScheduleDTO dto = new ExamScheduleDTO();

      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(campaign));
      when(examScheduleRepository.findByCampaignIdAndStatus(1L, true)).thenReturn(List.of(schedule));
      when(examScheduleMapper.toDtoList(List.of(schedule))).thenReturn(List.of(dto));

      List<ExamScheduleDTO> results = examScheduleService.getSchedulesByCampaignId(1L);

      assertThat(results).hasSize(1).containsExactly(dto);
    }

    @Test
    @DisplayName("getSchedulesByCampaignId() — campaign not found throws 404 NOT_FOUND")
    void getSchedulesByCampaignId_campaignNotFound_throws404() {
      when(examCampaignRepository.findByIdAndStatus(99L, true)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> examScheduleService.getSchedulesByCampaignId(99L))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("404 NOT_FOUND");
    }
  }

  @Nested
  @DisplayName("addOrUpdateSchedule() Tests (TC-CAMP-01)")
  class AddOrUpdateScheduleTests {

    @Test
    @DisplayName("TC-CAMP-01: New schedule with active dentists — saves schedule and assigns dentists")
    void addOrUpdateSchedule_newScheduleWithActiveDentists_savesAndReturnsDto() {
      ExamCampaign campaign = createMockCampaign(1L);
      Organization org = createMockOrganization(10L);
      Dentist dentist = createMockDentist(100L, 500L);
      User activeUser = createMockUser(500L, true, true);

      ExamScheduleDTO inputDto = new ExamScheduleDTO(
          null, 1L, 10L, "THPT Chu Văn An", "10A1", LocalDate.of(2026, 10, 1),
          true, List.of(100L), List.of("BS A"));

      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(campaign));
      when(organizationRepository.findById(10L)).thenReturn(Optional.of(org));
      when(examScheduleRepository.findByCampaignIdAndOrganizationIdAndSchoolClassAndStatus(
          1L, 10L, "10A1", true)).thenReturn(Optional.empty());
      when(dentistRepository.findAllById(List.of(100L))).thenReturn(List.of(dentist));
      when(userRepository.findById(500L)).thenReturn(Optional.of(activeUser));

      ExamSchedule savedEntity = new ExamSchedule();
      ExamScheduleDTO expectedDto = new ExamScheduleDTO();

      when(examScheduleRepository.save(any(ExamSchedule.class))).thenReturn(savedEntity);
      when(examScheduleMapper.toDto(savedEntity)).thenReturn(expectedDto);

      ExamScheduleDTO result = examScheduleService.addOrUpdateSchedule(1L, inputDto);

      assertThat(result).isEqualTo(expectedDto);
      verify(examScheduleRepository, times(1)).save(any(ExamSchedule.class));
    }

    @Test
    @DisplayName("TC-CAMP-01 & TC-ADMIN-02: Locked or unapproved dentist is filtered out of schedule")
    void addOrUpdateSchedule_withLockedOrUnapprovedDentist_filtersOutInactiveDentists() {
      ExamCampaign campaign = createMockCampaign(1L);
      Organization org = createMockOrganization(10L);
      Dentist lockedDentist = createMockDentist(101L, 501L);
      User lockedUser = createMockUser(501L, false, true); // Account locked (status = false)

      ExamScheduleDTO inputDto = new ExamScheduleDTO(
          null, 1L, 10L, "THPT Chu Văn An", "10A1", LocalDate.of(2026, 10, 1),
          true, List.of(101L), List.of("BS Locked"));

      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(campaign));
      when(organizationRepository.findById(10L)).thenReturn(Optional.of(org));
      when(examScheduleRepository.findByCampaignIdAndOrganizationIdAndSchoolClassAndStatus(
          1L, 10L, "10A1", true)).thenReturn(Optional.empty());
      when(dentistRepository.findAllById(List.of(101L))).thenReturn(List.of(lockedDentist));
      when(userRepository.findById(501L)).thenReturn(Optional.of(lockedUser));

      ExamSchedule savedEntity = new ExamSchedule();
      when(examScheduleRepository.save(any(ExamSchedule.class))).thenAnswer(inv -> {
        ExamSchedule s = inv.getArgument(0);
        assertThat(s.getDentists()).isEmpty(); // Verified: locked dentist was filtered out!
        return savedEntity;
      });
      when(examScheduleMapper.toDto(savedEntity)).thenReturn(new ExamScheduleDTO());

      examScheduleService.addOrUpdateSchedule(1L, inputDto);

      verify(examScheduleRepository, times(1)).save(any(ExamSchedule.class));
    }

    @Test
    @DisplayName("Existing schedule — updates exam date instead of creating new")
    void addOrUpdateSchedule_existingSchedule_updatesExamDate() {
      ExamCampaign campaign = createMockCampaign(1L);
      Organization org = createMockOrganization(10L);
      ExamSchedule existingSchedule = new ExamSchedule();
      existingSchedule.setId(5L);
      existingSchedule.setExamDate(LocalDate.of(2026, 9, 1));

      ExamScheduleDTO inputDto = new ExamScheduleDTO(
          null, 1L, 10L, "THPT Chu Văn An", "10A1", LocalDate.of(2026, 10, 1),
          true, null, null);

      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(campaign));
      when(organizationRepository.findById(10L)).thenReturn(Optional.of(org));
      when(examScheduleRepository.findByCampaignIdAndOrganizationIdAndSchoolClassAndStatus(
          1L, 10L, "10A1", true)).thenReturn(Optional.of(existingSchedule));

      ExamScheduleDTO expectedDto = new ExamScheduleDTO();
      when(examScheduleRepository.save(existingSchedule)).thenReturn(existingSchedule);
      when(examScheduleMapper.toDto(existingSchedule)).thenReturn(expectedDto);

      examScheduleService.addOrUpdateSchedule(1L, inputDto);

      assertThat(existingSchedule.getExamDate()).isEqualTo(LocalDate.of(2026, 10, 1));
      verify(examScheduleRepository, times(1)).save(existingSchedule);
    }

    @Test
    @DisplayName("Empty school class — throws 400 BAD_REQUEST")
    void addOrUpdateSchedule_emptySchoolClass_throws400() {
      ExamCampaign campaign = createMockCampaign(1L);
      Organization org = createMockOrganization(10L);
      ExamScheduleDTO inputDto = new ExamScheduleDTO(
          null, 1L, 10L, "THPT Chu Văn An", "   ", LocalDate.of(2026, 10, 1),
          true, null, null);

      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(campaign));
      when(organizationRepository.findById(10L)).thenReturn(Optional.of(org));

      assertThatThrownBy(() -> examScheduleService.addOrUpdateSchedule(1L, inputDto))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("400 BAD_REQUEST")
          .hasMessageContaining("School class cannot be empty");

      verify(examScheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Null exam date — throws 400 BAD_REQUEST")
    void addOrUpdateSchedule_emptyExamDate_throws400() {
      ExamCampaign campaign = createMockCampaign(1L);
      Organization org = createMockOrganization(10L);
      ExamScheduleDTO inputDto = new ExamScheduleDTO(
          null, 1L, 10L, "THPT Chu Văn An", "10A1", null,
          true, null, null);

      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(campaign));
      when(organizationRepository.findById(10L)).thenReturn(Optional.of(org));

      assertThatThrownBy(() -> examScheduleService.addOrUpdateSchedule(1L, inputDto))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("400 BAD_REQUEST")
          .hasMessageContaining("Exam date cannot be empty");

      verify(examScheduleRepository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("deleteSchedule() Tests")
  class DeleteScheduleTests {

    @Test
    @DisplayName("deleteSchedule() — existing schedule sets status=false and returns true")
    void deleteSchedule_existingSchedule_setsStatusFalse() {
      ExamCampaign campaign = createMockCampaign(1L);
      ExamSchedule schedule = new ExamSchedule();
      schedule.setId(10L);
      schedule.setStatus(true);

      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(campaign));
      when(examScheduleRepository.findByIdAndCampaignIdAndStatus(10L, 1L, true))
          .thenReturn(Optional.of(schedule));

      boolean deleted = examScheduleService.deleteSchedule(1L, 10L);

      assertThat(deleted).isTrue();
      assertThat(schedule.getStatus()).isFalse();
      verify(examScheduleRepository, times(1)).save(schedule);
    }

    @Test
    @DisplayName("deleteSchedule() — schedule not found returns false")
    void deleteSchedule_notFound_returnsFalse() {
      ExamCampaign campaign = createMockCampaign(1L);
      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(campaign));
      when(examScheduleRepository.findByIdAndCampaignIdAndStatus(99L, 1L, true))
          .thenReturn(Optional.empty());

      boolean deleted = examScheduleService.deleteSchedule(1L, 99L);

      assertThat(deleted).isFalse();
      verify(examScheduleRepository, never()).save(any());
    }
  }
}
