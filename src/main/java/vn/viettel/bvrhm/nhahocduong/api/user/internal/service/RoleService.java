package vn.viettel.bvrhm.nhahocduong.api.user.internal.service;

import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;

import java.util.List;

/**
 * @author: longlb1
 * @since: 19-Sep-23
 */
public interface RoleService {
    List<RoleDTO> getActiveRoleByUsername(String username);
}
