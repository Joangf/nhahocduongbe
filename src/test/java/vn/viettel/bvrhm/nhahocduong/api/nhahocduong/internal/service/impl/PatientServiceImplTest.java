package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthorizationService;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthorizationService.AuthorizationData;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.service.AreaService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.ResponseMessage;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.dto.AreaDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Grade;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.PatientSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DiseaseDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PatientDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Disease;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.helper.OrganizationHelper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.helper.PatientHelper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.PatientMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.DiseaseRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.OrganizationService;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientServiceImpl Unit Tests")
class PatientServiceImplTest {

    @Mock private AreaService areaService;
    @Mock private PatientRepository patientRepository;
    @Mock private OrganizationService organizationService;
    @Mock private PatientMapper patientMapper;
    @Mock private DiseaseRepository diseaseRepository;
    @Mock private EntityManager entityManager;
    @Mock private AuthorizationService authorizationService;
    @Mock private OrganizationHelper organizationHelper;
    @Mock private PatientHelper patientHelper;
    @Mock private ExamRepository examRepository;

    @InjectMocks private PatientServiceImpl patientService;

    @Captor private ArgumentCaptor<Patient> patientCaptor;

    // ─── Helper methods ────────────────────────────────────────────────

    private Patient createMockPatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFullName("Nguyen Van A");
        patient.setCode("TS01001");
        patient.setGender(1);
        patient.setSchoolClass("1A");
        patient.setStatus(true);

        Organization org = new Organization();
        org.setId(10L);
        org.setCode("TS01");
        org.setName("Test School");
        patient.setOrganization(org);

        return patient;
    }

    private PatientDTO createMockPatientDTO() {
        PatientDTO dto = new PatientDTO();
        dto.setId(1L);
        dto.setFullName("Nguyen Van A");
        dto.setCode("TS01001");
        dto.setGender(1);
        dto.setSchoolClass("1A");
        dto.setStatus(true);

        OrganizationDTO orgDTO = new OrganizationDTO();
        orgDTO.setId(10L);
        orgDTO.setCode("TS01");
        orgDTO.setName("Test School");
        dto.setOrganization(orgDTO);

        return dto;
    }

    private OrganizationDTO createMockOrganizationDTO() {
        OrganizationDTO org = new OrganizationDTO();
        org.setId(10L);
        org.setCode("TS01");
        org.setName("Test School");
        org.setClasses(Map.of(
            Grade._1, List.of("1A", "1B"),
            Grade._2, List.of("2A", "2B")
        ));
        return org;
    }

    private AuthorizationData createAdminAuthData() {
        AuthorizationData authData = new AuthorizationData();
        // Admin: no organizationId restriction
        authData.setOrganizationId(null);
        authData.setAreaCode(null);
        return authData;
    }

    private AuthorizationData createSchoolAuthData(Long orgId) {
        AuthorizationData authData = new AuthorizationData();
        authData.setOrganizationId(orgId);
        return authData;
    }

    // ─── createPatient() Tests ─────────────────────────────────────────

    @Nested
    @DisplayName("createPatient()")
    class CreatePatientTests {

        @Test
        @DisplayName("TC-PAT-01: Happy path — validates class, generates code, saves entity, returns DTO")
        void createPatient_happyPath_savesAndReturnsDTO() {
            // Arrange
            PatientDTO inputDTO = createMockPatientDTO();
            inputDTO.setSchoolClass("1A");

            OrganizationDTO orgDTO = createMockOrganizationDTO();
            Patient entity = createMockPatient();
            PatientDTO expectedDTO = createMockPatientDTO();
            expectedDTO.setCode("TS01001");

            when(organizationService.getOrganizationById(10L)).thenReturn(orgDTO);
            when(organizationHelper.getFlattenClassList(orgDTO))
                .thenReturn(List.of("1A", "1B", "2A", "2B"));
            when(patientMapper.toEntity(inputDTO)).thenReturn(entity);
            when(patientHelper.generateCode(inputDTO)).thenReturn("TS01001");
            when(patientMapper.toDto(entity)).thenReturn(expectedDTO);

            // Act
            PatientDTO result = patientService.createPatient(inputDTO);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo("TS01001");
            assertThat(result.getFullName()).isEqualTo("Nguyen Van A");

            // Verify code was generated via PatientHelper
            verify(patientHelper).generateCode(inputDTO);

            // Verify entity was saved and refreshed
            verify(patientRepository).saveAndFlush(patientCaptor.capture());
            assertThat(patientCaptor.getValue().getCode()).isEqualTo("TS01001");
            verify(entityManager).refresh(entity);
        }

        @Test
        @DisplayName("TC-PAT-02: Invalid class — throws HTTP 404 when class not in school's available classes")
        void createPatient_invalidClass_throwsException() {
            // Arrange
            PatientDTO inputDTO = createMockPatientDTO();
            inputDTO.setSchoolClass("5Z"); // Non-existent class

            OrganizationDTO orgDTO = createMockOrganizationDTO();
            when(organizationService.getOrganizationById(10L)).thenReturn(orgDTO);
            when(organizationHelper.getFlattenClassList(orgDTO))
                .thenReturn(List.of("1A", "1B", "2A", "2B"));

            // Act & Assert
            assertThatThrownBy(() -> patientService.createPatient(inputDTO))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(rse.getReason()).contains("5Z");
                });

            // Verify no save happened
            verify(patientRepository, never()).saveAndFlush(any());
            verify(patientHelper, never()).generateCode(any());
        }

        @Test
        @DisplayName("No classes in school — throws exception when getFlattenClassList returns null")
        void createPatient_nullClassList_throwsException() {
            // Arrange
            PatientDTO inputDTO = createMockPatientDTO();
            OrganizationDTO orgDTO = createMockOrganizationDTO();
            orgDTO.setClasses(null);

            when(organizationService.getOrganizationById(10L)).thenReturn(orgDTO);
            when(organizationHelper.getFlattenClassList(orgDTO)).thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> patientService.createPatient(inputDTO))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(rse.getReason()).contains(ResponseMessage.ORGANIZATION_CANT_FOUND_CLASS_OF_SCHOOL);
                });
        }
    }

    // ─── deletePatientById() Tests ─────────────────────────────────────

    @Nested
    @DisplayName("deletePatientById()")
    class DeletePatientTests {

        @Test
        @DisplayName("TC-PAT-03: Delete prevented — throws HTTP 400 when patient has exams")
        void deletePatientById_hasExams_throwsBadRequest() {
            // Arrange
            Patient patient = createMockPatient();
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(authorizationService.authorize()).thenReturn(createAdminAuthData());

            Exam activeExam = new Exam();
            activeExam.setId(100L);
            when(examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(1L, true))
                .thenReturn(List.of(activeExam));

            // Act & Assert
            assertThatThrownBy(() -> patientService.deletePatientById(1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(rse.getReason()).isEqualTo(ResponseMessage.PATIENT_CANT_DELETE_HAS_EXAMS);
                });

            // Verify patient was NOT soft-deleted
            verify(patientRepository, never()).save(any());
        }

        @Test
        @DisplayName("Happy path — deletes (soft-delete) patient when no exams exist")
        void deletePatientById_noExams_softDeletes() {
            // Arrange
            Patient patient = createMockPatient();
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(authorizationService.authorize()).thenReturn(createAdminAuthData());
            when(examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(1L, true))
                .thenReturn(Collections.emptyList());

            // Act
            boolean result = patientService.deletePatientById(1L);

            // Assert
            assertThat(result).isTrue();
            verify(patientRepository).save(patientCaptor.capture());
            assertThat(patientCaptor.getValue().getStatus()).isFalse();
        }

        @Test
        @DisplayName("Patient not found — throws NoSuchElementException")
        void deletePatientById_patientNotFound_throwsException() {
            // Arrange
            when(patientRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> patientService.deletePatientById(999L))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("School account cannot delete patient from another org — throws 403")
        void deletePatientById_wrongOrg_throwsForbidden() {
            // Arrange
            Patient patient = createMockPatient(); // org.id = 10
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(authorizationService.authorize()).thenReturn(createSchoolAuthData(99L)); // different org

            // Act & Assert
            assertThatThrownBy(() -> patientService.deletePatientById(1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.FORBIDDEN));
        }

        @Test
        @DisplayName("Null exams list is handled — deletes patient")
        void deletePatientById_nullExamsList_softDeletes() {
            // Arrange
            Patient patient = createMockPatient();
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(authorizationService.authorize()).thenReturn(createAdminAuthData());
            when(examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(1L, true))
                .thenReturn(null);

            // Act
            boolean result = patientService.deletePatientById(1L);

            // Assert
            assertThat(result).isTrue();
            verify(patientRepository).save(any(Patient.class));
        }

        @Test
        @DisplayName("School account can delete patient from same org")
        void deletePatientById_sameOrg_softDeletes() {
            // Arrange
            Patient patient = createMockPatient(); // org.id = 10
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(authorizationService.authorize()).thenReturn(createSchoolAuthData(10L));
            when(examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(1L, true))
                .thenReturn(Collections.emptyList());

            // Act
            boolean result = patientService.deletePatientById(1L);

            // Assert
            assertThat(result).isTrue();
            verify(patientRepository).save(patientCaptor.capture());
            assertThat(patientCaptor.getValue().getStatus()).isFalse();
        }

        @Test
        @DisplayName("School account — patient with null org throws 403")
        void deletePatientById_nullOrg_throwsForbidden() {
            // Arrange
            Patient patient = createMockPatient();
            patient.setOrganization(null);
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(authorizationService.authorize()).thenReturn(createSchoolAuthData(10L));

            // Act & Assert
            assertThatThrownBy(() -> patientService.deletePatientById(1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.FORBIDDEN));
        }
    }

    // ─── getPatientsByCondition() Tests ─────────────────────────────────

    @Nested
    @DisplayName("getPatientsByCondition()")
    class GetPatientsByConditionTests {

        @Test
        @DisplayName("TC-PAT-04: Data scoping — organizationId from AuthorizationService is used in query")
        void getPatientsByCondition_schoolAccount_queriesWithOrgId() {
            // Arrange
            PatientSearchCriteria criteria = new PatientSearchCriteria();
            criteria.setSearchText("Nguyen");
            Pageable pageable = PageRequest.of(0, 10);

            AuthorizationData authData = createSchoolAuthData(10L);
            when(authorizationService.authorize()).thenReturn(authData);

            Patient patient = createMockPatient();
            Page<Patient> patientPage = new PageImpl<>(List.of(patient), pageable, 1);
            when(patientRepository.findAllByCondition(
                eq("Nguyen"), eq(null), eq(10L), eq(Collections.emptyList()), eq(null), eq(true), eq(pageable)
            )).thenReturn(patientPage);

            PatientDTO dto = createMockPatientDTO();
            when(patientMapper.toDto(patient)).thenReturn(dto);

            // Act
            Page<PatientDTO> result = patientService.getPatientsByCondition(criteria, pageable);

            // Assert
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);

            // Verify the repository was called with the correct org ID from authorization
            verify(patientRepository).findAllByCondition(
                eq("Nguyen"), eq(null), eq(10L),
                eq(Collections.emptyList()), eq(null), eq(true), eq(pageable));
        }

        @Test
        @DisplayName("Admin account — queries without org restriction")
        void getPatientsByCondition_adminAccount_queriesWithoutOrgRestriction() {
            // Arrange
            PatientSearchCriteria criteria = new PatientSearchCriteria();
            Pageable pageable = PageRequest.of(0, 10);

            when(authorizationService.authorize()).thenReturn(createAdminAuthData());

            Page<Patient> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(patientRepository.findAllByCondition(
                any(), any(), eq(null), any(), any(), eq(true), eq(pageable)
            )).thenReturn(emptyPage);

            // Act
            Page<PatientDTO> result = patientService.getPatientsByCondition(criteria, pageable);

            // Assert
            assertThat(result.getTotalElements()).isZero();

            // Verify orgId is null (no restriction)
            verify(patientRepository).findAllByCondition(
                any(), any(), eq(null), any(), any(), eq(true), eq(pageable));
        }

        @Test
        @DisplayName("Area code set by authorization — overrides search criteria areaCode")
        void getPatientsByCondition_areaCodeFromAuth_overridesCriteria() {
            // Arrange
            PatientSearchCriteria criteria = new PatientSearchCriteria();
            criteria.setAreaCode("001"); // will be overridden

            AuthorizationData authData = createAdminAuthData();
            authData.setAreaCode("002");
            when(authorizationService.authorize()).thenReturn(authData);
            when(areaService.getAreaByCode("002")).thenReturn(new AreaDTO(null, "002", "Test Area", 1, null)); // non-null
            when(areaService.getChildrenAreaCode("002")).thenReturn(List.of("002", "002001"));

            Pageable pageable = PageRequest.of(0, 10);
            Page<Patient> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(patientRepository.findAllByCondition(
                any(), any(), any(), any(), any(), eq(true), eq(pageable)
            )).thenReturn(emptyPage);

            // Act
            patientService.getPatientsByCondition(criteria, pageable);

            // Assert — area code was overridden by authorization
            assertThat(criteria.getAreaCode()).isEqualTo("002");
        }

        @Test
        @DisplayName("Invalid area code — returns empty page")
        void getPatientsByCondition_invalidAreaCode_returnsEmptyPage() {
            // Arrange
            PatientSearchCriteria criteria = new PatientSearchCriteria();
            criteria.setAreaCode("INVALID");

            when(authorizationService.authorize()).thenReturn(createAdminAuthData());
            when(areaService.getAreaByCode("INVALID")).thenReturn(null);

            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<PatientDTO> result = patientService.getPatientsByCondition(criteria, pageable);

            // Assert
            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
            verify(patientRepository, never()).findAllByCondition(any(), any(), any(), any(), any(), eq(true), any());
        }

        @Test
        @DisplayName("Area code with null children — uses empty list for query")
        void getPatientsByCondition_nullChildren_usesEmptyList() {
            // Arrange
            PatientSearchCriteria criteria = new PatientSearchCriteria();
            criteria.setAreaCode("001");

            when(authorizationService.authorize()).thenReturn(createAdminAuthData());
            when(areaService.getAreaByCode("001")).thenReturn(new AreaDTO(null, "001", "Test", 1, null));
            when(areaService.getChildrenAreaCode("001")).thenReturn(null);

            Pageable pageable = PageRequest.of(0, 10);
            Page<Patient> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(patientRepository.findAllByCondition(
                any(), any(), any(), eq(Collections.emptyList()), any(), eq(true), eq(pageable)
            )).thenReturn(emptyPage);

            // Act
            Page<PatientDTO> result = patientService.getPatientsByCondition(criteria, pageable);

            // Assert
            assertThat(result.getTotalElements()).isZero();
            verify(patientRepository).findAllByCondition(
                any(), any(), any(), eq(Collections.emptyList()), any(), eq(true), eq(pageable));
        }
    }

    // ─── importPatientsFromExcel() Tests ────────────────────────────────

    @Nested
    @DisplayName("importPatientsFromExcel()")
    class ImportPatientsFromExcelTests {

        @Test
        @DisplayName("TC-PAT-05: Empty file throws HTTP 400")
        void importPatientsFromExcel_emptyFile_throwsBadRequest() {
            // Arrange
            MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(true);
            when(file.getOriginalFilename()).thenReturn("test.xlsx");

            // Act & Assert
            assertThatThrownBy(() -> patientService.importPatientsFromExcel(file))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(rse.getReason()).contains(".xlsx");
                });
        }

        @Test
        @DisplayName("TC-PAT-05: Non-.xlsx file throws HTTP 400")
        void importPatientsFromExcel_nonXlsxFile_throwsBadRequest() {
            // Arrange
            MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getOriginalFilename()).thenReturn("data.csv");

            // Act & Assert
            assertThatThrownBy(() -> patientService.importPatientsFromExcel(file))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                });
        }

        @Test
        @DisplayName("Null filename throws HTTP 400")
        void importPatientsFromExcel_nullFilename_throwsBadRequest() {
            // Arrange
            MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getOriginalFilename()).thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> patientService.importPatientsFromExcel(file))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST));
        }

        @Test
        @DisplayName("File ending in .xls (not .xlsx) throws HTTP 400")
        void importPatientsFromExcel_xlsFile_throwsBadRequest() {
            // Arrange
            MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getOriginalFilename()).thenReturn("old_format.xls");

            // Act & Assert
            assertThatThrownBy(() -> patientService.importPatientsFromExcel(file))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST));
        }
    }

    // ─── getPatientById() Tests ────────────────────────────────────────

    @Nested
    @DisplayName("getPatientById()")
    class GetPatientByIdTests {

        @Test
        @DisplayName("Happy path — returns PatientDTO for existing patient")
        void getPatientById_existingPatient_returnsDTO() {
            // Arrange
            Patient patient = createMockPatient();
            PatientDTO dto = createMockPatientDTO();

            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(authorizationService.authorize()).thenReturn(createAdminAuthData());
            when(patientMapper.toDto(patient)).thenReturn(dto);

            // Act
            PatientDTO result = patientService.getPatientById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getFullName()).isEqualTo("Nguyen Van A");
        }

        @Test
        @DisplayName("Patient not found — returns null")
        void getPatientById_notFound_returnsNull() {
            // Arrange
            when(patientRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            PatientDTO result = patientService.getPatientById(999L);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("School account cannot view patient from another org — throws 403")
        void getPatientById_wrongOrg_throwsForbidden() {
            // Arrange
            Patient patient = createMockPatient(); // org.id = 10
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(authorizationService.authorize()).thenReturn(createSchoolAuthData(99L)); // different org

            // Act & Assert
            assertThatThrownBy(() -> patientService.getPatientById(1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.FORBIDDEN));
        }

        @Test
        @DisplayName("School account can view patient from same org")
        void getPatientById_sameOrg_returnsDTO() {
            // Arrange
            Patient patient = createMockPatient(); // org.id = 10
            PatientDTO dto = createMockPatientDTO();

            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(authorizationService.authorize()).thenReturn(createSchoolAuthData(10L));
            when(patientMapper.toDto(patient)).thenReturn(dto);

            // Act
            PatientDTO result = patientService.getPatientById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getFullName()).isEqualTo("Nguyen Van A");
        }

        @Test
        @DisplayName("School account — patient with null org throws 403")
        void getPatientById_nullOrg_throwsForbidden() {
            // Arrange
            Patient patient = createMockPatient();
            patient.setOrganization(null);
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(authorizationService.authorize()).thenReturn(createSchoolAuthData(10L));

            // Act & Assert
            assertThatThrownBy(() -> patientService.getPatientById(1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.FORBIDDEN));
        }
    }

    // ─── updatePatient() Tests ─────────────────────────────────────────

    @Nested
    @DisplayName("updatePatient()")
    class UpdatePatientTests {

        @Test
        @DisplayName("Admin — updates patient successfully")
        void updatePatient_admin_savesAndReturnsDTO() {
            // Arrange
            PatientDTO inputDTO = createMockPatientDTO();
            Patient entity = createMockPatient();
            PatientDTO expectedDTO = createMockPatientDTO();

            when(authorizationService.authorize()).thenReturn(createAdminAuthData());
            when(patientMapper.toEntity(inputDTO)).thenReturn(entity);
            when(patientMapper.toDto(entity)).thenReturn(expectedDTO);

            // Act
            PatientDTO result = patientService.updatePatient(inputDTO, 1L);

            // Assert
            assertThat(result).isNotNull();
            verify(patientRepository).save(patientCaptor.capture());
            assertThat(patientCaptor.getValue().getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("School cannot update patient from another org — throws 403")
        void updatePatient_wrongOrg_throwsForbidden() {
            // Arrange
            Patient existing = createMockPatient(); // org.id = 10
            PatientDTO inputDTO = createMockPatientDTO();

            when(authorizationService.authorize()).thenReturn(createSchoolAuthData(99L));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(existing));

            // Act & Assert
            assertThatThrownBy(() -> patientService.updatePatient(inputDTO, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.FORBIDDEN));
        }

        @Test
        @DisplayName("School account can update patient from same org")
        void updatePatient_sameOrg_succeeds() {
            // Arrange
            Patient existing = createMockPatient(); // org.id = 10
            PatientDTO inputDTO = createMockPatientDTO();
            PatientDTO expectedDTO = createMockPatientDTO();

            when(authorizationService.authorize()).thenReturn(createSchoolAuthData(10L));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(patientMapper.toEntity(inputDTO)).thenReturn(existing);
            when(patientMapper.toDto(existing)).thenReturn(expectedDTO);

            // Act
            PatientDTO result = patientService.updatePatient(inputDTO, 1L);

            // Assert
            assertThat(result).isNotNull();
            verify(patientRepository).save(any(Patient.class));
        }

        @Test
        @DisplayName("School account — patient not found throws 404")
        void updatePatient_patientNotFound_throws404() {
            // Arrange
            PatientDTO inputDTO = createMockPatientDTO();

            when(authorizationService.authorize()).thenReturn(createSchoolAuthData(10L));
            when(patientRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> patientService.updatePatient(inputDTO, 999L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(rse.getReason()).contains("Không tìm thấy học sinh");
                });
        }

        @Test
        @DisplayName("School account — patient with null org throws 403")
        void updatePatient_nullOrg_throwsForbidden() {
            // Arrange
            Patient existing = createMockPatient();
            existing.setOrganization(null);
            PatientDTO inputDTO = createMockPatientDTO();

            when(authorizationService.authorize()).thenReturn(createSchoolAuthData(10L));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(existing));

            // Act & Assert
            assertThatThrownBy(() -> patientService.updatePatient(inputDTO, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.FORBIDDEN));
        }

        @Test
        @DisplayName("With chronic conditions — fetches diseases from repository")
        void updatePatient_withChronicConditions_fetchesDiseases() {
            // Arrange
            PatientDTO inputDTO = createMockPatientDTO();
            DiseaseDTO diseaseDTO = new DiseaseDTO(1L, "D001", "Diabetes");
            inputDTO.setChronicConditions(List.of(diseaseDTO));

            Patient entity = createMockPatient();
            PatientDTO expectedDTO = createMockPatientDTO();

            Disease disease = new Disease();
            disease.setId(1L);
            disease.setCode("D001");
            disease.setName("Diabetes");

            when(authorizationService.authorize()).thenReturn(createAdminAuthData());
            when(patientMapper.toEntity(inputDTO)).thenReturn(entity);
            when(diseaseRepository.findAllById(List.of(1L))).thenReturn(List.of(disease));
            when(patientMapper.toDto(entity)).thenReturn(expectedDTO);

            // Act
            PatientDTO result = patientService.updatePatient(inputDTO, 1L);

            // Assert
            assertThat(result).isNotNull();
            verify(diseaseRepository).findAllById(List.of(1L));
            verify(patientRepository).save(patientCaptor.capture());
            assertThat(patientCaptor.getValue().getChronicConditions()).containsExactly(disease);
        }

        @Test
        @DisplayName("With empty chronic conditions — sets null chronicConditions")
        void updatePatient_emptyChronicConditions_setsNull() {
            // Arrange
            PatientDTO inputDTO = createMockPatientDTO();
            inputDTO.setChronicConditions(Collections.emptyList());

            Patient entity = createMockPatient();
            PatientDTO expectedDTO = createMockPatientDTO();

            when(authorizationService.authorize()).thenReturn(createAdminAuthData());
            when(patientMapper.toEntity(inputDTO)).thenReturn(entity);
            when(patientMapper.toDto(entity)).thenReturn(expectedDTO);

            // Act
            patientService.updatePatient(inputDTO, 1L);

            // Assert — diseaseRepository not called, chronicConditions set to null
            verify(diseaseRepository, never()).findAllById(any());
            verify(patientRepository).save(patientCaptor.capture());
            assertThat(patientCaptor.getValue().getChronicConditions()).isNull();
        }
    }

    // ─── getAllPatients() Tests ─────────────────────────────────────────

    @Nested
    @DisplayName("getAllPatients()")
    class GetAllPatientsTests {

        @Test
        @DisplayName("Admin sees all patients")
        void getAllPatients_admin_returnAllPatients() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(authorizationService.authorize()).thenReturn(createAdminAuthData());

            Patient patient = createMockPatient();
            Page<Patient> page = new PageImpl<>(List.of(patient), pageable, 1);
            when(patientRepository.findAll(pageable)).thenReturn(page);
            when(patientMapper.toDto(patient)).thenReturn(createMockPatientDTO());

            // Act
            Page<PatientDTO> result = patientService.getAllPatients(pageable);

            // Assert
            assertThat(result.getTotalElements()).isEqualTo(1);
            verify(patientRepository).findAll(pageable);
        }

        @Test
        @DisplayName("School account only sees own org patients")
        void getAllPatients_school_queriesWithOrgId() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(authorizationService.authorize()).thenReturn(createSchoolAuthData(10L));

            Page<Patient> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(patientRepository.findAllByCondition(
                eq(null), eq(null), eq(10L), eq(Collections.emptyList()), eq(null), eq(true), eq(pageable)
            )).thenReturn(page);

            // Act
            Page<PatientDTO> result = patientService.getAllPatients(pageable);

            // Assert
            assertThat(result.getTotalElements()).isZero();
            verify(patientRepository).findAllByCondition(
                eq(null), eq(null), eq(10L), eq(Collections.emptyList()), eq(null), eq(true), eq(pageable));
            verify(patientRepository, never()).findAll(pageable);
        }
    }

    // ─── getPatientsByConditionCacheKey() Tests ────────────────────────

    @Nested
    @DisplayName("getPatientsByConditionCacheKey()")
    class GetPatientsByConditionCacheKeyTests {

        @Test
        @DisplayName("Admin account — cache key starts with 'all'")
        void getPatientsByConditionCacheKey_admin_startsAll() {
            // Arrange
            PatientSearchCriteria criteria = new PatientSearchCriteria();
            criteria.setSearchText("Nguyen");
            criteria.setOrganizationName("School A");
            criteria.setSchoolClass("1A");
            criteria.setAreaCode("001");

            Pageable pageable = PageRequest.of(0, 10);
            when(authorizationService.authorize()).thenReturn(createAdminAuthData());

            // Act
            String key = patientService.getPatientsByConditionCacheKey(criteria, pageable);

            // Assert
            assertThat(key).startsWith("all|");
            assertThat(key).contains("Nguyen");
            assertThat(key).contains("School A");
            assertThat(key).contains("1A");
            assertThat(key).contains("001");
        }

        @Test
        @DisplayName("School account — cache key starts with 'org-{id}'")
        void getPatientsByConditionCacheKey_school_startsWithOrgId() {
            // Arrange
            PatientSearchCriteria criteria = new PatientSearchCriteria();
            criteria.setSearchText("");
            Pageable pageable = PageRequest.of(0, 20);

            when(authorizationService.authorize()).thenReturn(createSchoolAuthData(10L));

            // Act
            String key = patientService.getPatientsByConditionCacheKey(criteria, pageable);

            // Assert
            assertThat(key).startsWith("org-10|");
        }
    }

    // ─── generateExcelTemplateFile() Tests ─────────────────────────────

    @Nested
    @DisplayName("generateExcelTemplateFile()")
    class GenerateExcelTemplateFileTests {

        @Test
        @DisplayName("Happy path — returns Excel byte array with correct headers")
        void generateExcelTemplateFile_success_returnsBytes() throws Exception {
            // Arrange
            HttpServletResponse response = mock(HttpServletResponse.class);

            // Act
            byte[] result = patientService.generateExcelTemplateFile(response);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.length).isGreaterThan(0);
            verify(response).setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            verify(response).setHeader(eq(HttpHeaders.CONTENT_DISPOSITION), eq("attachment; filename=Import_Hocsinh.xlsx"));
        }
    }

    // ─── exportPatients() Tests ────────────────────────────────────────

    @Nested
    @DisplayName("exportPatients()")
    class ExportPatientsTests {

        @Test
        @DisplayName("Happy path — returns Excel byte array with patient data")
        void exportPatients_success_returnsBytes() throws Exception {
            // Arrange
            HttpServletResponse response = mock(HttpServletResponse.class);

            // Act
            byte[] result = patientService.exportPatients(response);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.length).isGreaterThan(0);
            verify(response).setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            verify(response).setHeader(eq(HttpHeaders.CONTENT_DISPOSITION), eq("attachment; filename=Export_Hocsinh.xlsx"));
        }
    }
}
