package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DashboardStatsDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamServiceImpl;

/**
 * Unit test ExamServiceImpl — chỉ test các method không phụ thuộc vào ExamMapper (abstract class).
 * ExamMapper là abstract class do MapStruct, Mockito không thể mock trực tiếp.
 * Các method getReExams, getDashboardStats, delete đều không mock mapper hoặc
 * mapper không cần thiết ở đây.
 */
@DisplayName("ExamServiceImpl — Unit Tests (Chức năng mới)")
@ExtendWith(MockitoExtension.class)
class ExamServiceImplUnitTest {

  // Khai báo Mock cho tất cả dependencies (ExamMapper là abstract → dùng @Mock annotation thường,
  // nhưng không sử dụng trực tiếp trong test; chỉ cần @InjectMocks tạo được instance)
  @Mock ExamRepository examRepository;
  @Mock DiseaseRepository diseaseRepository;
  @Mock vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.TreatmentRecordMapper treatmentRecordMapper;
  @Mock PatientRepository patientRepository;
  @Mock DentistRepository dentistRepository;
  @Mock OrganizationRepository organizationRepository;
  @Mock TartarRecordRepository tartarRecordRepository;
  @Mock TeethRecordRepository teethRecordRepository;
  @Mock PlaqueRecordRepository plaqueRecordRepository;
  @Mock ExamCampaignRepository examCampaignRepository;

  // ExamMapper là abstract class → tạo manual mock bằng Mockito.mock()
  vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.ExamMapper examMapper =
      mock(vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.ExamMapper.class,
          withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS));

  ExamServiceImpl service;

  @BeforeEach
  void setUp() {
    // Manual injection vì ExamMapper không thể dùng @Mock với abstract class + JDK 23
    service = new ExamServiceImpl();
    // Inject dependencies via reflection
    injectField(service, "examRepository", examRepository);
    injectField(service, "diseaseRepository", diseaseRepository);
    injectField(service, "examMapper", examMapper);
    injectField(service, "treatmentRecordMapper", treatmentRecordMapper);
    injectField(service, "patientRepository", patientRepository);
    injectField(service, "dentistRepository", dentistRepository);
    injectField(service, "organizationRepository", organizationRepository);
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
      // field might be in parent if not found, try declared fields
      try {
        java.lang.reflect.Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
      } catch (Exception ex) {
        // field not found - skip
      }
    }
  }

  private Exam buildExam(Long id, LocalDate reExamDate, String reExamNote) {
    Exam e = new Exam();
    e.setId(id);
    e.setStatus(true);
    e.setDate(LocalDate.of(2026, 5, 1));
    e.setReExamDate(reExamDate);
    e.setReExamNote(reExamNote);
    Patient p = new Patient();
    p.setId(100L);
    p.setFullName("Test Patient");
    e.setPatient(p);
    return e;
  }

  private ExamDTO buildDto(Long id, LocalDate reExamDate, String reExamNote) {
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
    @DisplayName("Gọi findUpcomingReExams() và trả về danh sách DTO")
    void shouldCallRepositoryAndMapResult() {
      LocalDate futureDate = LocalDate.now().plusDays(7);
      Exam exam = buildExam(1L, futureDate, "Tái khám sau 1 tuần");
      ExamDTO dto = buildDto(1L, futureDate, "Tái khám sau 1 tuần");

      when(examRepository.findUpcomingReExams()).thenReturn(List.of(exam));
      when(examMapper.toDtoList(List.of(exam))).thenReturn(List.of(dto));

      List<ExamDTO> result = service.getReExams();

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getReExamDate()).isEqualTo(futureDate);
      assertThat(result.get(0).getReExamNote()).isEqualTo("Tái khám sau 1 tuần");
      verify(examRepository).findUpcomingReExams();
      verify(examMapper).toDtoList(List.of(exam));
    }

    @Test
    @DisplayName("Trả về danh sách rỗng khi không có lịch tái khám")
    void shouldReturnEmptyListWhenNoReExams() {
      when(examRepository.findUpcomingReExams()).thenReturn(Collections.emptyList());
      when(examMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

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
      ExamCampaign activeCampaign = new ExamCampaign();
      activeCampaign.setId(1L);
      activeCampaign.setStatus(true);

      when(examCampaignRepository.count()).thenReturn(5L);
      when(examCampaignRepository.findAllByStatusOrderByIdDesc(true)).thenReturn(List.of(activeCampaign));
      when(patientRepository.count()).thenReturn(100L);
      when(examRepository.countTotalExamined()).thenReturn(30L);

      DashboardStatsDTO result = service.getDashboardStats();

      assertThat(result.getTotalCampaigns()).isEqualTo(5L);
      assertThat(result.getActiveCampaigns()).isEqualTo(1L);
      assertThat(result.getTotalStudents()).isEqualTo(100L);
      assertThat(result.getTotalExamined()).isEqualTo(30L);
    }

    @Test
    @DisplayName("Trả về số 0 khi không có dữ liệu")
    void shouldReturnZerosWhenNoData() {
      when(examCampaignRepository.count()).thenReturn(0L);
      when(examCampaignRepository.findAllByStatusOrderByIdDesc(true)).thenReturn(Collections.emptyList());
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
      ExamCampaign c1 = new ExamCampaign();
      c1.setId(1L); c1.setStatus(true);

      when(examCampaignRepository.count()).thenReturn(3L);
      when(examCampaignRepository.findAllByStatusOrderByIdDesc(true)).thenReturn(List.of(c1));
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
      Exam exam = buildExam(1L, null, null);
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
