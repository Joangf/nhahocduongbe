package vn.viettel.bvrhm.nhahocduong.api.user.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository.LoginLogRepository;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.UserDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.UserService;

@DisplayName("AdminController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

  @Mock UserService userService;
  @Mock LoginLogRepository loginLogRepository;
  @InjectMocks AdminController controller;

  @Nested
  @DisplayName("GET /api/admin/waiting")
  class GetWaitingUsers {

    @Test
    @DisplayName("Trả về danh sách user đang chờ duyệt")
    void shouldReturnWaitingUsers() {
      when(userService.getWaitingUsers()).thenReturn(List.of());
      assertThat(controller.getWaitingUsers()).isEmpty();
    }
  }

  @Nested
  @DisplayName("GET /api/admin/users")
  class GetAllUsers {

    @Test
    @DisplayName("Trả về tất cả users")
    void shouldReturnAllUsers() {
      when(userService.getAllUsers()).thenReturn(List.of());
      assertThat(controller.getAllUsers()).isEmpty();
    }
  }

  @Nested
  @DisplayName("PUT /api/admin/users/{id}/lock")
  class LockUser {

    @Test
    @DisplayName("Khóa user")
    void shouldLockUser() {
      controller.lockUser(1L);
      verify(userService).lockUser(1L);
    }
  }

  @Nested
  @DisplayName("PUT /api/admin/users/{id}/unlock")
  class UnlockUser {

    @Test
    @DisplayName("Mở khóa user")
    void shouldUnlockUser() {
      controller.unlockUser(1L);
      verify(userService).unlockUser(1L);
    }
  }

  @Nested
  @DisplayName("GET /api/admin/login-logs")
  class GetLoginLogs {

    @Test
    @DisplayName("Trả về login logs (loại trừ guest)")
    void shouldReturnLoginLogs() {
      when(loginLogRepository.findByUsernameNotOrderByLoginTimeDesc("guest")).thenReturn(List.of());

      var result = controller.getLoginLogs();

      assertThat(result.getStatusCode().value()).isEqualTo(200);
    }
  }
}
