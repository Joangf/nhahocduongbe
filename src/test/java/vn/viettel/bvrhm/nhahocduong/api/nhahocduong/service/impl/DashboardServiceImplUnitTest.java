package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthorizationService.AuthorizationData;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Tooth;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothProblem;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.StudentCountBySchoolDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.YearlyStudentCountDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.DashboardServiceImpl;

@DisplayName("DashboardServiceImpl — Unit Tests")
@ExtendWith(MockitoExtension.class)
class DashboardServiceImplUnitTest {

  @SuppressWarnings({"unchecked", "rawtypes"})
  private List<Object[]> rows(Object[]... rows) {
    return (List) java.util.Arrays.asList(rows);
  }

  @Mock ExamCampaignRepository campaignRepository;
  @Mock PatientRepository patientRepository;
  @Mock ExamRepository examRepository;
  @Mock vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthorizationService authorizationService;
  @Mock StudentClassAffiliationRepository affiliationRepository;
  @InjectMocks DashboardServiceImpl service;

  @Nested
  @DisplayName("TC-01 getCampaignStats()")
  class GetCampaignStats {

    @Test
    @DisplayName("Trả về đầy đủ 4 chỉ số thống kê")
    void shouldReturnFourStats() {
      when(campaignRepository.count()).thenReturn(10L);
      when(campaignRepository.countByStatus(true)).thenReturn(5L);
      when(patientRepository.count()).thenReturn(500L);
      when(examRepository.countTotalExamined()).thenReturn(200L);

      Map<String, Object> stats = service.getCampaignStats();

      assertThat(stats)
          .containsEntry("totalCampaigns", 10L)
          .containsEntry("activeCampaigns", 5L)
          .containsEntry("totalStudents", 500L)
          .containsEntry("totalExamined", 200L);
    }

    @Test
    @DisplayName("Trả về giá trị 0 khi không có dữ liệu")
    void shouldReturnZerosWhenNoData() {
      when(campaignRepository.count()).thenReturn(0L);
      when(campaignRepository.countByStatus(true)).thenReturn(0L);
      when(patientRepository.count()).thenReturn(0L);
      when(examRepository.countTotalExamined()).thenReturn(0L);

      Map<String, Object> stats = service.getCampaignStats();

      assertThat(stats)
          .containsEntry("totalCampaigns", 0L)
          .containsEntry("activeCampaigns", 0L)
          .containsEntry("totalStudents", 0L)
          .containsEntry("totalExamined", 0L);
    }
  }

  @Nested
  @DisplayName("TC-02 getStudentCountByYear()")
  class GetStudentCountByYear {

    @Test
    @DisplayName("Trả về danh sách student count khi không có org filter")
    void shouldReturnStudentCounts() {
      AuthorizationData authData = new AuthorizationData();
      authData.setOrganizationId(null);
      when(authorizationService.authorize()).thenReturn(authData);
      when(affiliationRepository.countStudentsBySchoolAndGrade(1L))
          .thenReturn(rows(
              new Object[]{"Trường A", "5A", 30L},
              new Object[]{"Trường B", "5B", 25L}
          ));

      List<StudentCountBySchoolDTO> result = service.getStudentCountByYear(1L);

      assertThat(result).hasSize(2);
      assertThat(result.get(0).getSchoolName()).isEqualTo("Trường A");
      assertThat(result.get(1).getStudentCount()).isEqualTo(25L);
    }

    @Test
    @DisplayName("Trả về danh sách rỗng khi không có dữ liệu")
    void shouldReturnEmptyWhenNoData() {
      AuthorizationData authData = new AuthorizationData();
      authData.setOrganizationId(null);
      when(authorizationService.authorize()).thenReturn(authData);
      when(affiliationRepository.countStudentsBySchoolAndGrade(1L))
          .thenReturn(Collections.emptyList());

      List<StudentCountBySchoolDTO> result = service.getStudentCountByYear(1L);

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Lọc theo orgId khi có authorization")
    void shouldFilterByOrgId() {
      AuthorizationData authData = new AuthorizationData();
      authData.setOrganizationId(5L);
      when(authorizationService.authorize()).thenReturn(authData);
      when(affiliationRepository.countStudentsBySchoolAndGradeFiltered(1L, 5L))
          .thenReturn(rows(new Object[]{"Trường A", "5A", 30L}));

      List<StudentCountBySchoolDTO> result = service.getStudentCountByYear(1L);

      assertThat(result).hasSize(1);
    }
  }

  @Nested
  @DisplayName("TC-03 getStudentCountHistory()")
  class GetStudentCountHistory {

    @Test
    @DisplayName("Trả về danh sách yearly student count")
    void shouldReturnYearlyCounts() {
      AuthorizationData authData = new AuthorizationData();
      authData.setOrganizationId(null);
      when(authorizationService.authorize()).thenReturn(authData);
      when(affiliationRepository.countStudentsByYear())
          .thenReturn(rows(
              new Object[]{"2024-2025", 1000L},
              new Object[]{"2025-2026", 1100L}
          ));

      List<YearlyStudentCountDTO> result = service.getStudentCountHistory();

      assertThat(result).hasSize(2);
      assertThat(result.get(0).getYearName()).isEqualTo("2024-2025");
      assertThat(result.get(1).getStudentCount()).isEqualTo(1100L);
    }

    @Test
    @DisplayName("Trả về danh sách rỗng khi không có dữ liệu")
    void shouldReturnEmptyWhenNoData() {
      AuthorizationData authData = new AuthorizationData();
      authData.setOrganizationId(null);
      when(authorizationService.authorize()).thenReturn(authData);
      when(affiliationRepository.countStudentsByYear())
          .thenReturn(Collections.emptyList());

      List<YearlyStudentCountDTO> result = service.getStudentCountHistory();

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Lọc history theo orgId khi có authorization")
    void shouldFilterHistoryByOrgId() {
      AuthorizationData authData = new AuthorizationData();
      authData.setOrganizationId(5L);
      when(authorizationService.authorize()).thenReturn(authData);
      when(affiliationRepository.countStudentsByYearFiltered(5L))
          .thenReturn(rows(new Object[]{"2025-2026", 500L}));

      List<YearlyStudentCountDTO> result = service.getStudentCountHistory();

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getStudentCount()).isEqualTo(500L);
    }
  }

  @Nested
  @DisplayName("TC-04 hasCaries() via getStats")
  class HasCaries {

    @Test
    @DisplayName("getStats trả về map đầy đủ keys khi có dữ liệu")
    void getStatsShouldContainAllKeys() {
      when(campaignRepository.count()).thenReturn(1L);
      when(campaignRepository.countByStatus(true)).thenReturn(1L);
      when(patientRepository.count()).thenReturn(10L);
      when(examRepository.countTotalExamined()).thenReturn(5L);

      // Exam với teeth record có caries
      Organization org = new Organization();
      org.setId(1L);
      org.setName("Trường TH A");

      TeethRecord teethRecord = new TeethRecord();
      ToothCondition condition = new ToothCondition();
      condition.setProblem(ToothProblem.CARIES);
      teethRecord.setRecord(Map.of(Tooth._11, condition));

      Exam exam = new Exam();
      exam.setId(1L);
      exam.setOrganization(org);
      exam.setSchoolClass("5A");
      exam.setYear("2025-2026");
      exam.setTeethRecord(teethRecord);

      when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(exam));

      Map<String, Object> result = service.getStats();

      assertThat(result).containsKeys(
          "cariesBySchoolClass", "statsByYear", "topSchoolsCaries", "pathologyHeatmap");
    }

    @Test
    @DisplayName("getStats trả về empty lists khi không có exam")
    void getStatsShouldReturnEmptyWhenNoExams() {
      when(campaignRepository.count()).thenReturn(1L);
      when(campaignRepository.countByStatus(true)).thenReturn(1L);
      when(patientRepository.count()).thenReturn(10L);
      when(examRepository.countTotalExamined()).thenReturn(5L);
      when(examRepository.findAllActiveWithDetails()).thenReturn(Collections.emptyList());

      Map<String, Object> result = service.getStats();

      assertThat((List<?>) result.get("cariesBySchoolClass")).isEmpty();
    }
  }
}
