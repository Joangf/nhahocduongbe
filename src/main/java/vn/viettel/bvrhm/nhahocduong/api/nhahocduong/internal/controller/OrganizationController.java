package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.criteria.OrganizationSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.OrganizationService;

import java.util.List;


@RestController
@RequestMapping("/api/organization")
public class OrganizationController {
    @Autowired
    private OrganizationService organizationService;

    @GetMapping("/all")
    public List<OrganizationDTO> getAll(){
        return organizationService.getAll();
    }

    @GetMapping("/search")
    public Page<OrganizationDTO> search(
        OrganizationSearchCriteria searchCriteria,
        Pageable pageable
    ) {
        return organizationService.search(searchCriteria, pageable);
    }

    @PostMapping("")
    public OrganizationDTO createOrganization(@RequestBody OrganizationDTO organizationDTO) {
        return organizationService.createOrganization(organizationDTO);
    }
    @PutMapping("/{id}")
    public OrganizationDTO updateOrganization(@RequestBody OrganizationDTO organizationDTO, @PathVariable Long id) {
        return organizationService.updateOrganization(organizationDTO, id);
    }

    @GetMapping("/{id}")
    public OrganizationDTO getOrganizationById(@PathVariable Long id) {
        OrganizationDTO dto = organizationService.getOrganizationById(id);
        return dto;
    }
    @DeleteMapping("/{id}")
    public boolean deleteOrganization(@PathVariable Long id){
        return organizationService.delete(id);
    }

    @GetMapping("/{id}/classes/{clazz}/deletable")
    public boolean checkDeletableClass(@PathVariable("id") Long organizationId, @PathVariable String clazz) {
        return organizationService.checkDeletableClass(organizationId, clazz);
    }
}
