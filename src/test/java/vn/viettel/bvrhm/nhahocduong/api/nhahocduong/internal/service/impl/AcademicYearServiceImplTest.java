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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
}
