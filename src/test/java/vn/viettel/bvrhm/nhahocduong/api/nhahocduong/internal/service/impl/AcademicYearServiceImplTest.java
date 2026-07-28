package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.AcademicYearStatus;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.AffiliationStatus;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.CampaignStatus;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.AcademicYearDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TransitionResultDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.YearTransitionRequest;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.AcademicYear;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamCampaign;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.StudentClassAffiliation;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.SystemLog;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.AcademicYearRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ClassRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamCampaignRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.OrganizationRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.StudentClassAffiliationRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.SystemLogRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AcademicYearServiceImpl Unit Tests")
class AcademicYearServiceImplTest {

    @Mock private AcademicYearRepository academicYearRepository;
    @Mock private ClassRepository classRepository;
    @Mock private StudentClassAffiliationRepository affiliationRepository;
    @Mock private ExamCampaignRepository campaignRepository;
    @Mock private SystemLogRepository systemLogRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private OrganizationRepository organizationRepository;

    @InjectMocks private AcademicYearServiceImpl academicYearService;

    @Captor private ArgumentCaptor<AcademicYear> yearCaptor;
    @Captor private ArgumentCaptor<SystemLog> logCaptor;
    @Captor private ArgumentCaptor<StudentClassAffiliation> affCaptor;

    // ─── Helper methods ────────────────────────────────────────────────

    private AcademicYear createCurrentYear() {
        AcademicYear year = new AcademicYear();
        year.setId(1L);
        year.setName("2025-2026");
        year.setStartDate(LocalDate.of(2025, 9, 1));
        year.setEndDate(LocalDate.of(2026, 6, 30));
        year.setStatus(AcademicYearStatus.CURRENT);
        year.setCreatedDate(LocalDateTime.now());
        year.setUpdatedDate(LocalDateTime.now());
        year.setCreatedBy("system");
        return year;
    }

    private AcademicYear createUpcomingYear() {
        AcademicYear year = new AcademicYear();
        year.setId(2L);
        year.setName("2026-2027");
        year.setStartDate(LocalDate.of(2026, 9, 1));
        year.setEndDate(LocalDate.of(2027, 6, 30));
        year.setStatus(AcademicYearStatus.UPCOMING);
        year.setCreatedDate(LocalDateTime.now());
        year.setUpdatedDate(LocalDateTime.now());
        year.setCreatedBy("system");
        return year;
    }

    private AcademicYear createCompletedYear() {
        AcademicYear year = new AcademicYear();
        year.setId(3L);
        year.setName("2024-2025");
        year.setStatus(AcademicYearStatus.COMPLETED);
        return year;
    }

    private YearTransitionRequest createTransitionRequest() {
        YearTransitionRequest request = new YearTransitionRequest();
        request.setNewYearName("2026-2027");
        request.setStartDate(LocalDate.of(2026, 9, 1));
        request.setEndDate(LocalDate.of(2027, 6, 30));
        return request;
    }

    private Organization createMockSchool() {
        Organization school = new Organization();
        school.setId(10L);
        school.setName("Truong Tieu Hoc ABC");
        school.setCode("001001");
        return school;
    }

    private vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class createMockClass(
            Long id, String name, String grade, String room, Organization school, AcademicYear year) {
        vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class clazz =
            new vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class();
        clazz.setId(id);
        clazz.setName(name);
        clazz.setGrade(grade);
        clazz.setRoom(room);
        clazz.setSchool(school);
        clazz.setAcademicYear(year);
        clazz.setStatus(true);
        return clazz;
    }

    private StudentClassAffiliation createMockAffiliation(Long id, Patient student,
            vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class clazz,
            AcademicYear year, AffiliationStatus status) {
        StudentClassAffiliation aff = new StudentClassAffiliation();
        aff.setId(id);
        aff.setStudent(student);
        aff.setStudentClass(clazz);
        aff.setAcademicYear(year);
        aff.setStatus(status);
        aff.setCreatedDate(LocalDateTime.now());
        aff.setUpdatedDate(LocalDateTime.now());
        return aff;
    }

    private Patient createMockStudent(Long id, String name) {
        Patient student = new Patient();
        student.setId(id);
        student.setFullName(name);
        student.setStatus(true);
        return student;
    }

    private ExamCampaign createInProgressCampaign(String name) {
        ExamCampaign campaign = new ExamCampaign();
        campaign.setId(1L);
        campaign.setName(name);
        campaign.setCampaignStatus(CampaignStatus.IN_PROGRESS);
        return campaign;
    }

    private SystemLog createAcademicYearLog(String sessionId, Long entityId, String oldValue, String newValue) {
        SystemLog log = new SystemLog();
        log.setSessionId(sessionId);
        log.setAction("YEAR_TRANSITION");
        log.setEntityType("ACADEMIC_YEAR");
        log.setEntityId(entityId);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setCreatedDate(LocalDateTime.now());
        log.setCreatedBy("system");
        return log;
    }

    // ─── CRUD Tests ────────────────────────────────────────────────────

    @Nested
    @DisplayName("CRUD Operations")
    class CrudTests {

        @Test
        @DisplayName("getAll() returns all academic years as DTOs")
        void getAll_returnsList() {
            // Arrange
            AcademicYear year = createCurrentYear();
            when(academicYearRepository.findAll()).thenReturn(List.of(year));

            // Act
            List<AcademicYearDTO> result = academicYearService.getAll();

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("2025-2026");
            assertThat(result.get(0).getStatus()).isEqualTo("CURRENT");
        }

        @Test
        @DisplayName("getById() returns DTO for existing year")
        void getById_exists_returnsDTO() {
            // Arrange
            AcademicYear year = createCurrentYear();
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(year));

            // Act
            AcademicYearDTO result = academicYearService.getById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("2025-2026");
        }

        @Test
        @DisplayName("getById() throws when year not found")
        void getById_notFound_throwsException() {
            // Arrange
            when(academicYearRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> academicYearService.getById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("999");
        }

        @Test
        @DisplayName("getCurrentYear() returns current year DTO")
        void getCurrentYear_exists_returnsDTO() {
            // Arrange
            AcademicYear year = createCurrentYear();
            when(academicYearRepository.findByStatus(AcademicYearStatus.CURRENT))
                .thenReturn(Optional.of(year));

            // Act
            AcademicYearDTO result = academicYearService.getCurrentYear();

            // Assert
            assertThat(result.getStatus()).isEqualTo("CURRENT");
        }

        @Test
        @DisplayName("getCurrentYear() throws when no current year")
        void getCurrentYear_noCurrentYear_throws() {
            // Arrange
            when(academicYearRepository.findByStatus(AcademicYearStatus.CURRENT))
                .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> academicYearService.getCurrentYear())
                .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("create() saves and returns DTO with UPCOMING status")
        void create_upcomingStatus_savesAndReturns() {
            // Arrange
            AcademicYearDTO dto = new AcademicYearDTO(null, "2026-2027",
                LocalDate.of(2026, 9, 1), LocalDate.of(2027, 6, 30), "UPCOMING");

            AcademicYear saved = createUpcomingYear();
            when(academicYearRepository.save(any(AcademicYear.class))).thenReturn(saved);

            // Act
            AcademicYearDTO result = academicYearService.create(dto);

            // Assert
            assertThat(result.getName()).isEqualTo("2026-2027");
            verify(academicYearRepository).save(yearCaptor.capture());
            assertThat(yearCaptor.getValue().getStatus()).isEqualTo(AcademicYearStatus.UPCOMING);
        }

        @Test
        @DisplayName("create() with CURRENT throws if CURRENT year already exists")
        void create_currentAlreadyExists_throws() {
            // Arrange
            AcademicYearDTO dto = new AcademicYearDTO(null, "2026-2027",
                LocalDate.of(2026, 9, 1), LocalDate.of(2027, 6, 30), "CURRENT");
            when(academicYearRepository.existsByStatus(AcademicYearStatus.CURRENT)).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> academicYearService.create(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("CURRENT");
        }

        @Test
        @DisplayName("create() with null status defaults to UPCOMING")
        void create_nullStatus_defaultsToUpcoming() {
            // Arrange
            AcademicYearDTO dto = new AcademicYearDTO(null, "2026-2027",
                LocalDate.of(2026, 9, 1), LocalDate.of(2027, 6, 30), null);

            // null status treated as CURRENT check path:
            // if (dto.getStatus() != null && "CURRENT".equals(dto.getStatus()) || dto.getStatus() == null)
            // This evaluates true for null status, so it checks existsByStatus
            when(academicYearRepository.existsByStatus(AcademicYearStatus.CURRENT)).thenReturn(false);

            AcademicYear saved = createUpcomingYear();
            when(academicYearRepository.save(any(AcademicYear.class))).thenReturn(saved);

            // Act
            AcademicYearDTO result = academicYearService.create(dto);

            // Assert
            verify(academicYearRepository).save(yearCaptor.capture());
            assertThat(yearCaptor.getValue().getStatus()).isEqualTo(AcademicYearStatus.UPCOMING);
        }

        @Test
        @DisplayName("delete() throws when deleting CURRENT year")
        void delete_currentYear_throws() {
            // Arrange
            AcademicYear year = createCurrentYear();
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(year));

            // Act & Assert
            assertThatThrownBy(() -> academicYearService.delete(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("đang diễn ra");

            verify(academicYearRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("delete() deletes COMPLETED year")
        void delete_completedYear_deletes() {
            // Arrange
            AcademicYear year = createCompletedYear();
            when(academicYearRepository.findById(3L)).thenReturn(Optional.of(year));

            // Act
            academicYearService.delete(3L);

            // Assert
            verify(academicYearRepository).deleteById(3L);
        }
    }

    // ─── validateBeforeTransition() Tests ──────────────────────────────

    @Nested
    @DisplayName("validateBeforeTransition()")
    class ValidateBeforeTransitionTests {

        @Test
        @DisplayName("No active campaigns — returns empty warnings")
        void validateBeforeTransition_noCampaigns_emptyWarnings() {
            // Arrange
            AcademicYear year = createCurrentYear();
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(year));
            when(campaignRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<String> warnings = academicYearService.validateBeforeTransition(1L);

            // Assert
            assertThat(warnings).isEmpty();
        }

        @Test
        @DisplayName("Active campaigns — returns warnings with campaign names")
        void validateBeforeTransition_activeCampaigns_returnsWarnings() {
            // Arrange
            AcademicYear year = createCurrentYear();
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(year));

            ExamCampaign campaign = createInProgressCampaign("Đợt khám tháng 3");
            when(campaignRepository.findAll()).thenReturn(List.of(campaign));

            // Act
            List<String> warnings = academicYearService.validateBeforeTransition(1L);

            // Assert
            assertThat(warnings).hasSize(1);
            assertThat(warnings.get(0)).contains("Đợt khám tháng 3");
            assertThat(warnings.get(0)).contains("Đang diễn ra");
        }

        @Test
        @DisplayName("Year not found — throws RuntimeException")
        void validateBeforeTransition_yearNotFound_throws() {
            // Arrange
            when(academicYearRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> academicYearService.validateBeforeTransition(999L))
                .isInstanceOf(RuntimeException.class);
        }
    }

    // ─── transitionToNewYear() Tests ───────────────────────────────────

    @Nested
    @DisplayName("transitionToNewYear()")
    class TransitionTests {

        @Test
        @DisplayName("TC-ACAD-01: Happy path — marks old year COMPLETED, creates new CURRENT, promotes students, generates logs")
        void transition_happyPath_promotesStudentsAndGraduates() {
            // Arrange
            AcademicYear oldYear = createCurrentYear(); // id=1, status=CURRENT
            Organization school = createMockSchool();
            YearTransitionRequest request = createTransitionRequest();

            // Student in grade 5 → should promote to grade 6
            Patient student1 = createMockStudent(100L, "Nguyen Van A");
            vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class oldClass5A =
                createMockClass(50L, "5A", "5", "A", school, oldYear);
            StudentClassAffiliation aff1 = createMockAffiliation(
                500L, student1, oldClass5A, oldYear, AffiliationStatus.STUDYING);

            // Student in grade 12 → should graduate
            Patient student2 = createMockStudent(101L, "Tran Thi B");
            vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class oldClass12A =
                createMockClass(51L, "12A", "12", "A", school, oldYear);
            StudentClassAffiliation aff2 = createMockAffiliation(
                501L, student2, oldClass12A, oldYear, AffiliationStatus.STUDYING);

            when(academicYearRepository.findByStatus(AcademicYearStatus.CURRENT))
                .thenReturn(Optional.of(oldYear));
            when(academicYearRepository.findById(oldYear.getId()))
                .thenReturn(Optional.of(oldYear));
            when(campaignRepository.findAll()).thenReturn(Collections.emptyList());

            // Save old year
            when(academicYearRepository.saveAndFlush(any(AcademicYear.class)))
                .thenAnswer(inv -> inv.getArgument(0));

            // Save new year — return with generated ID
            AcademicYear newYear = new AcademicYear();
            newYear.setId(2L);
            newYear.setName("2026-2027");
            newYear.setStatus(AcademicYearStatus.CURRENT);
            when(academicYearRepository.save(any(AcademicYear.class)))
                .thenReturn(newYear);

            // System log saves
            when(systemLogRepository.save(any(SystemLog.class)))
                .thenAnswer(inv -> inv.getArgument(0));

            // Affiliations for old year
            when(affiliationRepository.findByAcademicYearIdAndStatus(1L, AffiliationStatus.STUDYING))
                .thenReturn(List.of(aff1, aff2));

            // Grade 5 → 6: no existing class found, will create one
            when(classRepository.findByNameAndSchoolIdAndAcademicYearId("6A", 10L, 2L))
                .thenReturn(Optional.empty());

            // Save new class
            vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class newClass6A =
                createMockClass(60L, "6A", "6", "A", school, newYear);
            when(classRepository.save(any(vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class.class)))
                .thenReturn(newClass6A);

            // Save new affiliation
            StudentClassAffiliation newAff = new StudentClassAffiliation();
            newAff.setId(600L);
            when(affiliationRepository.save(any(StudentClassAffiliation.class)))
                .thenReturn(newAff);

            // Act
            TransitionResultDTO result = academicYearService.transitionToNewYear(request);

            // Assert
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getOldYearId()).isEqualTo(1L);
            assertThat(result.getNewYearId()).isEqualTo(2L);
            assertThat(result.getNewYearName()).isEqualTo("2026-2027");
            assertThat(result.getPromotedCount()).isEqualTo(1);
            assertThat(result.getGraduatedCount()).isEqualTo(1);
            assertThat(result.getSessionId()).isNotNull();

            // Verify old year set to COMPLETED
            verify(academicYearRepository).saveAndFlush(yearCaptor.capture());
            assertThat(yearCaptor.getValue().getStatus()).isEqualTo(AcademicYearStatus.COMPLETED);

            // Verify system logs were generated (at least for YEAR_TRANSITION actions)
            verify(systemLogRepository, atLeastOnce()).save(logCaptor.capture());
            List<SystemLog> allLogs = logCaptor.getAllValues();
            assertThat(allLogs).isNotEmpty();
            assertThat(allLogs.stream().allMatch(l -> "YEAR_TRANSITION".equals(l.getAction()))).isTrue();

            // Verify graduated student affiliation was updated
            assertThat(aff2.getStatus()).isEqualTo(AffiliationStatus.GRADUATED);
        }

        @Test
        @DisplayName("No current year — throws RuntimeException")
        void transition_noCurrentYear_throws() {
            // Arrange
            when(academicYearRepository.findByStatus(AcademicYearStatus.CURRENT))
                .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> academicYearService.transitionToNewYear(createTransitionRequest()))
                .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("Transition with warnings — includes warnings in result")
        void transition_withWarnings_includesWarnings() {
            // Arrange
            AcademicYear oldYear = createCurrentYear();
            YearTransitionRequest request = createTransitionRequest();

            when(academicYearRepository.findByStatus(AcademicYearStatus.CURRENT))
                .thenReturn(Optional.of(oldYear));
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(oldYear));

            ExamCampaign campaign = createInProgressCampaign("Đợt khám Q1");
            when(campaignRepository.findAll()).thenReturn(List.of(campaign));

            when(academicYearRepository.saveAndFlush(any(AcademicYear.class)))
                .thenAnswer(inv -> inv.getArgument(0));

            AcademicYear newYear = new AcademicYear();
            newYear.setId(2L);
            newYear.setName("2026-2027");
            newYear.setStatus(AcademicYearStatus.CURRENT);
            when(academicYearRepository.save(any(AcademicYear.class))).thenReturn(newYear);
            when(systemLogRepository.save(any(SystemLog.class)))
                .thenAnswer(inv -> inv.getArgument(0));
            when(affiliationRepository.findByAcademicYearIdAndStatus(1L, AffiliationStatus.STUDYING))
                .thenReturn(Collections.emptyList());

            // Act
            TransitionResultDTO result = academicYearService.transitionToNewYear(request);

            // Assert
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getWarnings()).isNotEmpty();
            assertThat(result.getWarnings().get(0)).contains("Đợt khám Q1");
        }
    }

    // ─── rollbackTransition() Tests ────────────────────────────────────

    @Nested
    @DisplayName("rollbackTransition()")
    class RollbackTests {

        @Test
        @DisplayName("TC-ACAD-02: Happy path — deletes new year's data, restores old year to CURRENT, cleans logs")
        void rollback_happyPath_restoresOldYear() {
            // Arrange
            String sessionId = "test-session-123";

            // Log entry for old year (has oldValue → means this is the old year being COMPLETED)
            SystemLog oldYearLog = createAcademicYearLog(sessionId, 1L,
                "{\"name\":\"2025-2026\",\"status\":\"CURRENT\"}", "{\"status\":\"COMPLETED\"}");

            // Log entry for new year (null oldValue → means this is the newly created year)
            SystemLog newYearLog = createAcademicYearLog(sessionId, 2L,
                null, "{\"name\":\"2026-2027\",\"status\":\"CURRENT\"}");

            when(systemLogRepository.findBySessionIdOrderByCreatedDateDesc(sessionId))
                .thenReturn(List.of(newYearLog, oldYearLog));

            // Rollback step 2: find classes of new year
            when(classRepository.findByAcademicYearId(2L)).thenReturn(Collections.emptyList());

            // Rollback step 3: find affiliations of old year to restore GRADUATED → STUDYING
            StudentClassAffiliation graduatedAff = new StudentClassAffiliation();
            graduatedAff.setId(500L);
            graduatedAff.setStatus(AffiliationStatus.GRADUATED);
            when(affiliationRepository.findByAcademicYearId(1L))
                .thenReturn(List.of(graduatedAff));
            when(affiliationRepository.save(any(StudentClassAffiliation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

            // Rollback step 5: restore old year to CURRENT
            AcademicYear oldYear = createCurrentYear();
            oldYear.setStatus(AcademicYearStatus.COMPLETED); // was set to COMPLETED during transition
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(oldYear));
            when(academicYearRepository.saveAndFlush(any(AcademicYear.class)))
                .thenAnswer(inv -> inv.getArgument(0));

            // Act
            TransitionResultDTO result = academicYearService.rollbackTransition(sessionId);

            // Assert
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getSessionId()).isEqualTo(sessionId);
            assertThat(result.getMessage()).contains("Khôi phục thành công");

            // Verify new year affiliations deleted
            verify(affiliationRepository).deleteByAcademicYearId(2L);

            // Verify new year deleted
            verify(academicYearRepository).deleteById(2L);
            verify(academicYearRepository).flush();

            // Verify old year restored to CURRENT
            verify(academicYearRepository).saveAndFlush(yearCaptor.capture());
            assertThat(yearCaptor.getValue().getStatus()).isEqualTo(AcademicYearStatus.CURRENT);

            // Verify graduated affiliation restored to STUDYING
            assertThat(graduatedAff.getStatus()).isEqualTo(AffiliationStatus.STUDYING);

            // Verify system logs cleaned up
            verify(systemLogRepository).deleteBySessionId(sessionId);
        }

        @Test
        @DisplayName("Invalid sessionId — throws RuntimeException")
        void rollback_invalidSessionId_throws() {
            // Arrange
            when(systemLogRepository.findBySessionIdOrderByCreatedDateDesc("non-existent"))
                .thenReturn(Collections.emptyList());

            // Act & Assert
            assertThatThrownBy(() -> academicYearService.rollbackTransition("non-existent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("non-existent");
        }

        @Test
        @DisplayName("Rollback with classes — deletes new year's classes too")
        void rollback_withClasses_deletesNewYearClasses() {
            // Arrange
            String sessionId = "rollback-classes-session";
            SystemLog oldYearLog = createAcademicYearLog(sessionId, 1L,
                "{\"name\":\"2025-2026\",\"status\":\"CURRENT\"}", "{\"status\":\"COMPLETED\"}");
            SystemLog newYearLog = createAcademicYearLog(sessionId, 2L,
                null, "{\"name\":\"2026-2027\",\"status\":\"CURRENT\"}");

            when(systemLogRepository.findBySessionIdOrderByCreatedDateDesc(sessionId))
                .thenReturn(List.of(newYearLog, oldYearLog));

            // New year has classes to delete
            Organization school = createMockSchool();
            AcademicYear newYear = createUpcomingYear();
            vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class clazz =
                createMockClass(60L, "6A", "6", "A", school, newYear);
            when(classRepository.findByAcademicYearId(2L)).thenReturn(List.of(clazz));

            when(affiliationRepository.findByAcademicYearId(1L))
                .thenReturn(Collections.emptyList());

            AcademicYear oldYear = createCurrentYear();
            oldYear.setStatus(AcademicYearStatus.COMPLETED);
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(oldYear));
            when(academicYearRepository.saveAndFlush(any(AcademicYear.class)))
                .thenAnswer(inv -> inv.getArgument(0));

            // Act
            TransitionResultDTO result = academicYearService.rollbackTransition(sessionId);

            // Assert
            assertThat(result.isSuccess()).isTrue();
            verify(classRepository).delete(clazz);
        }
    }

    // ─── update() Tests ────────────────────────────────────────────────

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("Happy path — updates existing year")
        void update_existingYear_updatesFields() {
            // Arrange
            AcademicYear existing = createCurrentYear();
            AcademicYearDTO dto = new AcademicYearDTO(1L, "2025-2026 (Updated)",
                LocalDate.of(2025, 9, 1), LocalDate.of(2026, 7, 15), "CURRENT");

            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(academicYearRepository.findByStatus(AcademicYearStatus.CURRENT))
                .thenReturn(Optional.of(existing)); // same year → OK
            when(academicYearRepository.save(any(AcademicYear.class))).thenReturn(existing);

            // Act
            AcademicYearDTO result = academicYearService.update(1L, dto);

            // Assert
            verify(academicYearRepository).save(yearCaptor.capture());
            assertThat(yearCaptor.getValue().getName()).isEqualTo("2025-2026 (Updated)");
        }

        @Test
        @DisplayName("Setting CURRENT on different year when one already exists — throws")
        void update_setCurrentOnDifferentYear_throws() {
            // Arrange
            AcademicYear yearToUpdate = createUpcomingYear(); // id=2
            AcademicYear currentYear = createCurrentYear(); // id=1

            AcademicYearDTO dto = new AcademicYearDTO(2L, "2026-2027",
                LocalDate.of(2026, 9, 1), LocalDate.of(2027, 6, 30), "CURRENT");

            when(academicYearRepository.findById(2L)).thenReturn(Optional.of(yearToUpdate));
            when(academicYearRepository.findByStatus(AcademicYearStatus.CURRENT))
                .thenReturn(Optional.of(currentYear)); // different id!

            // Act & Assert
            assertThatThrownBy(() -> academicYearService.update(2L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("CURRENT");
        }
    }

    // ─── delete() additional edge cases ───────────────────────────────

    @Nested
    @DisplayName("delete() — additional cases")
    class DeleteAdditionalTests {

        @Test
        @DisplayName("delete() deletes UPCOMING year")
        void delete_upcomingYear_deletes() {
            AcademicYear year = createUpcomingYear();
            when(academicYearRepository.findById(2L)).thenReturn(Optional.of(year));

            academicYearService.delete(2L);

            verify(academicYearRepository).deleteById(2L);
        }

        @Test
        @DisplayName("delete() throws when year not found")
        void delete_notFound_throws() {
            when(academicYearRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> academicYearService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("999");
        }
    }

    // ─── getTransitionHistory() Tests ─────────────────────────────────

    @Nested
    @DisplayName("getTransitionHistory()")
    class GetTransitionHistoryTests {

        @Test
        @DisplayName("Trả về danh sách rỗng khi không có log")
        void shouldReturnEmptyWhenNoLogs() {
            when(systemLogRepository.findAll()).thenReturn(Collections.emptyList());

            List<Map<String, Object>> result = academicYearService.getTransitionHistory();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Trả về lịch sử chuyển năm với thông tin năm cũ/mới")
        void shouldReturnHistoryWithYearInfo() {
            String sessionId = "session-1";

            SystemLog oldYearLog = createAcademicYearLog(sessionId, 1L,
                "{\"name\":\"2025-2026\",\"status\":\"CURRENT\"}",
                "{\"status\":\"COMPLETED\"}");
            SystemLog newYearLog = createAcademicYearLog(sessionId, 2L,
                null,
                "{\"name\":\"2026-2027\",\"status\":\"CURRENT\"}");

            when(systemLogRepository.findAll()).thenReturn(List.of(oldYearLog, newYearLog));

            List<Map<String, Object>> result = academicYearService.getTransitionHistory();

            assertThat(result).hasSize(1);
            Map<String, Object> session = result.get(0);
            assertThat(session.get("sessionId")).isEqualTo(sessionId);
            assertThat(session.get("action")).isEqualTo("Chuyển năm học");
            assertThat(session.get("time")).isNotNull();

            @SuppressWarnings("unchecked")
            List<String> summary = (List<String>) session.get("summary");
            assertThat(summary).isNotEmpty();
            assertThat(summary.stream().anyMatch(s -> s.contains("2025-2026"))).isTrue();
            assertThat(summary.stream().anyMatch(s -> s.contains("2026-2027"))).isTrue();
        }

        @Test
        @DisplayName("Trả về lịch sử với học sinh lên lớp")
        void shouldReturnHistoryWithPromotedStudents() {
            String sessionId = "session-2";

            SystemLog oldYearLog = createAcademicYearLog(sessionId, 1L,
                "{\"name\":\"2025-2026\",\"status\":\"CURRENT\"}",
                "{\"status\":\"COMPLETED\"}");
            SystemLog newYearLog = createAcademicYearLog(sessionId, 2L,
                null, "{\"name\":\"2026-2027\",\"status\":\"CURRENT\"}");

            SystemLog affLog = new SystemLog();
            affLog.setSessionId(sessionId);
            affLog.setAction("YEAR_TRANSITION");
            affLog.setEntityType("STUDENT_AFFILIATION");
            affLog.setEntityId(100L);
            affLog.setOldValue(null);
            affLog.setNewValue("{\"student_id\":100,\"class_id\":50,\"status\":\"STUDYING\"}");
            affLog.setCreatedDate(LocalDateTime.now());

            Patient student = createMockStudent(100L, "Nguyen Van A");
            vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class clazz =
                createMockClass(50L, "6A", "6", "A", createMockSchool(), createUpcomingYear());

            when(systemLogRepository.findAll()).thenReturn(List.of(oldYearLog, newYearLog, affLog));
            when(patientRepository.findById(100L)).thenReturn(Optional.of(student));
            when(classRepository.findById(50L)).thenReturn(Optional.of(clazz));

            List<Map<String, Object>> result = academicYearService.getTransitionHistory();

            assertThat(result).hasSize(1);
            @SuppressWarnings("unchecked")
            List<String> summary = (List<String>) result.get(0).get("summary");
            assertThat(summary.stream().anyMatch(s -> s.contains("lên"))).isTrue();
        }

        @Test
        @DisplayName("Trả về lịch sử với học sinh tốt nghiệp")
        void shouldReturnHistoryWithGraduatedStudents() {
            String sessionId = "session-3";

            SystemLog oldYearLog = createAcademicYearLog(sessionId, 1L,
                "{\"name\":\"2025-2026\",\"status\":\"CURRENT\"}",
                "{\"status\":\"COMPLETED\"}");
            SystemLog newYearLog = createAcademicYearLog(sessionId, 2L,
                null, "{\"name\":\"2026-2027\",\"status\":\"CURRENT\"}");

            SystemLog gradLog = new SystemLog();
            gradLog.setSessionId(sessionId);
            gradLog.setAction("YEAR_TRANSITION");
            gradLog.setEntityType("STUDENT_AFFILIATION");
            gradLog.setEntityId(200L);
            gradLog.setOldValue("{\"student_id\":200,\"status\":\"GRADUATED\"}");
            gradLog.setNewValue("{\"status\":\"GRADUATED\"}");
            gradLog.setCreatedDate(LocalDateTime.now());

            Patient student = createMockStudent(200L, "Tran Thi B");
            when(systemLogRepository.findAll()).thenReturn(List.of(oldYearLog, newYearLog, gradLog));
            when(patientRepository.findById(200L)).thenReturn(Optional.of(student));

            List<Map<String, Object>> result = academicYearService.getTransitionHistory();

            assertThat(result).hasSize(1);
            @SuppressWarnings("unchecked")
            List<String> summary = (List<String>) result.get(0).get("summary");
            assertThat(summary.stream().anyMatch(s -> s.contains("tốt nghiệp"))).isTrue();
        }

        @Test
        @DisplayName("Sắp xếp theo thời gian giảm dần")
        void shouldSortByTimeDescending() {
            SystemLog log1 = createAcademicYearLog("s1", 1L,
                "{\"name\":\"2024-2025\",\"status\":\"CURRENT\"}",
                "{\"status\":\"COMPLETED\"}");
            log1.setCreatedDate(LocalDateTime.of(2025, 6, 1, 10, 0));

            SystemLog log2 = createAcademicYearLog("s2", 3L,
                "{\"name\":\"2025-2026\",\"status\":\"CURRENT\"}",
                "{\"status\":\"COMPLETED\"}");
            log2.setCreatedDate(LocalDateTime.of(2026, 6, 1, 10, 0));

            when(systemLogRepository.findAll()).thenReturn(List.of(log1, log2));

            List<Map<String, Object>> result = academicYearService.getTransitionHistory();

            assertThat(result).hasSize(2);
            assertThat((LocalDateTime) result.get(0).get("time"))
                .isAfter((LocalDateTime) result.get(1).get("time"));
        }
    }

    // ─── Additional branch coverage tests ─────────────────────────────

    @Nested
    @DisplayName("create() — additional branches")
    class CreateAdditionalTests {

        @Test
        @DisplayName("create() with CURRENT status when no CURRENT exists — saves with CURRENT status")
        void create_currentStatusNoExisting_savesCurrent() {
            // Arrange
            AcademicYearDTO dto = new AcademicYearDTO(null, "2026-2027",
                LocalDate.of(2026, 9, 1), LocalDate.of(2027, 6, 30), "CURRENT");

            when(academicYearRepository.existsByStatus(AcademicYearStatus.CURRENT)).thenReturn(false);

            AcademicYear saved = createCurrentYear();
            when(academicYearRepository.save(any(AcademicYear.class))).thenReturn(saved);

            // Act
            AcademicYearDTO result = academicYearService.create(dto);

            // Assert
            assertThat(result.getName()).isEqualTo("2025-2026");
            verify(academicYearRepository).save(yearCaptor.capture());
            assertThat(yearCaptor.getValue().getStatus()).isEqualTo(AcademicYearStatus.CURRENT);
        }

        @Test
        @DisplayName("create() with null status when CURRENT exists — throws RuntimeException")
        void create_nullStatusCurrentExists_throws() {
            // Arrange
            AcademicYearDTO dto = new AcademicYearDTO(null, "2026-2027",
                LocalDate.of(2026, 9, 1), LocalDate.of(2027, 6, 30), null);

            when(academicYearRepository.existsByStatus(AcademicYearStatus.CURRENT)).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> academicYearService.create(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("CURRENT");
        }
    }

    @Nested
    @DisplayName("update() — additional branches")
    class UpdateAdditionalTests {

        @Test
        @DisplayName("update() with null status — entity status unchanged")
        void update_nullStatus_statusUnchanged() {
            // Arrange
            AcademicYear existing = createCurrentYear();
            existing.setStatus(AcademicYearStatus.CURRENT);
            AcademicYearDTO dto = new AcademicYearDTO(1L, "2025-2026 (Updated)",
                LocalDate.of(2025, 9, 1), LocalDate.of(2026, 7, 15), null);

            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(academicYearRepository.save(any(AcademicYear.class))).thenReturn(existing);

            // Act
            AcademicYearDTO result = academicYearService.update(1L, dto);

            // Assert
            verify(academicYearRepository).save(yearCaptor.capture());
            assertThat(yearCaptor.getValue().getStatus()).isEqualTo(AcademicYearStatus.CURRENT);
        }

        @Test
        @DisplayName("update() setting CURRENT when no CURRENT exists — succeeds")
        void update_setCurrentWhenNoneExists_succeeds() {
            // Arrange
            AcademicYear existing = createUpcomingYear(); // id=2, status=UPCOMING
            AcademicYearDTO dto = new AcademicYearDTO(2L, "2026-2027",
                LocalDate.of(2026, 9, 1), LocalDate.of(2027, 6, 30), "CURRENT");

            when(academicYearRepository.findById(2L)).thenReturn(Optional.of(existing));
            when(academicYearRepository.findByStatus(AcademicYearStatus.CURRENT))
                .thenReturn(Optional.empty());
            when(academicYearRepository.save(any(AcademicYear.class))).thenReturn(existing);

            // Act
            AcademicYearDTO result = academicYearService.update(2L, dto);

            // Assert
            verify(academicYearRepository).save(yearCaptor.capture());
            assertThat(yearCaptor.getValue().getStatus()).isEqualTo(AcademicYearStatus.CURRENT);
        }
    }

    @Nested
    @DisplayName("validateBeforeTransition() — additional branches")
    class ValidateAdditionalTests {

        @Test
        @DisplayName("Non-IN_PROGRESS campaigns — returns empty warnings")
        void validate_nonInProgressCampaigns_emptyWarnings() {
            // Arrange
            AcademicYear year = createCurrentYear();
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(year));

            ExamCampaign completedCampaign = new ExamCampaign();
            completedCampaign.setId(2L);
            completedCampaign.setName("Đợt khám đã xong");
            completedCampaign.setCampaignStatus(CampaignStatus.COMPLETED);

            ExamCampaign upcomingCampaign = new ExamCampaign();
            upcomingCampaign.setId(3L);
            upcomingCampaign.setName("Đợt khám sắp tới");
            upcomingCampaign.setCampaignStatus(CampaignStatus.UPCOMING);

            when(campaignRepository.findAll()).thenReturn(List.of(completedCampaign, upcomingCampaign));

            // Act
            List<String> warnings = academicYearService.validateBeforeTransition(1L);

            // Assert
            assertThat(warnings).isEmpty();
        }
    }

    @Nested
    @DisplayName("transitionToNewYear() — exception handling")
    class TransitionExceptionTests {

        @Test
        @DisplayName("Exception during transition — wraps and rethrows RuntimeException")
        void transition_exceptionInTryBlock_throwsWrappedException() {
            // Arrange
            AcademicYear oldYear = createCurrentYear();
            YearTransitionRequest request = createTransitionRequest();

            when(academicYearRepository.findByStatus(AcademicYearStatus.CURRENT))
                .thenReturn(Optional.of(oldYear));
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(oldYear));

            // Make campaignRepository.findAll() throw to trigger catch block early
            when(campaignRepository.findAll())
                .thenThrow(new RuntimeException("DB connection lost"));

            // Act & Assert
            assertThatThrownBy(() -> academicYearService.transitionToNewYear(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Chuyển năm học thất bại")
                .hasMessageContaining("DB connection lost");
        }
    }

    @Nested
    @DisplayName("autoPromoteStudents — additional branches")
    class AutoPromoteAdditionalTests {

        @Test
        @DisplayName("Target class already exists — no new class created")
        void autoPromote_targetClassExists_noNewClassCreated() {
            // Arrange
            AcademicYear oldYear = createCurrentYear();
            AcademicYear newYear = createUpcomingYear();
            Organization school = createMockSchool();

            // Student in grade 5 → should promote to grade 6
            Patient student = createMockStudent(100L, "Nguyen Van A");
            vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class oldClass =
                createMockClass(50L, "5A", "5", "A", school, oldYear);
            StudentClassAffiliation aff = createMockAffiliation(
                500L, student, oldClass, oldYear, AffiliationStatus.STUDYING);

            when(academicYearRepository.findByStatus(AcademicYearStatus.CURRENT))
                .thenReturn(Optional.of(oldYear));
            when(academicYearRepository.findById(oldYear.getId()))
                .thenReturn(Optional.of(oldYear));
            when(campaignRepository.findAll()).thenReturn(Collections.emptyList());

            when(academicYearRepository.saveAndFlush(any(AcademicYear.class)))
                .thenAnswer(inv -> inv.getArgument(0));

            when(academicYearRepository.save(any(AcademicYear.class)))
                .thenReturn(newYear);

            when(systemLogRepository.save(any(SystemLog.class)))
                .thenAnswer(inv -> inv.getArgument(0));

            when(affiliationRepository.findByAcademicYearIdAndStatus(1L, AffiliationStatus.STUDYING))
                .thenReturn(List.of(aff));

            // Class already exists for the new year
            vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class existingNewClass =
                createMockClass(60L, "6A", "6", "A", school, newYear);
            when(classRepository.findByNameAndSchoolIdAndAcademicYearId("6A", 10L, 2L))
                .thenReturn(Optional.of(existingNewClass));

            StudentClassAffiliation newAff = new StudentClassAffiliation();
            newAff.setId(600L);
            when(affiliationRepository.save(any(StudentClassAffiliation.class)))
                .thenReturn(newAff);

            // Act
            TransitionResultDTO result = academicYearService.transitionToNewYear(
                createTransitionRequest());

            // Assert
            assertThat(result.isSuccess()).isTrue();
            // Verify classRepository.save was NOT called for class creation
            // (it's called for affiliations, but not for Class entity)
            verify(classRepository, never()).save(
                any(vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class.class));
            assertThat(result.getPromotedCount()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("rollbackTransition() — additional branches")
    class RollbackAdditionalTests {

        @Test
        @DisplayName("newYearId is null — delete and class deletion calls are skipped")
        void rollback_newYearIdNull_skipsDeleteCalls() {
            // Arrange
            String sessionId = "session-no-new-year";

            // Only old year log (with oldValue), no new year log (null oldValue)
            SystemLog oldYearLog = createAcademicYearLog(sessionId, 1L,
                "{\"name\":\"2025-2026\",\"status\":\"CURRENT\"}",
                "{\"status\":\"COMPLETED\"}");

            when(systemLogRepository.findBySessionIdOrderByCreatedDateDesc(sessionId))
                .thenReturn(List.of(oldYearLog));

            // No affiliations for old year
            when(affiliationRepository.findByAcademicYearId(1L))
                .thenReturn(Collections.emptyList());

            AcademicYear oldYear = createCurrentYear();
            oldYear.setStatus(AcademicYearStatus.COMPLETED);
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(oldYear));
            when(academicYearRepository.saveAndFlush(any(AcademicYear.class)))
                .thenAnswer(inv -> inv.getArgument(0));

            // Act
            TransitionResultDTO result = academicYearService.rollbackTransition(sessionId);

            // Assert
            assertThat(result.isSuccess()).isTrue();
            // newYearId is null → these should never be called
            verify(affiliationRepository, never()).deleteByAcademicYearId(anyLong());
            verify(academicYearRepository, never()).deleteById(anyLong());
            verify(academicYearRepository, never()).flush();
        }

        @Test
        @DisplayName("oldYearId is null — affiliation restore and old year restore are skipped")
        void rollback_oldYearIdNull_skipsRestoreCalls() {
            // Arrange
            String sessionId = "session-no-old-year";

            // Only new year log (null oldValue), no old year log
            SystemLog newYearLog = createAcademicYearLog(sessionId, 2L,
                null, "{\"name\":\"2026-2027\",\"status\":\"CURRENT\"}");

            when(systemLogRepository.findBySessionIdOrderByCreatedDateDesc(sessionId))
                .thenReturn(List.of(newYearLog));

            // new year classes to delete
            when(classRepository.findByAcademicYearId(2L)).thenReturn(Collections.emptyList());

            // Act
            TransitionResultDTO result = academicYearService.rollbackTransition(sessionId);

            // Assert
            assertThat(result.isSuccess()).isTrue();
            // oldYearId is null → these should never be called
            verify(affiliationRepository, never()).findByAcademicYearId(1L);
            verify(academicYearRepository, never()).findById(1L);
            verify(academicYearRepository, never()).saveAndFlush(any(AcademicYear.class));
            // newYearId is 2 → deletion should happen
            verify(affiliationRepository).deleteByAcademicYearId(2L);
            verify(academicYearRepository).deleteById(2L);
        }

        @Test
        @DisplayName("Non-GRADUATED affiliation — not restored to STUDYING")
        void rollback_nonGraduatedAffiliation_notRestored() {
            // Arrange
            String sessionId = "session-studying-aff";

            SystemLog oldYearLog = createAcademicYearLog(sessionId, 1L,
                "{\"name\":\"2025-2026\",\"status\":\"CURRENT\"}",
                "{\"status\":\"COMPLETED\"}");
            SystemLog newYearLog = createAcademicYearLog(sessionId, 2L,
                null, "{\"name\":\"2026-2027\",\"status\":\"CURRENT\"}");

            when(systemLogRepository.findBySessionIdOrderByCreatedDateDesc(sessionId))
                .thenReturn(List.of(newYearLog, oldYearLog));

            when(classRepository.findByAcademicYearId(2L)).thenReturn(Collections.emptyList());

            // Old year has a STUDYING affiliation (not GRADUATED)
            StudentClassAffiliation studyingAff = new StudentClassAffiliation();
            studyingAff.setId(501L);
            studyingAff.setStatus(AffiliationStatus.STUDYING);
            when(affiliationRepository.findByAcademicYearId(1L))
                .thenReturn(List.of(studyingAff));

            AcademicYear oldYear = createCurrentYear();
            oldYear.setStatus(AcademicYearStatus.COMPLETED);
            when(academicYearRepository.findById(1L)).thenReturn(Optional.of(oldYear));
            when(academicYearRepository.saveAndFlush(any(AcademicYear.class)))
                .thenAnswer(inv -> inv.getArgument(0));

            // Act
            TransitionResultDTO result = academicYearService.rollbackTransition(sessionId);

            // Assert
            assertThat(result.isSuccess()).isTrue();
            // Affiliation was STUDYING, not GRADUATED → should NOT be saved/restored
            verify(affiliationRepository, never()).save(any(StudentClassAffiliation.class));
            assertThat(studyingAff.getStatus()).isEqualTo(AffiliationStatus.STUDYING);
        }

        @Test
        @DisplayName("findById oldYearId returns empty — saveAndFlush not called for old year")
        void rollback_oldYearIdNotFound_saveNotCalled() {
            // Arrange
            String sessionId = "session-old-year-missing";

            SystemLog oldYearLog = createAcademicYearLog(sessionId, 1L,
                "{\"name\":\"2025-2026\",\"status\":\"CURRENT\"}",
                "{\"status\":\"COMPLETED\"}");
            SystemLog newYearLog = createAcademicYearLog(sessionId, 2L,
                null, "{\"name\":\"2026-2027\",\"status\":\"CURRENT\"}");

            when(systemLogRepository.findBySessionIdOrderByCreatedDateDesc(sessionId))
                .thenReturn(List.of(newYearLog, oldYearLog));

            when(classRepository.findByAcademicYearId(2L)).thenReturn(Collections.emptyList());

            when(affiliationRepository.findByAcademicYearId(1L))
                .thenReturn(Collections.emptyList());

            // findById returns empty (old year was deleted before rollback attempt)
            when(academicYearRepository.findById(1L)).thenReturn(Optional.empty());

            // Act
            TransitionResultDTO result = academicYearService.rollbackTransition(sessionId);

            // Assert
            assertThat(result.isSuccess()).isTrue();
            // findById returned empty → ifPresent skips → saveAndFlush not called for restoring old year
            verify(academicYearRepository, never()).saveAndFlush(any(AcademicYear.class));
        }
    }

    @Nested
    @DisplayName("getTransitionHistory() — additional branches")
    class GetTransitionHistoryAdditionalTests {

        @Test
        @DisplayName("Year log JSON missing 'name' key — oldYearName/newYearName are null")
        void getHistory_missingNameKey_yearNamesNull() {
            String sessionId = "session-missing-name";

            // Old year log with no "name" key in oldValue
            SystemLog oldYearLog = createAcademicYearLog(sessionId, 1L,
                "{\"status\":\"COMPLETED\"}",
                "{\"status\":\"COMPLETED\"}");
            // New year log with no "name" key in newValue
            SystemLog newYearLog = createAcademicYearLog(sessionId, 2L,
                null,
                "{\"status\":\"CURRENT\"}");

            when(systemLogRepository.findAll()).thenReturn(List.of(oldYearLog, newYearLog));

            // Act
            List<Map<String, Object>> result = academicYearService.getTransitionHistory();

            // Assert
            assertThat(result).hasSize(1);
            @SuppressWarnings("unchecked")
            List<String> summary = (List<String>) result.get(0).get("summary");
            // Since oldYearName and newYearName are null, the year transition summary lines are not added
            assertThat(summary.stream().noneMatch(s -> s.contains("Đóng năm học"))).isTrue();
            assertThat(summary.stream().noneMatch(s -> s.contains("Mở năm học mới"))).isTrue();
        }

        @Test
        @DisplayName("Patient not found for promoted student — uses fallback 'HS #id'")
        void getHistory_patientNotFound_usesFallback() {
            String sessionId = "session-patient-missing";

            SystemLog oldYearLog = createAcademicYearLog(sessionId, 1L,
                "{\"name\":\"2025-2026\",\"status\":\"CURRENT\"}",
                "{\"status\":\"COMPLETED\"}");
            SystemLog newYearLog = createAcademicYearLog(sessionId, 2L,
                null, "{\"name\":\"2026-2027\",\"status\":\"CURRENT\"}");

            SystemLog affLog = new SystemLog();
            affLog.setSessionId(sessionId);
            affLog.setAction("YEAR_TRANSITION");
            affLog.setEntityType("STUDENT_AFFILIATION");
            affLog.setEntityId(100L);
            affLog.setOldValue(null);
            affLog.setNewValue("{\"student_id\":100,\"class_id\":50,\"status\":\"STUDYING\"}");
            affLog.setCreatedDate(LocalDateTime.now());

            // Patient not found
            when(systemLogRepository.findAll()).thenReturn(List.of(oldYearLog, newYearLog, affLog));
            when(patientRepository.findById(100L)).thenReturn(Optional.empty());

            // Class also not found
            when(classRepository.findById(50L)).thenReturn(Optional.empty());

            // Act
            List<Map<String, Object>> result = academicYearService.getTransitionHistory();

            // Assert
            assertThat(result).hasSize(1);
            @SuppressWarnings("unchecked")
            List<String> summary = (List<String>) result.get(0).get("summary");
            assertThat(summary.stream().anyMatch(s -> s.contains("HS #100"))).isTrue();
            assertThat(summary.stream().anyMatch(s -> s.contains("Lớp #50"))).isTrue();
        }
    }

    @Nested
    @DisplayName("getTransitionHistory() — malformed JSON branches")
    class GetTransitionHistoryMalformedJsonTests {

        @Test
        @DisplayName("Malformed JSON in oldValue for old year → catch block, oldYearName = null")
        void getHistory_malformedOldValueJson_catchBlock() {
            String sessionId = "session-malformed-old";

            // oldValue contains "name" but is not valid JSON → objectMapper.readTree throws
            SystemLog oldYearLog = createAcademicYearLog(sessionId, 1L,
                "NOT_JSON{name:\"2025-2026\",status:\"CURRENT\"}",
                "{\"status\":\"COMPLETED\"}");
            // newValue for new year is valid
            SystemLog newYearLog = createAcademicYearLog(sessionId, 2L,
                null,
                "{\"name\":\"2026-2027\",\"status\":\"CURRENT\"}");

            when(systemLogRepository.findAll()).thenReturn(List.of(oldYearLog, newYearLog));

            List<Map<String, Object>> result = academicYearService.getTransitionHistory();

            assertThat(result).hasSize(1);
            @SuppressWarnings("unchecked")
            List<String> summary = (List<String>) result.get(0).get("summary");
            // oldYearName is null → year transition summary block not added at all
            assertThat(summary.stream().noneMatch(s -> s.contains("Đóng năm học"))).isTrue();
            assertThat(summary.stream().noneMatch(s -> s.contains("Mở năm học mới"))).isTrue();
            assertThat(summary).isEmpty();
        }

        @Test
        @DisplayName("Malformed JSON in newValue for new year → catch block, newYearName = null")
        void getHistory_malformedNewValueJson_catchBlock() {
            String sessionId = "session-malformed-new";

            // oldValue for old year is valid
            SystemLog oldYearLog = createAcademicYearLog(sessionId, 1L,
                "{\"name\":\"2025-2026\",\"status\":\"CURRENT\"}",
                "{\"status\":\"COMPLETED\"}");
            // newValue contains "name" but is not valid JSON → objectMapper.readTree throws
            SystemLog newYearLog = createAcademicYearLog(sessionId, 2L,
                null,
                "NOT_JSON{name:\"2026-2027\",status:\"CURRENT\"}");

            when(systemLogRepository.findAll()).thenReturn(List.of(oldYearLog, newYearLog));

            List<Map<String, Object>> result = academicYearService.getTransitionHistory();

            assertThat(result).hasSize(1);
            @SuppressWarnings("unchecked")
            List<String> summary = (List<String>) result.get(0).get("summary");
            // newYearName is null → year transition summary block not added at all
            assertThat(summary.stream().noneMatch(s -> s.contains("Đóng năm học"))).isTrue();
            assertThat(summary.stream().noneMatch(s -> s.contains("Mở năm học mới"))).isTrue();
            assertThat(summary).isEmpty();
        }

        @Test
        @DisplayName("Malformed JSON in promoted student newValue → catch block, student skipped")
        void getHistory_malformedPromotedStudentJson_catchBlock() {
            String sessionId = "session-malformed-promoted";

            SystemLog oldYearLog = createAcademicYearLog(sessionId, 1L,
                "{\"name\":\"2025-2026\",\"status\":\"CURRENT\"}",
                "{\"status\":\"COMPLETED\"}");
            SystemLog newYearLog = createAcademicYearLog(sessionId, 2L,
                null, "{\"name\":\"2026-2027\",\"status\":\"CURRENT\"}");

            // STUDENT_AFFILIATION log with malformed JSON in newValue
            SystemLog affLog = new SystemLog();
            affLog.setSessionId(sessionId);
            affLog.setAction("YEAR_TRANSITION");
            affLog.setEntityType("STUDENT_AFFILIATION");
            affLog.setEntityId(100L);
            affLog.setOldValue(null);
            affLog.setNewValue("NOT_JSON{student_id:100,class_id:50}");
            affLog.setCreatedDate(LocalDateTime.now());

            when(systemLogRepository.findAll()).thenReturn(List.of(oldYearLog, newYearLog, affLog));

            List<Map<String, Object>> result = academicYearService.getTransitionHistory();

            assertThat(result).hasSize(1);
            @SuppressWarnings("unchecked")
            List<String> summary = (List<String>) result.get(0).get("summary");
            // Malformed JSON → student not parsed → no "lên" in summary
            assertThat(summary.stream().noneMatch(s -> s.contains("lên"))).isTrue();
            // Year transition info still present
            assertThat(summary.stream().anyMatch(s -> s.contains("2025-2026"))).isTrue();
        }

        @Test
        @DisplayName("Malformed JSON in graduated student oldValue → catch block, student skipped")
        void getHistory_malformedGraduatedStudentJson_catchBlock() {
            String sessionId = "session-malformed-graduated";

            SystemLog oldYearLog = createAcademicYearLog(sessionId, 1L,
                "{\"name\":\"2025-2026\",\"status\":\"CURRENT\"}",
                "{\"status\":\"COMPLETED\"}");
            SystemLog newYearLog = createAcademicYearLog(sessionId, 2L,
                null, "{\"name\":\"2026-2027\",\"status\":\"CURRENT\"}");

            // STUDENT_AFFILIATION log with malformed JSON in oldValue containing "GRADUATED"
            SystemLog gradLog = new SystemLog();
            gradLog.setSessionId(sessionId);
            gradLog.setAction("YEAR_TRANSITION");
            gradLog.setEntityType("STUDENT_AFFILIATION");
            gradLog.setEntityId(200L);
            gradLog.setOldValue("NOT_JSON{student_id:200,status:GRADUATED}");
            gradLog.setNewValue("{\"status\":\"GRADUATED\"}");
            gradLog.setCreatedDate(LocalDateTime.now());

            when(systemLogRepository.findAll()).thenReturn(List.of(oldYearLog, newYearLog, gradLog));

            List<Map<String, Object>> result = academicYearService.getTransitionHistory();

            assertThat(result).hasSize(1);
            @SuppressWarnings("unchecked")
            List<String> summary = (List<String>) result.get(0).get("summary");
            // Malformed JSON → graduated student not parsed → no "tốt nghiệp" in summary
            assertThat(summary.stream().noneMatch(s -> s.contains("tốt nghiệp"))).isTrue();
            // Year transition info still present
            assertThat(summary.stream().anyMatch(s -> s.contains("2026-2027"))).isTrue();
        }
    }

    @Nested
    @DisplayName("toDTO() — additional branches")
    class ToDTOAdditionalTests {

        @Test
        @DisplayName("toDTO with null status — DTO status is null")
        void toDTO_nullStatus_dtoStatusNull() {
            // Arrange
            AcademicYear year = new AcademicYear();
            year.setId(99L);
            year.setName("Null Status Year");
            year.setStartDate(LocalDate.of(2025, 9, 1));
            year.setEndDate(LocalDate.of(2026, 6, 30));
            year.setStatus(null);

            when(academicYearRepository.findById(99L)).thenReturn(Optional.of(year));

            // Act
            AcademicYearDTO result = academicYearService.getById(99L);

            // Assert
            assertThat(result.getStatus()).isNull();
            assertThat(result.getName()).isEqualTo("Null Status Year");
        }
    }
}
