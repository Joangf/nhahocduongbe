package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Unit Tests")
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    /**
     * A valid Base64-encoded 256-bit (32-byte) key for HMAC-SHA256 signing.
     * Generated from 32 bytes of zeros for deterministic testing.
     */
    private static final String TEST_SIGNING_KEY = "dGVzdFNpZ25pbmdLZXlGb3JVbml0VGVzdHMxMjM0NTY=";  // 32 bytes base64
    private static final long TEST_EXPIRATION_MS = 3600000L; // 1 hour

    /** A DIFFERENT key to simulate token tampering. */
    private static final String WRONG_SIGNING_KEY = "d3JvbmdTaWduaW5nS2V5Rm9yVGFtcGVyVGVzdHMxMjM="; // 32 bytes different base64

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey", TEST_SIGNING_KEY);
        ReflectionTestUtils.setField(jwtService, "tokenExpTimeMillis", TEST_EXPIRATION_MS);
    }

    // ─── Helper methods ────────────────────────────────────────────────

    private UserDetails createMockUserDetails() {
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }

            @Override
            public String getPassword() {
                return "password";
            }

            @Override
            public String getUsername() {
                return "testuser";
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }

    private String createExpiredToken() {
        byte[] keyBytes = Decoders.BASE64.decode(TEST_SIGNING_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
            .setSubject("1")
            .setIssuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 hours ago
            .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    private String createTokenWithWrongKey() {
        byte[] keyBytes = Decoders.BASE64.decode(WRONG_SIGNING_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
            .setSubject("1")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    // ─── makeToken(Map, UserDetails) Tests ─────────────────────────────

    @Nested
    @DisplayName("makeToken(Map, UserDetails)")
    class MakeTokenWithUserDetailsTests {

        @Test
        @DisplayName("Happy path: generates signed JWT with roles and username claims")
        void makeToken_generatesSignedJwtWithCorrectClaims() {
            // Arrange
            Map<String, Object> extraClaims = new LinkedHashMap<>();
            List<RoleDTO> roles = List.of(
                new RoleDTO("1", "ADMIN", "Administrator", true, "Admin role")
            );
            extraClaims.put("roles", roles);
            extraClaims.put("username", "testuser");
            UserDetails userDetails = createMockUserDetails();

            // Act
            String token = jwtService.makeToken(extraClaims, userDetails);

            // Assert
            assertThat(token).isNotBlank();
            // Token has 3 parts separated by dots
            assertThat(token.split("\\.")).hasSize(3);

            // Verify subject is set from UserDetails.getUsername()
            String subject = jwtService.extractUserId(token);
            assertThat(subject).isEqualTo("testuser");

            // Verify username claim is extractable
            String username = jwtService.extractUsername(token);
            assertThat(username).isEqualTo("testuser");
        }

        @Test
        @DisplayName("Token is valid after creation")
        void makeToken_createdToken_isValid() {
            // Arrange
            Map<String, Object> claims = Map.of("username", "testuser");
            UserDetails userDetails = createMockUserDetails();

            // Act
            String token = jwtService.makeToken(claims, userDetails);

            // Assert
            assertThat(jwtService.isTokenValid(token)).isTrue();
        }
    }

    // ─── makeToken(Long, Map) Tests ────────────────────────────────────

    @Nested
    @DisplayName("makeToken(Long, Map)")
    class MakeTokenWithUserIdTests {

        @Test
        @DisplayName("Happy path: generates JWT with userId as subject and extra claims")
        void makeToken_withUserId_setsSubjectToUserId() {
            // Arrange
            Map<String, Object> claims = new LinkedHashMap<>();
            claims.put("username", "admin");
            claims.put("roles", List.of(new RoleDTO("1", "ADMIN", "Admin", true, null)));

            // Act
            String token = jwtService.makeToken(99L, claims);

            // Assert
            assertThat(token).isNotBlank();
            String subject = jwtService.extractUserId(token);
            assertThat(subject).isEqualTo("99");
            assertThat(jwtService.isTokenValid(token)).isTrue();
        }
    }

    // ─── makeTokenWithUserIdAndRoles() Tests ───────────────────────────

    @Nested
    @DisplayName("makeTokenWithUserIdAndRoles()")
    class MakeTokenWithUserIdAndRolesTests {

        @Test
        @DisplayName("Generates JWT with roles as comma-separated string claim")
        void makeTokenWithUserIdAndRoles_setsRolesClaimCorrectly() {
            // Arrange
            Collection<String> roles = List.of("ADMIN", "USER");

            // Act
            String token = jwtService.makeTokenWithUserIdAndRoles(1L, roles);

            // Assert
            assertThat(token).isNotBlank();
            String subject = jwtService.extractUserId(token);
            assertThat(subject).isEqualTo("1");

            // Extract the raw roles claim
            String rolesStr = jwtService.extractClaim(token, claims -> claims.get("roles", String.class));
            assertThat(rolesStr).isEqualTo("ADMIN,USER");
        }
    }

    // ─── isTokenValid() Tests ──────────────────────────────────────────

    @Nested
    @DisplayName("isTokenValid()")
    class IsTokenValidTests {

        @Test
        @DisplayName("Happy path: returns true for correctly signed, non-expired token")
        void isTokenValid_validToken_returnsTrue() {
            // Arrange
            String token = jwtService.makeToken(1L, Map.of("username", "test"));

            // Act & Assert
            assertThat(jwtService.isTokenValid(token)).isTrue();
        }

        @Test
        @DisplayName("Edge case: returns false for expired token")
        void isTokenValid_expiredToken_returnsFalse() {
            // Arrange
            String expiredToken = createExpiredToken();

            // Act & Assert
            assertThat(jwtService.isTokenValid(expiredToken)).isFalse();
        }

        @Test
        @DisplayName("Edge case: returns false for tampered token (wrong signing key)")
        void isTokenValid_tamperedToken_returnsFalse() {
            // Arrange
            String tamperedToken = createTokenWithWrongKey();

            // Act & Assert
            assertThat(jwtService.isTokenValid(tamperedToken)).isFalse();
        }

        @Test
        @DisplayName("Edge case: returns false for malformed token string")
        void isTokenValid_malformedToken_returnsFalse() {
            // Act & Assert
            assertThat(jwtService.isTokenValid("not.a.valid.jwt.token")).isFalse();
        }

        @Test
        @DisplayName("Edge case: returns false for completely garbage input")
        void isTokenValid_garbageInput_returnsFalse() {
            // Act & Assert
            assertThat(jwtService.isTokenValid("totallyNotAToken")).isFalse();
        }
    }

    // ─── extractUsername() Tests ────────────────────────────────────────

    @Nested
    @DisplayName("extractUsername()")
    class ExtractUsernameTests {

        @Test
        @DisplayName("Extraction: correctly parses 'username' claim from valid token")
        void extractUsername_validToken_returnsUsername() {
            // Arrange
            Map<String, Object> claims = new LinkedHashMap<>();
            claims.put("username", "drNguyen");
            String token = jwtService.makeToken(5L, claims);

            // Act
            String username = jwtService.extractUsername(token);

            // Assert
            assertThat(username).isEqualTo("drNguyen");
        }

        @Test
        @DisplayName("Returns null when username claim is not present")
        void extractUsername_noUsernameClaim_returnsNull() {
            // Arrange
            String token = jwtService.makeToken(5L, Map.of("key", "value"));

            // Act
            String username = jwtService.extractUsername(token);

            // Assert
            assertThat(username).isNull();
        }
    }

    // ─── extractUserId() Tests ─────────────────────────────────────────

    @Nested
    @DisplayName("extractUserId()")
    class ExtractUserIdTests {

        @Test
        @DisplayName("Extraction: correctly parses subject (userId) from valid token")
        void extractUserId_validToken_returnsSubject() {
            // Arrange
            String token = jwtService.makeToken(42L, Map.of("username", "test"));

            // Act
            String userId = jwtService.extractUserId(token);

            // Assert
            assertThat(userId).isEqualTo("42");
        }
    }

    // ─── extractRoles() Tests ──────────────────────────────────────────

    @Nested
    @DisplayName("extractRoles()")
    class ExtractRolesTests {

        @Test
        @DisplayName("Extraction: correctly parses RoleDTO list from valid token")
        void extractRoles_validToken_returnsRoleDTOList() {
            // Arrange
            List<RoleDTO> inputRoles = List.of(
                new RoleDTO("1", "ADMIN", "Administrator", true, "Admin role"),
                new RoleDTO("2", "USER", "Standard User", true, null)
            );
            Map<String, Object> claims = new LinkedHashMap<>();
            claims.put("roles", inputRoles);
            claims.put("username", "test");
            String token = jwtService.makeToken(1L, claims);

            // Act
            List<RoleDTO> extractedRoles = jwtService.extractRoles(token);

            // Assert
            assertThat(extractedRoles).hasSize(2);
            assertThat(extractedRoles.get(0).code()).isEqualTo("ADMIN");
            assertThat(extractedRoles.get(0).name()).isEqualTo("Administrator");
            assertThat(extractedRoles.get(1).code()).isEqualTo("USER");
        }

        @Test
        @DisplayName("Returns empty list when 'roles' claim is null")
        void extractRoles_noRolesClaim_returnsEmptyList() {
            // Arrange
            String token = jwtService.makeToken(1L, Map.of("username", "test"));

            // Act
            List<RoleDTO> roles = jwtService.extractRoles(token);

            // Assert
            assertThat(roles).isEmpty();
        }
    }

    // ─── Token Expiration Boundary Tests ───────────────────────────────

    @Nested
    @DisplayName("Expiration Boundary")
    class ExpirationBoundaryTests {

        @Test
        @DisplayName("Token with 1ms expiry is valid immediately then expires")
        void token_withVeryShortExpiry_expiresQuickly() throws InterruptedException {
            // Arrange — 1ms expiry
            ReflectionTestUtils.setField(jwtService, "tokenExpTimeMillis", 1L);
            String token = jwtService.makeToken(1L, Map.of("username", "test"));

            // Wait for the token to expire
            Thread.sleep(50);

            // Assert — token should now be expired
            assertThat(jwtService.isTokenValid(token)).isFalse();

            // Restore
            ReflectionTestUtils.setField(jwtService, "tokenExpTimeMillis", TEST_EXPIRATION_MS);
        }
    }

    // ─── extractClaim() Generic Tests ──────────────────────────────────

    @Nested
    @DisplayName("extractClaim()")
    class ExtractClaimTests {

        @Test
        @DisplayName("Extracts expiration date from token")
        void extractClaim_expiration_returnsDate() {
            // Arrange
            String token = jwtService.makeToken(1L, Map.of("username", "test"));

            // Act
            Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

            // Assert
            assertThat(expiration).isNotNull();
            assertThat(expiration).isAfter(new Date());
        }

        @Test
        @DisplayName("Extracts issuedAt date from token")
        void extractClaim_issuedAt_returnsDate() {
            // Arrange
            String token = jwtService.makeToken(1L, Map.of("username", "test"));

            // Act
            Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);

            // Assert
            assertThat(issuedAt).isNotNull();
            // issuedAt should be close to now (within last 5 seconds)
            assertThat(issuedAt.getTime()).isCloseTo(System.currentTimeMillis(), org.assertj.core.api.Assertions.within(5000L));
        }
    }
}
