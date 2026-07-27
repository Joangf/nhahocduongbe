package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import static java.util.Objects.nonNull;

import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginResponse;
import vn.viettel.bvrhm.nhahocduong.api.auth.exception.InvalidCredentialException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.constants.enums.Status;
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
import vn.viettel.bvrhm.nhahocduong.api.auth.TokenPair;

@Service
public class AuthenticationService implements UserDetailsService {
  private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(30);

  @Autowired private UserService userService;
  @Autowired private UserRepository userRepository;
  @Autowired private JwtService jwtService;
  @Autowired private UserAuthDetailsMapper userAuthDetailsMapper;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private LoginLogRepository loginLogRepository;
  @Autowired private RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public TokenPair authenticate(LoginRequest loginRequest) throws InvalidCredentialException {
    String username = loginRequest.username();

    // Query 1: load user once — includes roles, organization, password, phoneNumber
    UserDTO userDTO;
    try {
      userDTO = userService.getUserByUsername(username);
      if (userDTO == null) {
        throw new InvalidCredentialException();
      }
    } catch (Exception e) {
      throw new InvalidCredentialException();
    }

    UserAuthDetails userAuthDetails = userAuthDetailsMapper.userAuthDetailsFromUserDTO(userDTO);

    // Check status (tài khoản không bị khóa) — no DB
    if (!userAuthDetails.isEnabled()) {
      throw new InvalidCredentialException();
    }

    // Check register status (tài khoản phải được admin duyệt) — no DB
    if (userAuthDetails.getRegisterStatus() == null || !userAuthDetails.getRegisterStatus()) {
      throw new InvalidCredentialException();
    }

    // Verify password — requires DB query for password hash (not in DTO)
    if (!userService.checkValidUserIdPassword(userDTO.id(), loginRequest.password())) {
      throw new InvalidCredentialException();
    }

    // Get roles from DTO — already fetched, filter in memory
    List<RoleDTO> userRoles = userDTO.roleList() != null
        ? userDTO.roleList().stream().filter(RoleDTO::status).toList()
        : List.of();

    // Get organization from DTO — already fetched
    OrganizationDTO organization = userDTO.organization();

    Map<String, Object> claims = new LinkedHashMap<>();
    claims.put("roles", userRoles);
    claims.put("username", userAuthDetails.getUsername());
    if (nonNull(organization)) {
      claims.put("organization", organization);
    }

    String token = jwtService.makeToken(userAuthDetails.getUserId(), claims);
    String refreshToken = createRefreshToken(userDTO.id());
    // Query 2: insert login log — pass phoneNumber from DTO, no extra query
    logSuccessLogin(username, userDTO.phoneNumber());
    return new TokenPair(token, refreshToken);
  }

  public TokenPair refreshToken(String refreshTokenValue) throws InvalidCredentialException {
    RefreshToken storedRefreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
        .filter(refreshToken -> Boolean.FALSE.equals(refreshToken.getRevoked()))
        .filter(refreshToken -> refreshToken.getExpiresAt().isAfter(LocalDateTime.now()))
        .orElseThrow(InvalidCredentialException::new);

    User user = storedRefreshToken.getUser();
    UserDTO userDTO = userService.getUserById(user.getId());
    UserAuthDetails userAuthDetails = userAuthDetailsMapper.userAuthDetailsFromUserDTO(userDTO);

    if (!userAuthDetails.isEnabled()) {
      throw new InvalidCredentialException();
    }

    if (userAuthDetails.getRegisterStatus() == null || !userAuthDetails.getRegisterStatus()) {
      throw new InvalidCredentialException();
    }

    storedRefreshToken.setRevoked(true);
    refreshTokenRepository.save(storedRefreshToken);

    Map<String, Object> claims = new LinkedHashMap<>();
    List<RoleDTO> userRoles = userDTO.roleList() != null
        ? userDTO.roleList().stream().filter(RoleDTO::status).toList()
        : List.of();
    claims.put("roles", userRoles);
    claims.put("username", userAuthDetails.getUsername());
    OrganizationDTO organization = userDTO.organization();
    if (nonNull(organization)) {
      claims.put("organization", organization);
    }

    String accessToken = jwtService.makeToken(userAuthDetails.getUserId(), claims);
    String rotatedRefreshToken = createRefreshToken(userDTO.id());

    return new TokenPair(accessToken, rotatedRefreshToken);
  }

  public LoginResponse guestLogin() {
    Map<String, Object> claims = new LinkedHashMap<>();
    claims.put("roles", List.of(new RoleDTO("0", "GUEST", "Guest User", true, "Guest Access Role")));
    claims.put("username", "guest");

    String token = jwtService.makeToken(0L, claims);

    return new LoginResponse(token);
  }

  private String createRefreshToken(Long userId) {
    User user = userRepository.getReferenceById(userId);
    String refreshTokenValue = UUID.randomUUID().toString();
    refreshTokenRepository.save(
        RefreshToken.builder()
            .user(user)
            .token(refreshTokenValue)
            .expiresAt(LocalDateTime.now().plus(REFRESH_TOKEN_TTL))
            .revoked(false)
            .createdAt(LocalDateTime.now())
            .build());
    return refreshTokenValue;
  }

  private void logSuccessLogin(String username, String phoneNumber) {
      try {
          loginLogRepository.save(vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.LoginLog.builder()
              .username(username)
              .loginTime(java.time.LocalDateTime.now())
              .status(Status.SUCCESS.getValue())
              .phoneNumber(phoneNumber)
              .build());
      } catch (Exception e) {
          // ignore — không để lỗi ghi log làm hỏng login
      }
  }

    public void logout(String username, String refreshTokenValue) {
      if (refreshTokenValue != null && !refreshTokenValue.isBlank()) {
        refreshTokenRepository.findByToken(refreshTokenValue).ifPresent(token -> {
          token.setRevoked(true);
          refreshTokenRepository.save(token);
        });
      }

      if (username == null || username.isBlank()) {
        return;
      }

      List<vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.LoginLog> activeLogins =
        loginLogRepository.findByUsernameAndLogoutTimeIsNullOrderByLoginTimeDesc(username);
      java.time.LocalDateTime now = java.time.LocalDateTime.now();
      for (vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.LoginLog loginLog : activeLogins) {
          loginLog.setLogoutTime(now);
      }
      if (!activeLogins.isEmpty()) {
          loginLogRepository.saveAll(activeLogins);
      }
  }

  @Override
  @Transactional()
  @Cacheable(value = "userAuthDetails", key = "#username")
  public UserAuthDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDTO userDTO = userService.getUserByUsername(username);
    return userDTO == null ? null : userAuthDetailsMapper.userAuthDetailsFromUserDTO(userDTO);
  }

  //  public boolean createPasswordForUser(Long userId, String inputPassword) {
  //    String newEncodedPassword = passwordEncoder.encode(inputPassword);
  //    UserPassword newUserPasswordEntity = new UserPassword(null, userId, newEncodedPassword);
  //    UserPassword createdUserPassword = userPasswordRepository.save(newUserPasswordEntity);
  //    return true; // Create password OK
  //  }
}