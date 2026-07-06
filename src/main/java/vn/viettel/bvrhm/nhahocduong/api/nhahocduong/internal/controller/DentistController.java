package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DentistWithUserDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.DentistService;

@RestController
@RequestMapping("/api/dentists")
public class DentistController {

  @Autowired private DentistService dentistService;

  @GetMapping
  public List<DentistWithUserDTO> getAllDentists() {
    return dentistService.getAllDentistsWithUserInfo();
  }
}
