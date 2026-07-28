package vn.viettel.bvrhm.nhahocduong.api.auth.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import vn.viettel.bvrhm.nhahocduong.api.auth.ForgotPasswordRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.RegisterSendOtpRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.ChangePasswordSendOtpRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.ResetPasswordRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.VerifyOtpRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.OtpService;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.UserService;

@DisplayName("PasswordResetController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class PasswordResetControllerTest {

  @Mock OtpService otpService;
  @Mock UserService userService;
  @InjectMocks PasswordResetController controller;

  @Nested
  @DisplayName("POST /api/auth/forgot-password")
  class ForgotPassword {

    @Test
    @DisplayName("Gửi OTP quên mật khẩu thành công")
    void shouldSendOtp() {
      ForgotPasswordRequest request = new ForgotPasswordRequest("admin", "0909000001", "admin@email.com");

      ResponseEntity<?> response = controller.forgotPassword(request);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      @SuppressWarnings("unchecked")
      Map<String, String> body = (Map<String, String>) response.getBody();
      assertThat(body).containsKey("message");
      verify(otpService).generateAndSendOtp("admin", "admin@email.com", "0909000001");
    }
  }

  @Nested
  @DisplayName("POST /api/auth/register-send-otp")
  class RegisterSendOtp {

    @Test
    @DisplayName("Gửi OTP đăng ký thành công")
    void shouldSendRegisterOtp() {
      RegisterSendOtpRequest request = new RegisterSendOtpRequest("newuser", "0909000002", "new@email.com");

      ResponseEntity<?> response = controller.registerSendOtp(request);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      @SuppressWarnings("unchecked")
      Map<String, String> body = (Map<String, String>) response.getBody();
      assertThat(body.get("message")).contains("OTP");
      verify(otpService).generateAndSendRegisterOtp("newuser", "new@email.com", "0909000002");
    }
  }

  @Nested
  @DisplayName("POST /api/auth/change-password-send-otp")
  class ChangePasswordSendOtp {

    @Test
    @DisplayName("Gửi OTP đổi mật khẩu thành công")
    void shouldSendChangePasswordOtp() {
      ChangePasswordSendOtpRequest request = new ChangePasswordSendOtpRequest("admin", "0909000001", "admin@email.com");

      ResponseEntity<?> response = controller.changePasswordSendOtp(request);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      @SuppressWarnings("unchecked")
      Map<String, String> body = (Map<String, String>) response.getBody();
      assertThat(body.get("message")).contains("OTP");
      verify(otpService).generateAndSendChangePasswordOtp("admin", "admin@email.com", "0909000001");
    }
  }

  @Nested
  @DisplayName("POST /api/auth/verify-otp")
  class VerifyOtp {

    @Test
    @DisplayName("Xác thực OTP thành công — trả về resetToken")
    void shouldVerifyOtp() {
      VerifyOtpRequest request = new VerifyOtpRequest("admin@email.com", "123456");
      when(otpService.verifyOtp("admin@email.com", "123456")).thenReturn("reset-token-abc");

      ResponseEntity<?> response = controller.verifyOtp(request);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      @SuppressWarnings("unchecked")
      Map<String, String> body = (Map<String, String>) response.getBody();
      assertThat(body).containsEntry("resetToken", "reset-token-abc");
    }
  }

  @Nested
  @DisplayName("POST /api/auth/reset-password")
  class ResetPassword {

    @Test
    @DisplayName("Đặt lại mật khẩu thành công")
    void shouldResetPassword() throws Exception {
      ResetPasswordRequest request = new ResetPasswordRequest("reset-token-abc", "newPass123");
      when(otpService.validateResetToken("reset-token-abc")).thenReturn("admin@email.com");

      ResponseEntity<?> response = controller.resetPassword(request);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      @SuppressWarnings("unchecked")
      Map<String, String> body = (Map<String, String>) response.getBody();
      assertThat(body.get("message")).contains("thành công");

      verify(userService).resetPassword("admin@email.com", "newPass123");
      verify(otpService).markTokenAsUsed("reset-token-abc");
    }
  }
}
