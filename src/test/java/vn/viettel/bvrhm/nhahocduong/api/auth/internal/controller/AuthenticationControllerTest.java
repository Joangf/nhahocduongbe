package vn.viettel.bvrhm.nhahocduong.api.auth.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import vn.viettel.bvrhm.nhahocduong.api.auth.LoginRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginResponse;
import vn.viettel.bvrhm.nhahocduong.api.auth.TokenPair;
import vn.viettel.bvrhm.nhahocduong.api.auth.exception.InvalidCredentialException;
import vn.viettel.bvrhm.nhahocduong.api.auth.exception.InvalidCredentialException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthenticationService;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.JwtService;

@DisplayName("AuthenticationController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

  @Mock AuthenticationService authenticationService;
  @Mock JwtService jwtService;
  @Mock HttpServletRequest request;
  @InjectMocks AuthenticationController controller;

  @Nested
  @DisplayName("POST /api/auth/login")
  class Login {

    @Test
    @DisplayName("Đăng nhập thành công — trả về token và set cookie")
    void shouldLoginSuccessfully() throws InvalidCredentialException {
      LoginRequest loginRequest = new LoginRequest("admin", "password123");
      TokenPair tokenPair = new TokenPair("access-token", "refresh-token");
      when(authenticationService.authenticate(loginRequest)).thenReturn(tokenPair);
      when(request.isSecure()).thenReturn(false);

      ResponseEntity<?> response = controller.login(loginRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isInstanceOf(LoginResponse.class);
      LoginResponse body = (LoginResponse) response.getBody();
      assertThat(body.token()).isEqualTo("access-token");

      // Verify refresh token cookie is set
      String setCookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
      assertThat(setCookie).contains("refresh_token=refresh-token");
      assertThat(setCookie).contains("HttpOnly");
    }

    @Test
    @DisplayName("Đăng nhập thất bại — trả về 401")
    void shouldReturn401OnInvalidCredentials() throws InvalidCredentialException {
      LoginRequest loginRequest = new LoginRequest("admin", "wrong");
      when(authenticationService.authenticate(loginRequest))
          .thenThrow(new InvalidCredentialException());

      ResponseEntity<?> response = controller.login(loginRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
      @SuppressWarnings("unchecked")
      Map<String, String> body = (Map<String, String>) response.getBody();
      assertThat(body).containsKey("error");
    }
  }

  @Nested
  @DisplayName("POST /api/auth/guest-login")
  class GuestLogin {

    @Test
    @DisplayName("Guest login — trả về LoginResponse")
    void shouldGuestLogin() {
      LoginResponse expected = new LoginResponse("guest-token");
      when(authenticationService.guestLogin()).thenReturn(expected);

      LoginResponse result = controller.guestLogin();

      assertThat(result).isSameAs(expected);
    }
  }

  @Nested
  @DisplayName("POST /api/auth/logout")
  class Logout {

    @Test
    @DisplayName("Đăng xuất — xóa cookie và gọi service.logout khi token hợp lệ")
    void shouldLogoutWithValidToken() {
      when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
      when(jwtService.isTokenValid("valid-token")).thenReturn(true);
      when(jwtService.extractUsername("valid-token")).thenReturn("admin");
      when(request.isSecure()).thenReturn(false);

      ResponseEntity<?> response = controller.logout("refresh-token");

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(authenticationService).logout("admin", "refresh-token");

      String setCookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
      assertThat(setCookie).contains("refresh_token=");
      assertThat(setCookie).contains("Max-Age=0");
    }

    @Test
    @DisplayName("Đăng xuất — không gọi service khi không có Authorization header")
    void shouldLogoutWithoutAuthHeader() {
      when(request.getHeader("Authorization")).thenReturn(null);
      when(request.isSecure()).thenReturn(false);

      ResponseEntity<?> response = controller.logout(null);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(authenticationService, never()).logout(any(), any());
    }

    @Test
    @DisplayName("Đăng xuất — không gọi service khi token không hợp lệ")
    void shouldLogoutWithInvalidToken() {
      when(request.getHeader("Authorization")).thenReturn("Bearer bad-token");
      when(jwtService.isTokenValid("bad-token")).thenReturn(false);
      when(request.isSecure()).thenReturn(false);

      ResponseEntity<?> response = controller.logout(null);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(authenticationService, never()).logout(any(), any());
    }

    @Test
    @DisplayName("Đăng xuất — không gọi service khi header không phải Bearer")
    void shouldLogoutWithNonBearerHeader() {
      when(request.getHeader("Authorization")).thenReturn("Basic abc123");
      when(request.isSecure()).thenReturn(false);

      ResponseEntity<?> response = controller.logout(null);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(authenticationService, never()).logout(any(), any());
    }
  }

  @Nested
  @DisplayName("POST /api/auth/refresh")
  class RefreshToken {

    @Test
    @DisplayName("Refresh token thành công — trả về token mới và set cookie")
    void shouldRefreshTokenSuccessfully() throws InvalidCredentialException {
      TokenPair tokenPair = new TokenPair("new-access", "new-refresh");
      when(authenticationService.refreshToken("valid-refresh")).thenReturn(tokenPair);
      when(request.isSecure()).thenReturn(false);

      ResponseEntity<?> response = controller.refreshToken("valid-refresh");

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isInstanceOf(LoginResponse.class);
      LoginResponse body = (LoginResponse) response.getBody();
      assertThat(body.token()).isEqualTo("new-access");

      String setCookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
      assertThat(setCookie).contains("new-refresh");
    }

    @Test
    @DisplayName("Refresh token null — trả về 401")
    void shouldReturn401WhenTokenNull() {
      ResponseEntity<?> response = controller.refreshToken(null);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
      @SuppressWarnings("unchecked")
      Map<String, String> body = (Map<String, String>) response.getBody();
      assertThat(body).containsKey("error");
    }

    @Test
    @DisplayName("Refresh token rỗng — trả về 401")
    void shouldReturn401WhenTokenBlank() {
      ResponseEntity<?> response = controller.refreshToken("  ");

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Refresh token không hợp lệ — trả về 401")
    void shouldReturn401WhenRefreshTokenInvalid() throws InvalidCredentialException {
      when(authenticationService.refreshToken("invalid-refresh"))
          .thenThrow(new InvalidCredentialException());

      ResponseEntity<?> response = controller.refreshToken("invalid-refresh");

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
  }
}
