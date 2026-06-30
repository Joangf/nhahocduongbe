package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import static java.util.Objects.nonNull;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthorizationService;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.model.response.ResponseModel;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.model.response.UpsertResponseModel;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.service.AreaService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.ResponseMessage;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.OrganizationSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.helper.OrganizationHelper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.OrganizationMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.OrganizationRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.OrganizationService;

@Service
public class OrganizationServiceImpl implements OrganizationService {

  @Autowired private OrganizationRepository organizationRepository;
  @Autowired private PatientRepository patientRepository;

  @Autowired private OrganizationMapper organizationMapper;
  @PersistenceContext private EntityManager entityManager;

  @Autowired private AreaService areaService;

  @Autowired private AuthorizationService authorizationService;

  @Autowired private OrganizationHelper organizationHelper;

  public OrganizationDTO getOrganizationById(Long id) {
    Organization organization = organizationRepository.findById(id).orElse(null);
    return organizationMapper.toDto(organization);
  }

  @Transactional
  public OrganizationDTO createOrganization(OrganizationDTO organizationDTO) {
    // Check duplicate class
    List<String> duplicateClasses = organizationHelper.getDuplicateClassList(organizationDTO);
    if (nonNull(duplicateClasses) && !duplicateClasses.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          ResponseMessage.ORGANIZATION_DUPLICATE_CLASS + String.join(", ", duplicateClasses));
    }

    var entity = organizationMapper.toEntity(organizationDTO);

    entity.setCode(organizationHelper.generateCode(organizationDTO));

    organizationRepository.saveAndFlush(entity);
    entityManager.refresh(entity);

    return organizationMapper.toDto(entity);
  }

  @Transactional
  public OrganizationDTO updateOrganization(OrganizationDTO organizationDTO, Long id) {
    var entity =
        organizationRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ResponseMessage.ORGANIZATION_NOT_FOUND_WITH_ID + id));

    // Check duplicate class
    List<String> duplicateClasses = organizationHelper.getDuplicateClassList(organizationDTO);
    if (nonNull(duplicateClasses) && !duplicateClasses.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          ResponseMessage.ORGANIZATION_DUPLICATE_CLASS + String.join(", ", duplicateClasses));
    }

    var updatedEntity = organizationMapper.partialUpdate(organizationDTO, entity);
    organizationRepository.save(updatedEntity);

    return organizationMapper.toDto(updatedEntity);
  }

  @Transactional
  public boolean delete(Long id) {
    List<Patient> patientList = patientRepository.findAllByOrganization_Id(id);
    if (nonNull(patientList) && patientList.size() > 0) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, ResponseMessage.ORGANIZATION_CANT_DELETE_HAS_STUDENT);
    }
    Organization organization = organizationRepository.findById(id).orElse(null);
    if (organization != null) {
      organization.setStatus(false);
      organizationRepository.save(organization);
      return true;
    }

    return false;
  }

  public List<OrganizationDTO> getByCondition(String name) {
    return organizationMapper.toDtoList(organizationRepository.findByNameIsLikeOrderByName(name));
  }

  public List<OrganizationDTO> getByAreaCode(String areaCode) {
    if (areaService.getAreaByCode(areaCode) == null) {
      return Collections.emptyList();
    }
    List<String> areaCodesInside = areaService.getChildrenAreaCode(areaCode);
    return organizationMapper.toDtoList(organizationRepository.findByAreaCodeIn(areaCodesInside));
  }

  public List<OrganizationDTO> getAll() {
    return organizationMapper.toDtoList(organizationRepository.findAllByOrderByName());
  }

  public Page<OrganizationDTO> search(
      OrganizationSearchCriteria searchCriteria, Pageable pageable) {
    Long organizationId = null;
    try {
      AuthorizationService.AuthorizationData authData = authorizationService.authorize();
      if (authData.getAreaCode() != null) {
        searchCriteria.setAreaCode(authData.getAreaCode());
      }
      organizationId = authData.getOrganizationId();
    } catch (Exception e) {
      // Không có auth (public page) — trả về tất cả trường không giới hạn
    }

    if (searchCriteria.getAreaCode() != null) {
      if (areaService.getAreaByCode(searchCriteria.getAreaCode()) == null) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
      }
    }

    List<String> areaCodesInside = areaService.getChildrenAreaCode(searchCriteria.getAreaCode());
    Page<Organization> organizations =
        organizationRepository.findByCriteria(
            areaCodesInside, searchCriteria, organizationId, pageable);

    return organizations.map(organizationMapper::toDto);
  }

  @Override
  public OrganizationDTO getOrganizationByCode(String code) {
    return organizationMapper.toDto(organizationRepository.findByCode(code));
  }

  @Override
  public UpsertResponseModel checkDeletableClasses(Long organizationId, List<String> classes) {
    List<Patient> patientList = patientRepository.findAllByOrganization_Id(organizationId);

    // Only allow to delete classes not have any student
    List<String> classesHaveStudent =
        classes.stream()
            .filter(
                clazz ->
                    patientList.stream()
                        .anyMatch(patient -> patient.getSchoolClass().equals(clazz)))
            .toList();
    List<String> classesDontHaveStudent = new ArrayList<>(classes);
    classesDontHaveStudent.removeAll(classesHaveStudent);

    // Map to response model
    List<ResponseModel> notAllowList =
        classesHaveStudent.stream()
            .map(
                clazz ->
                    ResponseModel.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(ResponseMessage.ORGANIZATION_CANT_DELETE_CLASS_HAS_STUDENT)
                        .content(clazz)
                        .build())
            .toList();
    List<ResponseModel> allowList =
        classesDontHaveStudent.stream()
            .map(
                clazz ->
                    ResponseModel.builder()
                        .status(HttpStatus.ACCEPTED.value())
                        .message(HttpStatus.ACCEPTED.getReasonPhrase())
                        .content(clazz)
                        .build())
            .toList();

    return UpsertResponseModel.builder()
        .successList(allowList)
        .successCount(allowList.size())
        .errorList(notAllowList)
        .errorCount(notAllowList.size())
        .build();
  }
}
