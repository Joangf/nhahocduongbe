package vn.viettel.bvrhm.nhahocduong.api.user.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.RoleService;

@DisplayName("RoleController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

  @Mock RoleService roleService;
  @InjectMocks RoleController controller;

  @Nested
  @DisplayName("GET /api/users/{id}/roles")
  class GetRolesByUserId {

    @Test
    @DisplayName("Trả về danh sách rỗng (hardcoded)")
    void shouldReturnEmptyList() {
      assertThat(controller.getRolesByUserId()).isEmpty();
    }
  }

  @Nested
  @DisplayName("GET /api/roles")
  class GetAllRoles {

    @Test
    @DisplayName("Trả về tất cả roles")
    void shouldReturnAllRoles() {
      RoleDTO dto = mock(RoleDTO.class);
      when(roleService.getAllRoles()).thenReturn(List.of(dto));

      assertThat(controller.getAllRoles()).hasSize(1);
    }
  }
}
