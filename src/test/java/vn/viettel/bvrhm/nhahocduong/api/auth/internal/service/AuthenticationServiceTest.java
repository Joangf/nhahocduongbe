package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginResponse;
import vn.viettel.bvrhm.nhahocduong.api.auth.TokenPair;
import vn.viettel.bvrhm.nhahocduong.api.auth.exception.InvalidCredentialException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.LoginLog;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.RefreshToken;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.mapper.UserAuthDetailsMapper;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.object.UserAuthDetails;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository.LoginLogRepository;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository.RefreshTokenRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.UserDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.User;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.UserRepository;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService Unit Tests")
class AuthenticationServiceTest {

    @Mock private UserService userService;
    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private UserAuthDetailsMapper userAuthDetailsMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private LoginLogRepository loginLogRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks private AuthenticationService authenticationService;

    @Captor private ArgumentCaptor<LoginLog> loginLogCaptor;
    @Captor private ArgumentCaptor<RefreshToken> refreshTokenCaptor;

    // ─── Helper methods ────────────────────────────────────────────────

    private UserDTO createMockUserDTO() {
        return createMockUserDTO(true, true);
    }

    private UserDTO createMockUserDTO(boolean status, Boolean registerStatus) {
        List<RoleDTO> roles = List.of(
            new RoleDTO("1", "ADMIN", "Administrator", true, "Admin role"),
            new RoleDTO("2", "INACTIVE_ROLE", "Inactive", false, "Disabled role")
        );
        OrganizationDTO org = new OrganizationDTO();
        org.setId(10L);
        org.setName("Test School");
        org.setCode("TS01");

        return new UserDTO(
            1L, "testuser", "hashedPassword", "John", "Doe",
            "john@test.com", "0901234567", null,
            roles, org, registerStatus, status, LocalDateTime.now()
        );
    }

    private UserAuthDetails createMockUserAuthDetails(boolean enabled, Boolean registerStatus) {
        UserAuthDetails details = new UserAuthDetails();
        details.setUserId(1L);
        details.setUsername("testuser");
        details.setEnabled(enabled);
        details.setRegisterStatus(registerStatus);
        details.setAccountNonExpired(true);
        details.setAccountNonLocked(true);
        details.setCredentialsNonExpired(true);
        details.setAuthorities(new HashSet<>(
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        ));
        return details;
    }

    private LoginRequest createLoginRequest() {
        return new LoginRequest("testuser", "correctPassword");
    }

    private User createMockUserEntity() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        return user;
    }

    private RefreshToken createMockRefreshToken(boolean revoked, boolean expired) {
        User user = createMockUserEntity();
        return RefreshToken.builder()
            .id(1L)
            .token("valid-refresh-token")
            .user(user)
            .expiresAt(expired ? LocalDateTime.now().minusDays(1) : LocalDateTime.now().plusDays(30))
            .revoked(revoked)
            .createdAt(LocalDateTime.now())
            .build();
    }

    // ─── authenticate() Tests ──────────────────────────────────────────

    @Nested
    @DisplayName("authenticate()")
    class AuthenticateTests {

        @Test
        @DisplayName("TC-AUTH-01: Happy path — valid credentials returns TokenPair with LoginLog status=true")
        void authenticate_happyPath_returnsTokenPairAndLogsSuccess() throws Exception {
            // Arrange
            LoginRequest request = createLoginRequest();
            UserDTO userDTO = createMockUserDTO();
            UserAuthDetails authDetails = createMockUserAuthDetails(true, true);
            User userEntity = createMockUserEntity();

            when(userService.getUserByUsername("testuser")).thenReturn(userDTO);
            when(userAuthDetailsMapper.userAuthDetailsFromUserDTO(userDTO)).thenReturn(authDetails);
            when(userService.checkValidUserIdPassword(1L, "correctPassword")).thenReturn(true);
            when(jwtService.makeToken(eq(1L), any(Map.class))).thenReturn("mock-access-token");
            when(userRepository.getReferenceById(1L)).thenReturn(userEntity);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            TokenPair result = authenticationService.authenticate(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.accessToken()).isEqualTo("mock-access-token");
            assertThat(result.refreshToken()).isNotBlank();

            // Verify LoginLog was saved with status=true
            verify(loginLogRepository).save(loginLogCaptor.capture());
            LoginLog savedLog = loginLogCaptor.getValue();
            assertThat(savedLog.getUsername()).isEqualTo("testuser");
            assertThat(savedLog.getStatus()).isTrue();
            assertThat(savedLog.getPhoneNumber()).isEqualTo("0901234567");
            assertThat(savedLog.getLoginTime()).isNotNull();

            // Verify JWT was built with correct claims
            ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
            verify(jwtService).makeToken(eq(1L), claimsCaptor.capture());
            Map<String, Object> capturedClaims = claimsCaptor.getValue();
            assertThat(capturedClaims).containsKey("roles");
            assertThat(capturedClaims).containsEntry("username", "testuser");
            assertThat(capturedClaims).containsKey("organization");

            // Verify only active roles are included
            @SuppressWarnings("unchecked")
            List<RoleDTO> capturedRoles = (List<RoleDTO>) capturedClaims.get("roles");
            assertThat(capturedRoles).hasSize(1);
            assertThat(capturedRoles.get(0).code()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("TC-AUTH-02: Invalid password throws InvalidCredentialException")
        void authenticate_wrongPassword_throwsInvalidCredentialException() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("testuser", "wrongPassword");
            UserDTO userDTO = createMockUserDTO();
            UserAuthDetails authDetails = createMockUserAuthDetails(true, true);

            when(userService.getUserByUsername("testuser")).thenReturn(userDTO);
            when(userAuthDetailsMapper.userAuthDetailsFromUserDTO(userDTO)).thenReturn(authDetails);
            when(userService.checkValidUserIdPassword(1L, "wrongPassword")).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(InvalidCredentialException.class);

            // Verify no login log or token was created
            verify(loginLogRepository, never()).save(any(LoginLog.class));
            verify(jwtService, never()).makeToken(anyLong(), any(Map.class));
        }

        @Test
        @DisplayName("TC-AUTH-03: Locked account (isEnabled==false) throws InvalidCredentialException")
        void authenticate_accountLocked_throwsInvalidCredentialException() throws Exception {
            // Arrange
            LoginRequest request = createLoginRequest();
            UserDTO userDTO = createMockUserDTO(false, true);
            UserAuthDetails authDetails = createMockUserAuthDetails(false, true);

            when(userService.getUserByUsername("testuser")).thenReturn(userDTO);
            when(userAuthDetailsMapper.userAuthDetailsFromUserDTO(userDTO)).thenReturn(authDetails);

            // Act & Assert
            assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(InvalidCredentialException.class);

            // Verify password check was never reached
            verify(userService, never()).checkValidUserIdPassword(anyLong(), anyString());
            verify(loginLogRepository, never()).save(any(LoginLog.class));
        }

        @Test
        @DisplayName("TC-AUTH-04: Not approved (registerStatus==false) throws InvalidCredentialException")
        void authenticate_notApproved_throwsInvalidCredentialException() throws Exception {
            // Arrange
            LoginRequest request = createLoginRequest();
            UserDTO userDTO = createMockUserDTO(true, false);
            UserAuthDetails authDetails = createMockUserAuthDetails(true, false);

            when(userService.getUserByUsername("testuser")).thenReturn(userDTO);
            when(userAuthDetailsMapper.userAuthDetailsFromUserDTO(userDTO)).thenReturn(authDetails);

            // Act & Assert
            assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(InvalidCredentialException.class);

            verify(userService, never()).checkValidUserIdPassword(anyLong(), anyString());
        }

        @Test
        @DisplayName("TC-AUTH-04b: registerStatus==null throws InvalidCredentialException")
        void authenticate_registerStatusNull_throwsInvalidCredentialException() throws Exception {
            // Arrange
            LoginRequest request = createLoginRequest();
            UserDTO userDTO = createMockUserDTO(true, null);
            UserAuthDetails authDetails = createMockUserAuthDetails(true, null);

            when(userService.getUserByUsername("testuser")).thenReturn(userDTO);
            when(userAuthDetailsMapper.userAuthDetailsFromUserDTO(userDTO)).thenReturn(authDetails);

            // Act & Assert
            assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(InvalidCredentialException.class);
        }

        @Test
        @DisplayName("TC-AUTH-02b: User not found throws InvalidCredentialException")
        void authenticate_userNotFound_throwsInvalidCredentialException() throws Exception {
            // Arrange
            LoginRequest request = createLoginRequest();
            when(userService.getUserByUsername("testuser")).thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(InvalidCredentialException.class);
        }

        @Test
        @DisplayName("TC-AUTH-02c: UserService throws exception wraps as InvalidCredentialException")
        void authenticate_userServiceException_throwsInvalidCredentialException() throws Exception {
            // Arrange
            LoginRequest request = createLoginRequest();
            when(userService.getUserByUsername("testuser"))
                .thenThrow(new RuntimeException("DB error"));

            // Act & Assert
            assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(InvalidCredentialException.class);
        }

        @Test
        @DisplayName("Claims omit organization when organization is null")
        void authenticate_noOrganization_claimsOmitOrganization() throws Exception {
            // Arrange
            LoginRequest request = createLoginRequest();
            UserDTO userDTO = new UserDTO(
                1L, "testuser", "hashedPassword", "John", "Doe",
                "john@test.com", "0901234567", null,
                List.of(new RoleDTO("1", "ADMIN", "Administrator", true, "Admin")),
                null, true, true, LocalDateTime.now()
            );
            UserAuthDetails authDetails = createMockUserAuthDetails(true, true);
            User userEntity = createMockUserEntity();

            when(userService.getUserByUsername("testuser")).thenReturn(userDTO);
            when(userAuthDetailsMapper.userAuthDetailsFromUserDTO(userDTO)).thenReturn(authDetails);
            when(userService.checkValidUserIdPassword(1L, "correctPassword")).thenReturn(true);
            when(jwtService.makeToken(eq(1L), any(Map.class))).thenReturn("token");
            when(userRepository.getReferenceById(1L)).thenReturn(userEntity);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            authenticationService.authenticate(request);

            // Assert — organization key should NOT be present in claims
            ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
            verify(jwtService).makeToken(eq(1L), claimsCaptor.capture());
            assertThat(claimsCaptor.getValue()).doesNotContainKey("organization");
        }

        @Test
        @DisplayName("Empty roleList results in empty roles claim list")
        void authenticate_nullRoleList_returnsEmptyRoles() throws Exception {
            // Arrange
            LoginRequest request = createLoginRequest();
            UserDTO userDTO = new UserDTO(
                1L, "testuser", "hashedPassword", "John", "Doe",
                "john@test.com", "0901234567", null,
                null, null, true, true, LocalDateTime.now()
            );
            UserAuthDetails authDetails = createMockUserAuthDetails(true, true);
            User userEntity = createMockUserEntity();

            when(userService.getUserByUsername("testuser")).thenReturn(userDTO);
            when(userAuthDetailsMapper.userAuthDetailsFromUserDTO(userDTO)).thenReturn(authDetails);
            when(userService.checkValidUserIdPassword(1L, "correctPassword")).thenReturn(true);
            when(jwtService.makeToken(eq(1L), any(Map.class))).thenReturn("token");
            when(userRepository.getReferenceById(1L)).thenReturn(userEntity);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            authenticationService.authenticate(request);

            // Assert
            ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
            verify(jwtService).makeToken(eq(1L), claimsCaptor.capture());
            @SuppressWarnings("unchecked")
            List<RoleDTO> roles = (List<RoleDTO>) claimsCaptor.getValue().get("roles");
            assertThat(roles).isEmpty();
        }
    }

    // ─── guestLogin() Tests ────────────────────────────────────────────

    @Nested
    @DisplayName("guestLogin()")
    class GuestLoginTests {

        @Test
        @DisplayName("TC-AUTH-05: Guest login returns JWT with userId=0, username=guest, GUEST role")
        void guestLogin_returnsTokenWithGuestClaims() {
            // Arrange
            when(jwtService.makeToken(eq(0L), any(Map.class))).thenReturn("guest-jwt-token");

            // Act
            LoginResponse response = authenticationService.guestLogin();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.token()).isEqualTo("guest-jwt-token");

            // Verify the claims passed to makeToken
            ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
            verify(jwtService).makeToken(eq(0L), claimsCaptor.capture());

            Map<String, Object> claims = claimsCaptor.getValue();
            assertThat(claims).containsEntry("username", "guest");

            @SuppressWarnings("unchecked")
            List<RoleDTO> roles = (List<RoleDTO>) claims.get("roles");
            assertThat(roles).hasSize(1);
            assertThat(roles.get(0).code()).isEqualTo("GUEST");
            assertThat(roles.get(0).name()).isEqualTo("Guest User");

            // Verify no login log for guests
            verifyNoInteractions(loginLogRepository);
        }
    }

    // ─── logout() Tests ────────────────────────────────────────────────

    @Nested
    @DisplayName("logout()")
    class LogoutTests {

        @Test
        @DisplayName("TC-AUTH-06: Logout updates logoutTime for active LoginLogs and revokes refresh token")
        void logout_updatesLogoutTimeForActiveLogins() {
            // Arrange
            LoginLog log1 = LoginLog.builder()
                .id(1L).username("testuser").loginTime(LocalDateTime.now().minusHours(2)).status(true).build();
            LoginLog log2 = LoginLog.builder()
                .id(2L).username("testuser").loginTime(LocalDateTime.now().minusHours(1)).status(true).build();

            when(loginLogRepository.findByUsernameAndLogoutTimeIsNullOrderByLoginTimeDesc("testuser"))
                .thenReturn(List.of(log1, log2));

            RefreshToken refreshToken = createMockRefreshToken(false, false);
            when(refreshTokenRepository.findByToken("refresh-token-value"))
                .thenReturn(Optional.of(refreshToken));

            // Act
            authenticationService.logout("testuser", "refresh-token-value");

            // Assert — refresh token was revoked
            verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
            assertThat(refreshTokenCaptor.getValue().getRevoked()).isTrue();

            // Assert — both login logs have logoutTime set
            ArgumentCaptor<List<LoginLog>> logsCaptor = ArgumentCaptor.forClass(List.class);
            verify(loginLogRepository).saveAll(logsCaptor.capture());
            List<LoginLog> savedLogs = logsCaptor.getValue();
            assertThat(savedLogs).hasSize(2);
            assertThat(savedLogs).allSatisfy(log ->
                assertThat(log.getLogoutTime()).isNotNull()
            );
        }

        @Test
        @DisplayName("Logout with null username only revokes refresh token")
        void logout_nullUsername_onlyRevokesRefreshToken() {
            // Arrange
            RefreshToken refreshToken = createMockRefreshToken(false, false);
            when(refreshTokenRepository.findByToken("refresh-token"))
                .thenReturn(Optional.of(refreshToken));

            // Act
            authenticationService.logout(null, "refresh-token");

            // Assert
            verify(refreshTokenRepository).save(any(RefreshToken.class));
            verify(loginLogRepository, never()).findByUsernameAndLogoutTimeIsNullOrderByLoginTimeDesc(anyString());
        }

        @Test
        @DisplayName("Logout with blank username skips login log update")
        void logout_blankUsername_skipsLoginLogUpdate() {
            // Act
            authenticationService.logout("", null);

            // Assert
            verify(loginLogRepository, never()).findByUsernameAndLogoutTimeIsNullOrderByLoginTimeDesc(anyString());
            verify(loginLogRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Logout with no active login logs does not call saveAll")
        void logout_noActiveLogins_doesNotCallSaveAll() {
            // Arrange
            when(loginLogRepository.findByUsernameAndLogoutTimeIsNullOrderByLoginTimeDesc("testuser"))
                .thenReturn(Collections.emptyList());

            // Act
            authenticationService.logout("testuser", null);

            // Assert
            verify(loginLogRepository, never()).saveAll(any());
        }
    }

    // ─── refreshToken() Tests ──────────────────────────────────────────

    @Nested
    @DisplayName("refreshToken()")
    class RefreshTokenTests {

        @Test
        @DisplayName("Valid refresh token returns new TokenPair and revokes old token")
        void refreshToken_validToken_returnsNewTokenPair() throws Exception {
            // Arrange
            RefreshToken storedToken = createMockRefreshToken(false, false);
            UserDTO userDTO = createMockUserDTO();
            UserAuthDetails authDetails = createMockUserAuthDetails(true, true);
            User userEntity = createMockUserEntity();

            when(refreshTokenRepository.findByToken("valid-refresh-token"))
                .thenReturn(Optional.of(storedToken));
            when(userService.getUserById(1L)).thenReturn(userDTO);
            when(userAuthDetailsMapper.userAuthDetailsFromUserDTO(userDTO)).thenReturn(authDetails);
            when(jwtService.makeToken(eq(1L), any(Map.class))).thenReturn("new-access-token");
            when(userRepository.getReferenceById(1L)).thenReturn(userEntity);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            TokenPair result = authenticationService.refreshToken("valid-refresh-token");

            // Assert
            assertThat(result.accessToken()).isEqualTo("new-access-token");
            assertThat(result.refreshToken()).isNotBlank();

            // Verify old token was revoked (first save call)
            verify(refreshTokenRepository).save(storedToken);
            assertThat(storedToken.getRevoked()).isTrue();
        }

        @Test
        @DisplayName("Revoked refresh token throws InvalidCredentialException")
        void refreshToken_revokedToken_throwsException() {
            // Arrange
            RefreshToken revokedToken = createMockRefreshToken(true, false);
            when(refreshTokenRepository.findByToken("revoked-token"))
                .thenReturn(Optional.of(revokedToken));

            // Act & Assert
            assertThatThrownBy(() -> authenticationService.refreshToken("revoked-token"))
                .isInstanceOf(InvalidCredentialException.class);
        }

        @Test
        @DisplayName("Expired refresh token throws InvalidCredentialException")
        void refreshToken_expiredToken_throwsException() {
            // Arrange
            RefreshToken expiredToken = createMockRefreshToken(false, true);
            when(refreshTokenRepository.findByToken("expired-token"))
                .thenReturn(Optional.of(expiredToken));

            // Act & Assert
            assertThatThrownBy(() -> authenticationService.refreshToken("expired-token"))
                .isInstanceOf(InvalidCredentialException.class);
        }

        @Test
        @DisplayName("Non-existent refresh token throws InvalidCredentialException")
        void refreshToken_notFound_throwsException() {
            // Arrange
            when(refreshTokenRepository.findByToken("nonexistent"))
                .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> authenticationService.refreshToken("nonexistent"))
                .isInstanceOf(InvalidCredentialException.class);
        }
    }

    // ─── loadUserByUsername() Tests ─────────────────────────────────────

    @Nested
    @DisplayName("loadUserByUsername()")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Returns UserAuthDetails for a valid username")
        void loadUserByUsername_validUser_returnsDetails() {
            // Arrange
            UserDTO userDTO = createMockUserDTO();
            UserAuthDetails expected = createMockUserAuthDetails(true, true);

            when(userService.getUserByUsername("testuser")).thenReturn(userDTO);
            when(userAuthDetailsMapper.userAuthDetailsFromUserDTO(userDTO)).thenReturn(expected);

            // Act
            UserAuthDetails result = authenticationService.loadUserByUsername("testuser");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(1L);
            assertThat(result.getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("Returns null when UserService returns null")
        void loadUserByUsername_nullUser_returnsNull() {
            // Arrange
            when(userService.getUserByUsername("unknown")).thenReturn(null);

            // Act
            UserAuthDetails result = authenticationService.loadUserByUsername("unknown");

            // Assert
            assertThat(result).isNull();
            verify(userAuthDetailsMapper, never()).userAuthDetailsFromUserDTO(any());
        }
    }
}
