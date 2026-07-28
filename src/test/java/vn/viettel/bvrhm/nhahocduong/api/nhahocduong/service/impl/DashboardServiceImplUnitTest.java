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

  @Nested
  @DisplayName("TC-05 getStats — null organization excluded from cariesBySchoolClass")
  class GetStatsNullOrganization {

    @Test
    @DisplayName("Exam có organization=null không xuất hiện trong cariesBySchoolClass")
    void shouldExcludeExamWithNullOrganization() {
      when(campaignRepository.count()).thenReturn(1L);
      when(campaignRepository.countByStatus(true)).thenReturn(1L);
      when(patientRepository.count()).thenReturn(10L);
      when(examRepository.countTotalExamined()).thenReturn(5L);

      Exam examWithNullOrg = new Exam();
      examWithNullOrg.setId(1L);
      examWithNullOrg.setOrganization(null);
      examWithNullOrg.setSchoolClass("5A");
      examWithNullOrg.setYear("2025-2026");
      examWithNullOrg.setTeethRecord(null);

      when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(examWithNullOrg));

      Map<String, Object> result = service.getStats();

      assertThat((List<?>) result.get("cariesBySchoolClass")).isEmpty();
    }
  }

  @Nested
  @DisplayName("TC-06 getStats — null schoolClass excluded from cariesBySchoolClass")
  class GetStatsNullSchoolClass {

    @Test
    @DisplayName("Exam có schoolClass=null không xuất hiện trong cariesBySchoolClass")
    void shouldExcludeExamWithNullSchoolClass() {
      when(campaignRepository.count()).thenReturn(1L);
      when(campaignRepository.countByStatus(true)).thenReturn(1L);
      when(patientRepository.count()).thenReturn(10L);
      when(examRepository.countTotalExamined()).thenReturn(5L);

      Organization org = new Organization();
      org.setId(1L);
      org.setName("Trường TH A");

      Exam examWithNullClass = new Exam();
      examWithNullClass.setId(1L);
      examWithNullClass.setOrganization(org);
      examWithNullClass.setSchoolClass(null);
      examWithNullClass.setYear("2025-2026");
      examWithNullClass.setTeethRecord(null);

      when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(examWithNullClass));

      Map<String, Object> result = service.getStats();

      assertThat((List<?>) result.get("cariesBySchoolClass")).isEmpty();
    }
  }

  @Nested
  @DisplayName("TC-07 getStats — null year excluded from statsByYear")
  class GetStatsNullYear {

    @Test
    @DisplayName("Exam có year=null không xuất hiện trong statsByYear")
    void shouldExcludeExamWithNullYear() {
      when(campaignRepository.count()).thenReturn(1L);
      when(campaignRepository.countByStatus(true)).thenReturn(1L);
      when(patientRepository.count()).thenReturn(10L);
      when(examRepository.countTotalExamined()).thenReturn(5L);

      Organization org = new Organization();
      org.setId(1L);
      org.setName("Trường TH A");

      Exam examWithNullYear = new Exam();
      examWithNullYear.setId(1L);
      examWithNullYear.setOrganization(org);
      examWithNullYear.setSchoolClass("5A");
      examWithNullYear.setYear(null);
      examWithNullYear.setTeethRecord(null);

      when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(examWithNullYear));

      Map<String, Object> result = service.getStats();

      assertThat((List<?>) result.get("statsByYear")).isEmpty();
    }
  }

  @Nested
  @DisplayName("TC-08 getStats — null teethRecord does not crash, cariesCount=0")
  class GetStatsNullTeethRecord {

    @Test
    @DisplayName("Exam có teethRecord=null không crash và cariesCount=0")
    void shouldHandleNullTeethRecord() {
      when(campaignRepository.count()).thenReturn(1L);
      when(campaignRepository.countByStatus(true)).thenReturn(1L);
      when(patientRepository.count()).thenReturn(10L);
      when(examRepository.countTotalExamined()).thenReturn(5L);

      Organization org = new Organization();
      org.setId(1L);
      org.setName("Trường TH A");

      Exam exam = new Exam();
      exam.setId(1L);
      exam.setOrganization(org);
      exam.setSchoolClass("5A");
      exam.setYear("2025-2026");
      exam.setTeethRecord(null);

      when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(exam));

      Map<String, Object> result = service.getStats();

      assertThat(result).containsKey("cariesBySchoolClass");
      assertThat(result).containsKey("statsByYear");
      assertThat(result).containsKey("pathologyHeatmap");

      List<?> cariesList = (List<?>) result.get("cariesBySchoolClass");
      assertThat(cariesList).hasSize(1);
      assertThat((long) ((Map<?, ?>) cariesList.get(0)).get("cariesCount")).isEqualTo(0L);
    }
  }

  @Nested
  @DisplayName("TC-09 getStats — null record map does not crash")
  class GetStatsNullRecordMap {

    @Test
    @DisplayName("TeethRecord có record=null không crash và cariesCount=0")
    void shouldHandleNullRecordMap() {
      when(campaignRepository.count()).thenReturn(1L);
      when(campaignRepository.countByStatus(true)).thenReturn(1L);
      when(patientRepository.count()).thenReturn(10L);
      when(examRepository.countTotalExamined()).thenReturn(5L);

      Organization org = new Organization();
      org.setId(1L);
      org.setName("Trường TH A");

      TeethRecord teethRecord = new TeethRecord();
      teethRecord.setRecord(null);

      Exam exam = new Exam();
      exam.setId(1L);
      exam.setOrganization(org);
      exam.setSchoolClass("5A");
      exam.setYear("2025-2026");
      exam.setTeethRecord(teethRecord);

      when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(exam));

      Map<String, Object> result = service.getStats();

      List<?> cariesList = (List<?>) result.get("cariesBySchoolClass");
      assertThat(cariesList).hasSize(1);
      assertThat((long) ((Map<?, ?>) cariesList.get(0)).get("cariesCount")).isEqualTo(0L);
    }
  }

  @Nested
  @DisplayName("TC-10 getStats — NO_PROBLEM condition yields cariesCount=0")
  class GetStatsNoProblem {

    @Test
    @DisplayName("TeethRecord với ToothProblem.NO_PROBLEM có cariesCount=0")
    void shouldReturnCariesCountZeroForNoProblem() {
      when(campaignRepository.count()).thenReturn(1L);
      when(campaignRepository.countByStatus(true)).thenReturn(1L);
      when(patientRepository.count()).thenReturn(10L);
      when(examRepository.countTotalExamined()).thenReturn(5L);

      Organization org = new Organization();
      org.setId(1L);
      org.setName("Trường TH A");

      TeethRecord teethRecord = new TeethRecord();
      ToothCondition condition = new ToothCondition();
      condition.setProblem(ToothProblem.NO_PROBLEM);
      teethRecord.setRecord(Map.of(Tooth._11, condition));

      Exam exam = new Exam();
      exam.setId(1L);
      exam.setOrganization(org);
      exam.setSchoolClass("5A");
      exam.setYear("2025-2026");
      exam.setTeethRecord(teethRecord);

      when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(exam));

      Map<String, Object> result = service.getStats();

      List<?> cariesList = (List<?>) result.get("cariesBySchoolClass");
      assertThat(cariesList).hasSize(1);
      assertThat((long) ((Map<?, ?>) cariesList.get(0)).get("cariesCount")).isEqualTo(0L);
    }
  }

  @Nested
  @DisplayName("TC-11 getStats — null ToothCondition value in record map")
  class GetStatsNullConditionValue {

    @Test
    @DisplayName("TeethRecord với giá trị null trong record map không crash")
    void shouldHandleNullConditionValue() {
      when(campaignRepository.count()).thenReturn(1L);
      when(campaignRepository.countByStatus(true)).thenReturn(1L);
      when(patientRepository.count()).thenReturn(10L);
      when(examRepository.countTotalExamined()).thenReturn(5L);

      Organization org = new Organization();
      org.setId(1L);
      org.setName("Trường TH A");

      TeethRecord teethRecord = new TeethRecord();
      Map<Tooth, ToothCondition> recordMap = new java.util.HashMap<>();
      recordMap.put(Tooth._11, null);
      teethRecord.setRecord(recordMap);

      Exam exam = new Exam();
      exam.setId(1L);
      exam.setOrganization(org);
      exam.setSchoolClass("5A");
      exam.setYear("2025-2026");
      exam.setTeethRecord(teethRecord);

      when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(exam));

      Map<String, Object> result = service.getStats();

      List<?> cariesList = (List<?>) result.get("cariesBySchoolClass");
      assertThat(cariesList).hasSize(1);
      assertThat((long) ((Map<?, ?>) cariesList.get(0)).get("cariesCount")).isEqualTo(0L);

      Map<?, Integer> heatmap = (Map<?, Integer>) result.get("pathologyHeatmap");
      assertThat(heatmap.get("11")).isEqualTo(0);
    }
  }

  @Nested
  @DisplayName("TC-12 evictDashboardCache()")
  class EvictDashboardCache {

    @Test
    @DisplayName("Gọi evictDashboardCache không throw exception")
    void shouldNotThrowException() {
      assertThatCode(() -> service.evictDashboardCache()).doesNotThrowAnyException();
    }
  }
}
