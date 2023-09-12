package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.OrganizationService;

import java.util.List;


@RestController
@RequestMapping("/api/organization")
public class OrganizationControler {
    @Autowired
    private OrganizationService organizationService;

    @GetMapping("/all")
    public List<OrganizationDTO> getByCondition(){
        return organizationService.getAll();
    }
    @GetMapping("/search")
    public List<OrganizationDTO> search(
      @RequestParam(name = "areaCode", required = false) String areaCode
    ) {
        return organizationService.getByAreaCode(areaCode);
    }
    @GetMapping("")
    public List<OrganizationDTO> getByCondition(@RequestParam(required = false) String name){
        return organizationService.getByCondition(name);
    }
    @PostMapping("")
    public OrganizationDTO createOrganization(@RequestBody OrganizationDTO organizationDTO) {
        OrganizationDTO createdDto = organizationService.createOrganization(organizationDTO);
        return createdDto;
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
}
