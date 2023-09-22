package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthorizationService;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.service.AreaService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.criteria.OrganizationSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.OrganizationMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.OrganizationRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

@Service
public class OrganizationService {

  @Autowired
  private OrganizationRepository organizationRepository;
  @Autowired
  private PatientRepository patientRepository;
  @Autowired
  private OrganizationMapper organizationMapper;
  @PersistenceContext EntityManager entityManager;

  @Autowired private AreaService areaService;

  @Autowired private AuthorizationService authorizationService;

  public OrganizationDTO getOrganizationById(Long id) {
    Organization organization = organizationRepository.findById(id).orElse(null);
    return organizationMapper.toDto(organization);
  }

  @Transactional
  public OrganizationDTO createOrganization(OrganizationDTO organizationDTO) {
    var entity = organizationMapper.toEntity(organizationDTO);

    String orgCode = generateOrgCode(organizationDTO);
    entity.setCode(orgCode);

    organizationRepository.saveAndFlush(entity);
    entityManager.refresh(entity);

    return organizationMapper.toDto(entity);
  }

  @Transactional
  public OrganizationDTO updateOrganization(OrganizationDTO organizationDTO, Long id) {
    var entity = organizationRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Not found organization with ID " + id
    ));

    var updatedEntity = organizationMapper.partialUpdate(organizationDTO, entity);
    organizationRepository.save(updatedEntity);

    return organizationMapper.toDto(updatedEntity);
  }

  public boolean delete(Long id){
    List<Patient> patientList = patientRepository.findAllByOrganization_Id(id);
    if(patientList.size() > 0){
      return false;
    }
    Organization organization = organizationRepository.findById(id).orElse(null);
    if(organization != null){
      organizationRepository.delete(organization);
      return true;
    }

    return false;
  }

  public List<OrganizationDTO> getByCondition(String name){

    return organizationMapper.toDtoList(organizationRepository.findByNameIsLikeOrderByName(name));
  }

  public List<OrganizationDTO> getByAreaCode(String areaCode) {
    if (areaService.getAreaByCode(areaCode) == null) {
      return Collections.emptyList();
    }
    List<String> areaCodesInside = areaService.getChildrenAreaCode(areaCode);
    return organizationMapper.toDtoList(organizationRepository.findByAreaCodeIn(areaCodesInside));
  }

  public List<OrganizationDTO> getAll(){

    return organizationMapper.toDtoList(organizationRepository.findAllByOrderByName());
  }

  public Page<OrganizationDTO> search(OrganizationSearchCriteria searchCriteria, Pageable pageable) {
    AuthorizationService.AuthorizationData authData = authorizationService.authorize();
    if (authData.getAreaCode() != null) {
      searchCriteria.setAreaCode(authData.getAreaCode());
    }

    if (searchCriteria.getAreaCode() != null) {
      if (areaService.getAreaByCode(searchCriteria.getAreaCode()) == null) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
      }
    }

    List<String> areaCodesInside = areaService.getChildrenAreaCode(searchCriteria.getAreaCode());
    Page<Organization> organizations = organizationRepository.findByCriteria(areaCodesInside,
                                                                             searchCriteria,
                                                                             authData.getOrganizationId(),
                                                                             pageable);

    return organizations.map(organizationMapper::toDto);
  }

  private String generateOrgCode(OrganizationDTO organizationDTO) {
    StringBuilder codeBuilder = new StringBuilder();
    codeBuilder.append(String.format("%03d", Integer.parseInt(organizationDTO.getAreaCode())));

    // Get latest org code and increase 1, if not exist start with xxx001
    Organization organization = organizationRepository.findFirstByAreaCodeOrderByCodeDesc(organizationDTO.getAreaCode());
    int orgOrderNumber;
    if (nonNull(organization)) {
      orgOrderNumber = Integer.parseInt(organization.getCode().substring(3, 6));
    } else {
      orgOrderNumber = 0;
    }
    codeBuilder.append(String.format("%03d", orgOrderNumber + 1));

    return codeBuilder.toString();
  }
}
