package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.model.response.UpsertResponseModel;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.OrganizationSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.OrganizationService;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {
  @Autowired private OrganizationService organizationService;

  @GetMapping("/all")
  public List<OrganizationDTO> getAll() {
    return organizationService.getAll();
  }

  @GetMapping("/search")
  public Page<OrganizationDTO> search(
      OrganizationSearchCriteria searchCriteria, Pageable pageable) {
    return organizationService.search(searchCriteria, pageable);
  }

  @PostMapping("")
  public OrganizationDTO createOrganization(@RequestBody OrganizationDTO organizationDTO) {
    return organizationService.createOrganization(organizationDTO);
  }

  @PutMapping("/{id}")
  public OrganizationDTO updateOrganization(
      @RequestBody OrganizationDTO organizationDTO, @PathVariable Long id) {
    return organizationService.updateOrganization(organizationDTO, id);
  }

  @GetMapping("/{id}")
  public OrganizationDTO getOrganizationById(@PathVariable Long id) {
    return organizationService.getOrganizationById(id);
  }

  @DeleteMapping("/{id}")
  public boolean deleteOrganization(@PathVariable Long id) {
    return organizationService.delete(id);
  }

  @PostMapping("/{id}/classes/deletable")
  public UpsertResponseModel checkDeletableClass(
      @PathVariable("id") Long organizationId, @RequestBody List<String> classes) {
    return organizationService.checkDeletableClasses(organizationId, classes);
  }
}
