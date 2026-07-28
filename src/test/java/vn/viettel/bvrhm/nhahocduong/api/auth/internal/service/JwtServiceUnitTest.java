package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;

@DisplayName("JwtService — Unit Tests")
class JwtServiceUnitTest {

  JwtService jwtService;

  /** 32-byte key cho HS256, Base64-encoded. */
  private static final String TEST_KEY = Base64.getEncoder().encodeToString(
      "abcdefghijklmnopqrstuvwxyz123456".getBytes());

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    ReflectionTestUtils.setField(jwtService, "jwtSigningKey", TEST_KEY);
    ReflectionTestUtils.setField(jwtService, "tokenExpTimeMillis", 3600000L); // 1h
  }

  @Nested
  @DisplayName("TC-01 makeToken & isTokenValid")
  class TokenCreationAndValidation {

    @Test
    @DisplayName("makeToken(Long userId, Map) tạo token hợp lệ")
    void shouldCreateValidToken() {
      Map<String, Object> claims = Map.of("roles", List.of(
          new RoleDTO("1", "ADMIN", "Admin", true, null)),
          "username", "admin");

      String token = jwtService.makeToken(1L, claims);

      assertThat(token).isNotBlank();
      assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("Token hết hạn trả về false")
    void expiredTokenShouldReturnFalse() throws Exception {
      ReflectionTestUtils.setField(jwtService, "tokenExpTimeMillis", -1000L); // đã hết hạn
      Map<String, Object> claims = Map.of("roles", List.of(), "username", "test");

      String token = jwtService.makeToken(1L, claims);

      assertThat(jwtService.isTokenValid(token)).isFalse();
    }

    @Test
    @DisplayName("Token không hợp lệ trả về false")
    void invalidTokenShouldReturnFalse() {
      assertThat(jwtService.isTokenValid("invalid.token.here")).isFalse();
    }
  }

  @Nested
  @DisplayName("TC-02 extractClaims")
  class ExtractClaims {

    @Test
    @DisplayName("extractUserId trả về subject")
    void shouldExtractUserId() {
      Map<String, Object> claims = Map.of("roles", List.of(), "username", "admin");

      String token = jwtService.makeToken(1L, claims);

      assertThat(jwtService.extractUserId(token)).isEqualTo("1");
    }

    @Test
    @DisplayName("extractUsername trả về claim username")
    void shouldExtractUsername() {
      Map<String, Object> claims = Map.of("roles", List.of(), "username", "admin");

      String token = jwtService.makeToken(1L, claims);

      assertThat(jwtService.extractUsername(token)).isEqualTo("admin");
    }

    @Test
    @DisplayName("extractRoles trả về danh sách roles")
    void shouldExtractRoles() {
      Map<String, Object> claims = Map.of(
          "roles", List.of(new RoleDTO("1", "ADMIN", "Admin", true, null)),
          "username", "admin");

      String token = jwtService.makeToken(1L, claims);

      List<RoleDTO> roles = jwtService.extractRoles(token);
      assertThat(roles).hasSize(1);
      assertThat(roles.get(0).code()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("extractRoles trả về list rỗng khi không có role")
    void shouldReturnEmptyRolesWhenMissing() {
      String token = jwtService.makeToken(1L, Map.of("username", "guest"));

      List<RoleDTO> roles = jwtService.extractRoles(token);

      assertThat(roles).isEmpty();
    }
  }

  @Nested
  @DisplayName("TC-03 makeTokenWithUserIdAndRoles")
  class MakeTokenWithUserIdAndRoles {

    @Test
    @DisplayName("Tạo token với userId và roles string")
    void shouldCreateToken() {
      String token = jwtService.makeTokenWithUserIdAndRoles(1L, List.of("ADMIN", "USER"));

      assertThat(token).isNotBlank();
      assertThat(jwtService.isTokenValid(token)).isTrue();
      assertThat(jwtService.extractUserId(token)).isEqualTo("1");
    }
  }
}
