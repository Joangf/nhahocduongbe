package vn.viettel.bvrhm.nhahocduong.api.user.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.OtpService;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.UserDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.UserService;

@DisplayName("UserController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock UserService userService;
  @Mock OtpService otpService;
  @InjectMocks UserController controller;

  @Nested
  @DisplayName("GET /api/user/{id}")
  class GetUserById {

    @Test
    @DisplayName("Trả về user theo id")
    void shouldReturnUserById() {
      UserDTO dto = mock(UserDTO.class);
      when(userService.getUserById(1L)).thenReturn(dto);
      assertThat(controller.getUserById(1L)).isSameAs(dto);
    }
  }

  @Nested
  @DisplayName("POST /api/user/register")
  class CreateUser {

    @Test
    @DisplayName("Đăng ký thành công khi OTP hợp lệ")
    void shouldRegisterWithValidOtp() throws Exception {
      UserDTO input = mock(UserDTO.class);
      when(input.email()).thenReturn("test@email.com");
      when(otpService.validateResetToken("valid-token")).thenReturn("test@email.com");
      when(userService.createUser(input)).thenReturn(input);

      var result = controller.createUser(input, "valid-token");

      assertThat(result).isSameAs(input);
      verify(otpService).markTokenAsUsed("valid-token");
    }

    @Test
    @DisplayName("Ném 400 khi email không khớp OTP")
    void shouldThrowWhenEmailMismatch() throws Exception {
      UserDTO input = mock(UserDTO.class);
      when(input.email()).thenReturn("user@email.com");
      when(otpService.validateResetToken("token")).thenReturn("other@email.com");

      assertThatThrownBy(() -> controller.createUser(input, "token"))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("không khớp");
    }
  }

  @Nested
  @DisplayName("GET /api/user/waiting")
  class GetWaitingUsers {

    @Test
    @DisplayName("Trả về danh sách user chờ duyệt")
    void shouldReturnWaitingUsers() {
      when(userService.getWaitingUsers()).thenReturn(List.of());
      assertThat(controller.getWaitingUsers()).isEmpty();
    }
  }

  @Nested
  @DisplayName("PUT /api/user/{id}/approve")
  class ApproveUser {

    @Test
    @DisplayName("Phê duyệt user")
    void shouldApproveUser() {
      UserDTO dto = mock(UserDTO.class);
      when(userService.approveUser(1L)).thenReturn(dto);
      assertThat(controller.approveUser(1L)).isSameAs(dto);
    }
  }

  @Nested
  @DisplayName("DELETE /api/user/{id}/reject")
  class RejectUser {

    @Test
    @DisplayName("Từ chối user")
    void shouldRejectUser() {
      controller.rejectUser(1L);
      verify(userService).rejectUser(1L);
    }
  }

  @Nested
  @DisplayName("GET /api/user/hello")
  class Hello {

    @Test
    @DisplayName("Trả về Hello")
    void shouldReturnHello() {
      assertThat(controller.hello()).isEqualTo("Hello");
    }
  }
}
