package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.model.response.UpsertResponseModel;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.OrganizationSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;

/**
 * @author: longlb1
 * @since: 22-Sep-23
 */
public interface OrganizationService {
  OrganizationDTO getOrganizationById(Long id);

  OrganizationDTO createOrganization(OrganizationDTO organizationDTO);

  OrganizationDTO updateOrganization(OrganizationDTO organizationDTO, Long id);

  boolean delete(Long id);

  List<OrganizationDTO> getByCondition(String name);

  List<OrganizationDTO> getByAreaCode(String areaCode);

  List<OrganizationDTO> getAll();

  Page<OrganizationDTO> search(OrganizationSearchCriteria searchCriteria, Pageable pageable);

  OrganizationDTO getOrganizationByCode(String code);

  UpsertResponseModel checkDeletableClasses(Long organizationId, List<String> clazz);
}
