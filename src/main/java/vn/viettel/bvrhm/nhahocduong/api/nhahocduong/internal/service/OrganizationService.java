package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.AuthorizationService;
import vn.viettel.bvrhm.nhahocduong.api.common.AreaService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.search.SearchOrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.OrganizationMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.OrganizationRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;

import java.util.Collections;
import java.util.List;

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

    organizationRepository.saveAndFlush(entity);
    entityManager.refresh(entity);

    return organizationMapper.toDto(entity);
  }

  @Transactional
  public OrganizationDTO updateOrganization(OrganizationDTO patientDTO, Long id) {
    var entity = organizationMapper.toEntity(patientDTO);
    entity.setId(id);


    organizationRepository.save(entity);

    return organizationMapper.toDto(entity);
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

  public Page<OrganizationDTO> search(SearchOrganizationDTO searchCriteria, Pageable pageable) {
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
    Page<Organization> organizations = organizationRepository.findByCondition(areaCodesInside,
                                                                              searchCriteria.getType(),
                                                                              authData.getOrganizationId(),
                                                                              pageable);

    return organizations.map(organizationMapper::toDto);
  }
}
