package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothProblem;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Tooth;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DashboardStatsDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamServiceImpl;

/**
 * Unit test ExamServiceImpl — chỉ test các method không phụ thuộc vào ExamMapper (abstract class).
 */
@DisplayName("ExamServiceImpl — Unit Tests (Chức năng mới)")
@ExtendWith(MockitoExtension.class)
class ExamServiceImplUnitTest {

  @Mock ExamRepository examRepository;
  @Mock DiseaseRepository diseaseRepository;
  @Mock PatientRepository patientRepository;
  @Mock TartarRecordRepository tartarRecordRepository;
  @Mock TeethRecordRepository teethRecordRepository;
  @Mock PlaqueRecordRepository plaqueRecordRepository;
  @Mock ExamCampaignRepository examCampaignRepository;

  // ExamMapper là abstract class → tạo manual mock
  vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.ExamMapper examMapper =
      mock(vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.ExamMapper.class,
          withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS));

  ExamServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new ExamServiceImpl();
    injectField(service, "examRepository", examRepository);
    injectField(service, "diseaseRepository", diseaseRepository);
    injectField(service, "examMapper", examMapper);
    injectField(service, "patientRepository", patientRepository);
    injectField(service, "tartarRecordRepository", tartarRecordRepository);
    injectField(service, "teethRecordRepository", teethRecordRepository);
    injectField(service, "plaqueRecordRepository", plaqueRecordRepository);
    injectField(service, "examCampaignRepository", examCampaignRepository);
  }

  private void injectField(Object target, String fieldName, Object value) {
    try {
      java.lang.reflect.Field field = ExamServiceImpl.class.getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (Exception e) {
      // field not found - skip
    }
  }

  private Exam createExam(Long id, TeethRecord teethRecord) {
    Exam e = new Exam();
    e.setId(id);
    e.setStatus(true);
    e.setDate(LocalDate.of(2026, 5, 1));
    e.setTeethRecord(teethRecord);
    Patient p = new Patient();
    p.setId(100L);
    p.setFullName("Test Patient");
    e.setPatient(p);
    return e;
  }

  private ExamDTO createDto(Long id, LocalDate reExamDate, String reExamNote) {
    ExamDTO dto = new ExamDTO();
    dto.setId(id);
    dto.setPatientId(100L);
    dto.setPatientName("Test Patient");
    dto.setReExamDate(reExamDate);
    dto.setReExamNote(reExamNote);
    return dto;
  }

  // ─── TC-08: getReExams ────────────────────────────────────────────────────
  @Nested
  @DisplayName("TC-08 getReExams()")
  class GetReExams {

    @Test
    @DisplayName("Trả về danh sách DTO khi có exam với sâu răng")
    void shouldReturnCariesReExams() {
      // Tạo TeethRecord có caries
      ToothCondition cariesCondition = new ToothCondition();
      cariesCondition.setProblem(ToothProblem.CARIES);
      Map<Tooth, ToothCondition> recordMap = Map.of(Tooth._11, cariesCondition);
      TeethRecord teethRecord = new TeethRecord();
      teethRecord.setId(1L);
      teethRecord.setRecord(recordMap);

      LocalDate expectedReExamDate = LocalDate.of(2026, 5, 1).plusMonths(6);
      Exam exam = createExam(1L, teethRecord);
      ExamDTO dto = createDto(1L, expectedReExamDate, "Cần tái khám điều trị sâu răng");

      when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(exam));
      when(examMapper.toDto(exam)).thenReturn(dto);

      List<ExamDTO> result = service.getReExams();

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getReExamDate()).isEqualTo(expectedReExamDate);
      assertThat(result.get(0).getReExamNote()).isEqualTo("Cần tái khám điều trị sâu răng");
      verify(examRepository).findAllActiveWithDetails();
    }

    @Test
    @DisplayName("Trả về danh sách rỗng khi không có exam")
    void shouldReturnEmptyListWhenNoExams() {
      when(examRepository.findAllActiveWithDetails()).thenReturn(Collections.emptyList());

      List<ExamDTO> result = service.getReExams();

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Trả về danh sách rỗng khi TeethRecord null")
    void shouldReturnEmptyListWhenTeethRecordNull() {
      Exam exam = createExam(1L, null);

      when(examRepository.findAllActiveWithDetails()).thenReturn(List.of(exam));

      List<ExamDTO> result = service.getReExams();

      assertThat(result).isEmpty();
    }
  }

  // ─── TC-09: getDashboardStats ─────────────────────────────────────────────
  @Nested
  @DisplayName("TC-09 getDashboardStats()")
  class GetDashboardStats {

    @Test
    @DisplayName("Tổng hợp đúng số liệu từ các repository")
    void shouldAggregateCorrectStats() {
      when(examCampaignRepository.count()).thenReturn(5L);
      when(examCampaignRepository.countByStatus(true)).thenReturn(3L);
      when(patientRepository.count()).thenReturn(100L);
      when(examRepository.countTotalExamined()).thenReturn(30L);

      DashboardStatsDTO result = service.getDashboardStats();

      assertThat(result.getTotalCampaigns()).isEqualTo(5L);
      assertThat(result.getActiveCampaigns()).isEqualTo(3L);
      assertThat(result.getTotalStudents()).isEqualTo(100L);
      assertThat(result.getTotalExamined()).isEqualTo(30L);
    }

    @Test
    @DisplayName("Trả về số 0 khi không có dữ liệu")
    void shouldReturnZerosWhenNoData() {
      when(examCampaignRepository.count()).thenReturn(0L);
      when(examCampaignRepository.countByStatus(true)).thenReturn(0L);
      when(patientRepository.count()).thenReturn(0L);
      when(examRepository.countTotalExamined()).thenReturn(0L);

      DashboardStatsDTO result = service.getDashboardStats();

      assertThat(result.getTotalCampaigns()).isZero();
      assertThat(result.getActiveCampaigns()).isZero();
      assertThat(result.getTotalStudents()).isZero();
      assertThat(result.getTotalExamined()).isZero();
    }

    @Test
    @DisplayName("activeCampaigns phải nhỏ hơn hoặc bằng totalCampaigns")
    void activeCampaignsShouldNotExceedTotal() {
      when(examCampaignRepository.count()).thenReturn(3L);
      when(examCampaignRepository.countByStatus(true)).thenReturn(2L);
      when(patientRepository.count()).thenReturn(50L);
      when(examRepository.countTotalExamined()).thenReturn(20L);

      DashboardStatsDTO result = service.getDashboardStats();

      assertThat(result.getActiveCampaigns()).isLessThanOrEqualTo(result.getTotalCampaigns());
    }
  }

  // ─── TC-10: delete exam ────────────────────────────────────────────────────
  @Nested
  @DisplayName("TC-10 delete()")
  class DeleteExam {

    @Test
    @DisplayName("Soft-delete: đặt status=false, trả về true")
    void shouldSoftDeleteExam() {
      Exam exam = createExam(1L, null);
      when(examRepository.findById(1L)).thenReturn(Optional.of(exam));

      boolean result = service.delete(1L);

      assertThat(result).isTrue();
      assertThat(exam.getStatus()).isEqualTo(Boolean.FALSE);
      verify(examRepository).save(exam);
    }

    @Test
    @DisplayName("Ném exception khi không tìm thấy exam")
    void shouldThrowWhenNotFound() {
      when(examRepository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.delete(99L))
          .isInstanceOf(ResponseStatusException.class);
    }
  }
}
