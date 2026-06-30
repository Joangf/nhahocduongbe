package vn.viettel.bvrhm.nhahocduong.api.user.internal.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.UserDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.mapper.RoleMapper;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.RoleRepository;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.RoleService;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.UserService;

/**
 * @author: longlb1
 * @since: 19-Sep-23
 */
@Service
public class RoleServiceImpl implements RoleService {
  @Autowired RoleRepository roleRepository;

  @Autowired UserService userService;

  @Autowired RoleMapper roleMapper;

  @Override
  public List<RoleDTO> getActiveRoleByUsername(String username) {
    UserDTO userDTO = userService.getUserByUsername(username);
    return userDTO.roleList().stream().filter(RoleDTO::status).toList();
  }

  @Override
  public List<RoleDTO> getAllRoles() {
    return roleMapper.toDtoList(roleRepository.findAll());
  }
}
