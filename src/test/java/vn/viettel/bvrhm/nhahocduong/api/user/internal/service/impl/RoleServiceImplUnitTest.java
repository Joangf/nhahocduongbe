package vn.viettel.bvrhm.nhahocduong.api.user.internal.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.UserDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.Role;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.mapper.RoleMapper;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.RoleRepository;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.UserService;

@DisplayName("RoleServiceImpl — Unit Tests")
@ExtendWith(MockitoExtension.class)
class RoleServiceImplUnitTest {

  @Mock RoleRepository roleRepository;
  @Mock RoleMapper roleMapper;
  @Mock UserService userService;
  @InjectMocks RoleServiceImpl service;

  @Nested
  @DisplayName("TC-01 getActiveRoleByUsername()")
  class GetActiveRoleByUsername {

    @Test
    @DisplayName("Trả về danh sách role active của user")
    void shouldReturnActiveRoles() {
      UserDTO user = new UserDTO(1L, "admin", null, "Admin", "User",
          "admin@test.com", null, null,
          List.of(new RoleDTO("1", "ADMIN", "Admin", true, null)),
          null, true, true, null);

      when(userService.getUserByUsername("admin")).thenReturn(user);

      List<RoleDTO> result = service.getActiveRoleByUsername("admin");

      assertThat(result).hasSize(1);
      assertThat(result.get(0).code()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Lọc bỏ role inactive")
    void shouldFilterInactiveRoles() {
      UserDTO user = new UserDTO(1L, "user", null, "User", "Test",
          "user@test.com", null, null,
          List.of(
              new RoleDTO("1", "ADMIN", "Admin", false, null),
              new RoleDTO("2", "USER", "User", true, null)),
          null, true, true, null);

      when(userService.getUserByUsername("user")).thenReturn(user);

      List<RoleDTO> result = service.getActiveRoleByUsername("user");

      assertThat(result).hasSize(1);
      assertThat(result.get(0).code()).isEqualTo("USER");
    }
  }

  @Nested
  @DisplayName("TC-02 getAllRoles()")
  class GetAllRoles {

    @Test
    @DisplayName("Trả về tất cả roles")
    void shouldReturnAllRoles() {
      Role r1 = new Role();
      r1.setCode("ADMIN");
      Role r2 = new Role();
      r2.setCode("USER");
      RoleDTO dto1 = new RoleDTO("1", "ADMIN", "Admin", true, null);
      RoleDTO dto2 = new RoleDTO("2", "USER", "User", true, null);

      when(roleRepository.findAll()).thenReturn(List.of(r1, r2));
      when(roleMapper.toDtoList(List.of(r1, r2))).thenReturn(List.of(dto1, dto2));

      List<RoleDTO> result = service.getAllRoles();

      assertThat(result).hasSize(2);
    }
  }
}
