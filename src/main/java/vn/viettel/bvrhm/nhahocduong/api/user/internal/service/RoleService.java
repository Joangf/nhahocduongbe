package vn.viettel.bvrhm.nhahocduong.api.user.internal.service;

import java.util.List;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;

/**
 * @author: longlb1
 * @since: 19-Sep-23
 */
public interface RoleService {
  List<RoleDTO> getActiveRoleByUsername(String username);

  List<RoleDTO> getAllRoles();
}
