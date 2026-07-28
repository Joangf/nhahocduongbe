package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthorizationService;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthorizationService.AuthorizationData;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.model.response.UpsertResponseModel;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.service.AreaService;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.dto.AreaDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.ResponseMessage;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.OrganizationSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.helper.OrganizationHelper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.OrganizationMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.OrganizationRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationServiceImpl Unit Tests")
class OrganizationServiceImplTest {

    @Mock private OrganizationRepository organizationRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private OrganizationMapper organizationMapper;
    @Mock private EntityManager entityManager;
    @Mock private AreaService areaService;
    @Mock private AuthorizationService authorizationService;
    @Mock private OrganizationHelper organizationHelper;

    @InjectMocks private OrganizationServiceImpl organizationService;

    @Captor private ArgumentCaptor<Organization> orgCaptor;

    // ─── Helper methods ────────────────────────────────────────────────

    private Organization createMockOrganization() {
        Organization org = new Organization();
        org.setId(1L);
        org.setName("Truong Tieu Hoc ABC");
        org.setCode("001001");
        org.setAddress("123 Le Loi, Hanoi");
        org.setAreaCode("001");
        org.setStatus(true);
        return org;
    }

    private OrganizationDTO createMockOrganizationDTO() {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(1L);
        dto.setName("Truong Tieu Hoc ABC");
        dto.setCode("001001");
        dto.setAddress("123 Le Loi, Hanoi");
        dto.setAreaCode("001");
        dto.setStatus(true);
        return dto;
    }

    private Patient createPatientInClass(String schoolClass) {
        Patient patient = new Patient();
        patient.setId(100L);
        patient.setFullName("Test Student");
        patient.setSchoolClass(schoolClass);
        patient.setStatus(true);
        return patient;
    }

    // ─── createOrganization() Tests ────────────────────────────────────

    @Nested
    @DisplayName("createOrganization()")
    class CreateOrganizationTests {

        @Test
        @DisplayName("Happy path — creates org with valid class data, generates code, returns DTO")
        void createOrganization_happyPath_savesAndReturnsDTO() {
            // Arrange
            OrganizationDTO inputDTO = createMockOrganizationDTO();
            Organization entity = createMockOrganization();
            OrganizationDTO expectedDTO = createMockOrganizationDTO();

            when(organizationHelper.getDuplicateClassList(inputDTO)).thenReturn(Collections.emptyList());
            when(organizationMapper.toEntity(inputDTO)).thenReturn(entity);
            when(organizationHelper.generateCode(inputDTO)).thenReturn("001001");
            when(organizationMapper.toDto(entity)).thenReturn(expectedDTO);

            // Act
            OrganizationDTO result = organizationService.createOrganization(inputDTO);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo("001001");
            assertThat(result.getName()).isEqualTo("Truong Tieu Hoc ABC");

            verify(organizationHelper).generateCode(inputDTO);
            verify(organizationRepository).saveAndFlush(orgCaptor.capture());
            assertThat(orgCaptor.getValue().getCode()).isEqualTo("001001");
            verify(entityManager).refresh(entity);
        }

        @Test
        @DisplayName("Duplicate classes — throws HTTP 400 BAD_REQUEST")
        void createOrganization_duplicateClasses_throwsBadRequest() {
            // Arrange
            OrganizationDTO inputDTO = createMockOrganizationDTO();
            when(organizationHelper.getDuplicateClassList(inputDTO))
                .thenReturn(List.of("1A", "2B"));

            // Act & Assert
            assertThatThrownBy(() -> organizationService.createOrganization(inputDTO))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(rse.getReason())
                        .startsWith(ResponseMessage.ORGANIZATION_DUPLICATE_CLASS)
                        .contains("1A")
                        .contains("2B");
                });

            verify(organizationRepository, never()).saveAndFlush(any());
        }

        @Test
        @DisplayName("Null duplicate list — proceeds normally (no duplicates)")
        void createOrganization_nullDuplicateList_savesSuccessfully() {
            // Arrange
            OrganizationDTO inputDTO = createMockOrganizationDTO();
            Organization entity = createMockOrganization();

            when(organizationHelper.getDuplicateClassList(inputDTO)).thenReturn(null);
            when(organizationMapper.toEntity(inputDTO)).thenReturn(entity);
            when(organizationHelper.generateCode(inputDTO)).thenReturn("001001");
            when(organizationMapper.toDto(entity)).thenReturn(createMockOrganizationDTO());

            // Act
            OrganizationDTO result = organizationService.createOrganization(inputDTO);

            // Assert
            assertThat(result).isNotNull();
            verify(organizationRepository).saveAndFlush(any());
        }
    }

    // ─── updateOrganization() Tests ────────────────────────────────────

    @Nested
    @DisplayName("updateOrganization()")
    class UpdateOrganizationTests {

        @Test
        @DisplayName("Happy path — finds existing org, partial updates, returns DTO")
        void updateOrganization_happyPath_updatesAndReturnsDTO() {
            // Arrange
            OrganizationDTO inputDTO = createMockOrganizationDTO();
            inputDTO.setName("Updated Name");
            Organization existing = createMockOrganization();
            Organization updated = createMockOrganization();
            updated.setName("Updated Name");
            OrganizationDTO expectedDTO = createMockOrganizationDTO();
            expectedDTO.setName("Updated Name");

            when(organizationRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(organizationHelper.getDuplicateClassList(inputDTO)).thenReturn(Collections.emptyList());
            when(organizationMapper.partialUpdate(inputDTO, existing)).thenReturn(updated);
            when(organizationMapper.toDto(updated)).thenReturn(expectedDTO);

            // Act
            OrganizationDTO result = organizationService.updateOrganization(inputDTO, 1L);

            // Assert
            assertThat(result.getName()).isEqualTo("Updated Name");
            verify(organizationRepository).save(updated);
        }

        @Test
        @DisplayName("Duplicate classes on update — throws HTTP 400")
        void updateOrganization_duplicateClasses_throwsBadRequest() {
            // Arrange
            OrganizationDTO inputDTO = createMockOrganizationDTO();
            Organization existing = createMockOrganization();

            when(organizationRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(organizationHelper.getDuplicateClassList(inputDTO))
                .thenReturn(List.of("3A"));

            // Act & Assert
            assertThatThrownBy(() -> organizationService.updateOrganization(inputDTO, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(rse.getReason()).contains("3A");
                });

            verify(organizationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Org not found — throws HTTP 404")
        void updateOrganization_notFound_throws404() {
            // Arrange
            when(organizationRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> organizationService.updateOrganization(createMockOrganizationDTO(), 999L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(rse.getReason()).contains(ResponseMessage.ORGANIZATION_NOT_FOUND_WITH_ID);
                });
        }
    }

    // ─── delete() Tests ────────────────────────────────────────────────

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("Delete prevented — throws HTTP 400 when org has active students")
        void delete_hasStudents_throwsBadRequest() {
            // Arrange
            Patient student = createPatientInClass("1A");
            when(patientRepository.findAllByOrganization_Id(1L)).thenReturn(List.of(student));

            // Act & Assert
            assertThatThrownBy(() -> organizationService.delete(1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(rse.getReason()).isEqualTo(ResponseMessage.ORGANIZATION_CANT_DELETE_HAS_STUDENT);
                });

            verify(organizationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Happy path — soft-deletes org when no students")
        void delete_noStudents_softDeletes() {
            // Arrange
            Organization org = createMockOrganization();
            when(patientRepository.findAllByOrganization_Id(1L)).thenReturn(Collections.emptyList());
            when(organizationRepository.findById(1L)).thenReturn(Optional.of(org));

            // Act
            boolean result = organizationService.delete(1L);

            // Assert
            assertThat(result).isTrue();
            verify(organizationRepository).save(orgCaptor.capture());
            assertThat(orgCaptor.getValue().getStatus()).isFalse();
        }

        @Test
        @DisplayName("Null patient list — soft-deletes org")
        void delete_nullPatientList_softDeletes() {
            // Arrange
            Organization org = createMockOrganization();
            when(patientRepository.findAllByOrganization_Id(1L)).thenReturn(null);
            when(organizationRepository.findById(1L)).thenReturn(Optional.of(org));

            // Act
            boolean result = organizationService.delete(1L);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Org not found — returns false")
        void delete_orgNotFound_returnsFalse() {
            // Arrange
            when(patientRepository.findAllByOrganization_Id(999L)).thenReturn(Collections.emptyList());
            when(organizationRepository.findById(999L)).thenReturn(Optional.empty());

            // Act
            boolean result = organizationService.delete(999L);

            // Assert
            assertThat(result).isFalse();
            verify(organizationRepository, never()).save(any());
        }
    }

    // ─── checkDeletableClasses() Tests ─────────────────────────────────

    @Nested
    @DisplayName("checkDeletableClasses()")
    class CheckDeletableClassesTests {

        @Test
        @DisplayName("All classes safe to remove — no students in any class")
        void checkDeletableClasses_noStudents_allAccepted() {
            // Arrange
            when(patientRepository.findAllByOrganization_Id(1L)).thenReturn(Collections.emptyList());

            // Act
            UpsertResponseModel result = organizationService.checkDeletableClasses(1L, List.of("1A", "2B"));

            // Assert
            assertThat(result.getSuccessCount()).isEqualTo(2);
            assertThat(result.getErrorCount()).isZero();
            assertThat(result.getSuccessList()).hasSize(2);
            assertThat(result.getErrorList()).isEmpty();
            assertThat(result.getSuccessList().get(0).getContent()).isEqualTo("1A");
            assertThat(result.getSuccessList().get(0).getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());
        }

        @Test
        @DisplayName("Some classes have students — mixed result")
        void checkDeletableClasses_someHaveStudents_mixedResult() {
            // Arrange
            Patient studentIn1A = createPatientInClass("1A");
            Patient studentIn2B = createPatientInClass("2B");
            when(patientRepository.findAllByOrganization_Id(1L))
                .thenReturn(List.of(studentIn1A, studentIn2B));

            // Act
            UpsertResponseModel result = organizationService.checkDeletableClasses(
                1L, List.of("1A", "2B", "3C"));

            // Assert
            // 1A and 2B have students → error; 3C is safe → success
            assertThat(result.getErrorCount()).isEqualTo(2);
            assertThat(result.getSuccessCount()).isEqualTo(1);

            assertThat(result.getErrorList()).hasSize(2);
            assertThat(result.getErrorList().get(0).getContent()).isEqualTo("1A");
            assertThat(result.getErrorList().get(0).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(result.getErrorList().get(0).getMessage())
                .isEqualTo(ResponseMessage.ORGANIZATION_CANT_DELETE_CLASS_HAS_STUDENT);

            assertThat(result.getSuccessList()).hasSize(1);
            assertThat(result.getSuccessList().get(0).getContent()).isEqualTo("3C");
        }

        @Test
        @DisplayName("All classes have students — all rejected")
        void checkDeletableClasses_allHaveStudents_allRejected() {
            // Arrange
            Patient studentIn1A = createPatientInClass("1A");
            when(patientRepository.findAllByOrganization_Id(1L))
                .thenReturn(List.of(studentIn1A));

            // Act
            UpsertResponseModel result = organizationService.checkDeletableClasses(1L, List.of("1A"));

            // Assert
            assertThat(result.getErrorCount()).isEqualTo(1);
            assertThat(result.getSuccessCount()).isZero();
        }
    }

    // ─── getOrganizationById() Tests ───────────────────────────────────

    @Nested
    @DisplayName("getOrganizationById()")
    class GetByIdTests {

        @Test
        @DisplayName("Happy path — returns DTO")
        void getOrganizationById_exists_returnsDTO() {
            // Arrange
            Organization org = createMockOrganization();
            OrganizationDTO dto = createMockOrganizationDTO();
            when(organizationRepository.findById(1L)).thenReturn(Optional.of(org));
            when(organizationMapper.toDto(org)).thenReturn(dto);

            // Act
            OrganizationDTO result = organizationService.getOrganizationById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Truong Tieu Hoc ABC");
        }

        @Test
        @DisplayName("Not found — returns null (mapper.toDto(null))")
        void getOrganizationById_notFound_returnsNull() {
            // Arrange
            when(organizationRepository.findById(999L)).thenReturn(Optional.empty());
            when(organizationMapper.toDto((Organization) null)).thenReturn(null);

            // Act
            OrganizationDTO result = organizationService.getOrganizationById(999L);

            // Assert
            assertThat(result).isNull();
        }
    }

    // ─── getOrganizationByCode() Tests ─────────────────────────────────

    @Nested
    @DisplayName("getOrganizationByCode()")
    class GetByCodeTests {

        @Test
        @DisplayName("Returns DTO for existing code")
        void getOrganizationByCode_exists_returnsDTO() {
            // Arrange
            Organization org = createMockOrganization();
            OrganizationDTO dto = createMockOrganizationDTO();
            when(organizationRepository.findByCode("001001")).thenReturn(org);
            when(organizationMapper.toDto(org)).thenReturn(dto);

            // Act
            OrganizationDTO result = organizationService.getOrganizationByCode("001001");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo("001001");
        }
    }

    // ─── getAll() Tests ────────────────────────────────────────────────

    @Nested
    @DisplayName("getAll()")
    class GetAllTests {

        @Test
        @DisplayName("Returns all organizations ordered by name")
        void getAll_returnsList() {
            // Arrange
            Organization org = createMockOrganization();
            OrganizationDTO dto = createMockOrganizationDTO();
            when(organizationRepository.findAllByOrderByName()).thenReturn(List.of(org));
            when(organizationMapper.toDtoList(List.of(org))).thenReturn(List.of(dto));

            // Act
            List<OrganizationDTO> result = organizationService.getAll();

            // Assert
            assertThat(result).hasSize(1);
        }
    }

    // ─── getByAreaCode() Tests ─────────────────────────────────────────

    @Nested
    @DisplayName("getByAreaCode()")
    class GetByAreaCodeTests {

        @Test
        @DisplayName("Invalid area code — returns empty list")
        void getByAreaCode_invalidCode_returnsEmpty() {
            // Arrange
            when(areaService.getAreaByCode("INVALID")).thenReturn(null);

            // Act
            List<OrganizationDTO> result = organizationService.getByAreaCode("INVALID");

            // Assert
            assertThat(result).isEmpty();
            verify(organizationRepository, never()).findByAreaCodeIn(any());
        }

        @Test
        @DisplayName("Valid area code — returns matching organizations")
        void getByAreaCode_validCode_returnsOrgs() {
            // Arrange
            when(areaService.getAreaByCode("001")).thenReturn(new AreaDTO(null, "001", "Area 001", 1, null));
            when(areaService.getChildrenAreaCode("001")).thenReturn(List.of("001", "001001"));

            Organization org = createMockOrganization();
            when(organizationRepository.findByAreaCodeIn(List.of("001", "001001")))
                .thenReturn(List.of(org));
            when(organizationMapper.toDtoList(List.of(org)))
                .thenReturn(List.of(createMockOrganizationDTO()));

            // Act
            List<OrganizationDTO> result = organizationService.getByAreaCode("001");

            // Assert
            assertThat(result).hasSize(1);
        }
    }

    // ─── getByCondition() Tests ────────────────────────────────────────

    @Nested
    @DisplayName("getByCondition()")
    class GetByConditionTests {

        @Test
        @DisplayName("Returns matching organizations by name")
        void getByCondition_matchingName_returnsList() {
            // Arrange
            Organization org = createMockOrganization();
            OrganizationDTO dto = createMockOrganizationDTO();
            when(organizationRepository.findByNameIsLikeOrderByName("Truong"))
                .thenReturn(List.of(org));
            when(organizationMapper.toDtoList(List.of(org)))
                .thenReturn(List.of(dto));

            // Act
            List<OrganizationDTO> result = organizationService.getByCondition("Truong");

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Truong Tieu Hoc ABC");
            verify(organizationRepository).findByNameIsLikeOrderByName("Truong");
        }

        @Test
        @DisplayName("No match — returns empty list")
        void getByCondition_noMatch_returnsEmptyList() {
            // Arrange
            when(organizationRepository.findByNameIsLikeOrderByName("NonExistent"))
                .thenReturn(Collections.emptyList());
            when(organizationMapper.toDtoList(Collections.emptyList()))
                .thenReturn(Collections.emptyList());

            // Act
            List<OrganizationDTO> result = organizationService.getByCondition("NonExistent");

            // Assert
            assertThat(result).isEmpty();
        }
    }

    // ─── search() Tests ────────────────────────────────────────────────

    @Nested
    @DisplayName("search()")
    class SearchTests {

        @Test
        @DisplayName("Admin account — searches without org restriction")
        void search_admin_returnsAllMatching() {
            // Arrange
            OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
            criteria.setSearchText("Truong");
            Pageable pageable = PageRequest.of(0, 10);

            AuthorizationData authData = new AuthorizationData();
            authData.setOrganizationId(null);
            authData.setAreaCode(null);
            when(authorizationService.authorize()).thenReturn(authData);

            when(areaService.getChildrenAreaCode(null)).thenReturn(Collections.emptyList());

            Organization org = createMockOrganization();
            Page<Organization> orgPage = new PageImpl<>(List.of(org), pageable, 1);
            when(organizationRepository.findByCriteria(
                eq(Collections.emptyList()), eq(criteria), eq(null), eq(pageable)
            )).thenReturn(orgPage);

            OrganizationDTO dto = createMockOrganizationDTO();
            when(organizationMapper.toDto(org)).thenReturn(dto);

            // Act
            Page<OrganizationDTO> result = organizationService.search(criteria, pageable);

            // Assert
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Truong Tieu Hoc ABC");
            verify(organizationRepository).findByCriteria(
                eq(Collections.emptyList()), eq(criteria), eq(null), eq(pageable));
        }

        @Test
        @DisplayName("School account — restricts by organizationId")
        void search_school_restrictedByOrgId() {
            // Arrange
            OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
            Pageable pageable = PageRequest.of(0, 10);

            AuthorizationData authData = new AuthorizationData();
            authData.setOrganizationId(10L);
            authData.setAreaCode(null);
            when(authorizationService.authorize()).thenReturn(authData);

            when(areaService.getChildrenAreaCode(null)).thenReturn(Collections.emptyList());

            Page<Organization> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(organizationRepository.findByCriteria(
                any(), any(), eq(10L), eq(pageable)
            )).thenReturn(emptyPage);

            // Act
            Page<OrganizationDTO> result = organizationService.search(criteria, pageable);

            // Assert
            assertThat(result.getTotalElements()).isZero();
            verify(organizationRepository).findByCriteria(
                any(), any(), eq(10L), eq(pageable));
        }

        @Test
        @DisplayName("Area code from auth — overrides search criteria")
        void search_areaCodeFromAuth_overridesCriteria() {
            // Arrange
            OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
            criteria.setAreaCode("001");
            Pageable pageable = PageRequest.of(0, 10);

            AuthorizationData authData = new AuthorizationData();
            authData.setOrganizationId(null);
            authData.setAreaCode("002");
            when(authorizationService.authorize()).thenReturn(authData);

            when(areaService.getAreaByCode("002")).thenReturn(new AreaDTO(null, "002", "Area", 1, null));
            when(areaService.getChildrenAreaCode("002")).thenReturn(List.of("002"));

            Page<Organization> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(organizationRepository.findByCriteria(
                eq(List.of("002")), eq(criteria), eq(null), eq(pageable)
            )).thenReturn(emptyPage);

            // Act
            organizationService.search(criteria, pageable);

            // Assert — area code was overridden by authorization
            assertThat(criteria.getAreaCode()).isEqualTo("002");
        }

        @Test
        @DisplayName("Invalid area code — returns empty page")
        void search_invalidAreaCode_returnsEmptyPage() {
            // Arrange
            OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
            criteria.setAreaCode("INVALID");
            Pageable pageable = PageRequest.of(0, 10);

            AuthorizationData authData = new AuthorizationData();
            authData.setOrganizationId(null);
            authData.setAreaCode(null);
            when(authorizationService.authorize()).thenReturn(authData);

            when(areaService.getAreaByCode("INVALID")).thenReturn(null);

            // Act
            Page<OrganizationDTO> result = organizationService.search(criteria, pageable);

            // Assert
            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
            verify(organizationRepository, never()).findByCriteria(any(), any(), any(), any());
        }

        @Test
        @DisplayName("Authorization throws exception — treats as public page (no auth restriction)")
        void search_authThrows_treatsAsPublicPage() {
            // Arrange
            OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
            Pageable pageable = PageRequest.of(0, 10);

            when(authorizationService.authorize()).thenThrow(new RuntimeException("No auth"));

            when(areaService.getChildrenAreaCode(null)).thenReturn(Collections.emptyList());

            Organization org = createMockOrganization();
            Page<Organization> orgPage = new PageImpl<>(List.of(org), pageable, 1);
            when(organizationRepository.findByCriteria(
                eq(Collections.emptyList()), eq(criteria), eq(null), eq(pageable)
            )).thenReturn(orgPage);

            OrganizationDTO dto = createMockOrganizationDTO();
            when(organizationMapper.toDto(org)).thenReturn(dto);

            // Act
            Page<OrganizationDTO> result = organizationService.search(criteria, pageable);

            // Assert — should still work, no org restriction
            assertThat(result.getTotalElements()).isEqualTo(1);
            verify(organizationRepository).findByCriteria(
                eq(Collections.emptyList()), eq(criteria), eq(null), eq(pageable));
        }

        @Test
        @DisplayName("Null area code — queries with children codes from areaService")
        void search_nullAreaCode_queriesWithChildrenCodes() {
            // Arrange
            OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
            // areaCode is null by default
            Pageable pageable = PageRequest.of(0, 10);

            AuthorizationData authData = new AuthorizationData();
            authData.setOrganizationId(null);
            authData.setAreaCode(null);
            when(authorizationService.authorize()).thenReturn(authData);

            when(areaService.getChildrenAreaCode(null)).thenReturn(Collections.emptyList());

            Page<Organization> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(organizationRepository.findByCriteria(
                eq(Collections.emptyList()), eq(criteria), eq(null), eq(pageable)
            )).thenReturn(emptyPage);

            // Act
            Page<OrganizationDTO> result = organizationService.search(criteria, pageable);

            // Assert
            assertThat(result.getTotalElements()).isZero();
        }
    }
}
