package vn.viettel.bvrhm.nhahocduong.api.user.internal.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.Role;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.RoleService;

/**
 * @author: longlb1
 * @since: 19-Sep-23
 */
@RestController
@RequestMapping("/api")
public class RoleController {

  @Autowired
  private RoleService roleService;

  @GetMapping("/users/{id}/roles")
  List<Role> getRolesByUserId() {
    return List.of();
  }

  @GetMapping("/roles")
  List<RoleDTO> getAllRoles() {
    return roleService.getAllRoles();
  }
}
