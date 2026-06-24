package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import static java.util.Objects.nonNull;

import jakarta.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginResponse;
import vn.viettel.bvrhm.nhahocduong.api.auth.exception.InvalidCredentialException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.constants.enums.Status;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.mapper.UserAuthDetailsMapper;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.object.UserAuthDetails;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository.UserPasswordRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.UserDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.RoleService;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.UserService;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository.LoginLogRepository;

@Service
public class AuthenticationService implements UserDetailsService {
  @Autowired private UserService userService;
  @Autowired private RoleService roleService;
  @Autowired private JwtService jwtService;
  @Autowired private UserAuthDetailsMapper userAuthDetailsMapper;

  private UserPasswordRepository userPasswordRepository;
  private PasswordEncoder passwordEncoder;

  @Autowired private LoginLogRepository loginLogRepository;
  @Autowired private jakarta.servlet.http.HttpServletRequest request;

  public LoginResponse authenticate(LoginRequest loginRequest) throws InvalidCredentialException {
    String username = loginRequest.username();
    // TODO expand logic to allow login with email, phoneNumber, security key, etc...

    // Check username
    UserAuthDetails userAuthDetails;
    try {
      userAuthDetails = loadUserByUsername(username);
      if (userAuthDetails == null) {
        throw new InvalidCredentialException();
      }
    } catch (Exception e) {
      throw new InvalidCredentialException();
    }

    // Check status (tài khoản không bị khóa)
    if (!userAuthDetails.isEnabled()) {
      throw new InvalidCredentialException();
    }

    // Check register status (tài khoản phải được admin duyệt)
    if (userAuthDetails.getRegisterStatus() == null || !userAuthDetails.getRegisterStatus()) {
      throw new InvalidCredentialException();
    }

    // Verify password
    String password = loginRequest.password();
    if (!userService.checkValidUserIdPassword(userAuthDetails.getUserId(), password)) {
      throw new InvalidCredentialException();
    }

    // Get role
    List<RoleDTO> userRoles = roleService.getActiveRoleByUsername(username);

    // Get organization
    OrganizationDTO organization = userService.getUserByUsername(username).organization();

    Map<String, Object> claims = new LinkedHashMap<>();
    claims.put("roles", userRoles);
    claims.put("username", userAuthDetails.getUsername());
    if (nonNull(organization)) {
      claims.put("organization", organization);
    }

    String token = jwtService.makeToken(userAuthDetails.getUserId(), claims);

    logSuccessLogin(username);
    return new LoginResponse(token);
  }

  public LoginResponse guestLogin() {
    Map<String, Object> claims = new LinkedHashMap<>();
    claims.put("roles", List.of(new RoleDTO("0", "GUEST", "Guest User", true, "Guest Access Role")));
    claims.put("username", "guest");

    String token = jwtService.makeToken(0L, claims);

    return new LoginResponse(token);
  }

  private String getPhoneNumberByUsername(String username) {
      try {
          UserDTO user = userService.getUserByUsername(username);
          return user != null ? user.phoneNumber() : null;
      } catch (Exception e) {
          return null;
      }
  }

  private void logSuccessLogin(String username) {
      try {
          loginLogRepository.save(vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.LoginLog.builder()
              .username(username)
              .loginTime(java.time.LocalDateTime.now())
              .status(Status.SUCCESS.getValue())
              .phoneNumber(getPhoneNumberByUsername(username))
              .build());
      } catch (Exception e) {
          // ignore — không để lỗi ghi log làm hỏng login
      }
  }

  public void logout(String username) {
      List<vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.LoginLog> activeLogins =
          loginLogRepository.findByUsernameAndLogoutTimeIsNullOrderByLoginTimeDesc(username);
      if (!activeLogins.isEmpty()) {
          vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.LoginLog loginLog = activeLogins.get(0);
          loginLog.setLogoutTime(java.time.LocalDateTime.now());
          loginLogRepository.save(loginLog);
      }
  }

  @Override
  @Transactional()
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