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
      logFailedLogin(username);
      throw new InvalidCredentialException();
    }

    // Check status (tài khoản không bị khóa)
    if (!userAuthDetails.isEnabled()) {
      logFailedLogin(username);
      throw new InvalidCredentialException();
    }

    // Check register status (tài khoản phải được admin duyệt)
    if (userAuthDetails.getRegisterStatus() == null || !userAuthDetails.getRegisterStatus()) {
      logFailedLogin(username);
      throw new InvalidCredentialException();
    }

    // Verify password
    String password = loginRequest.password();
    if (!userService.checkValidUserIdPassword(userAuthDetails.getUserId(), password)) {
      logFailedLogin(username);
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

  private void logFailedLogin(String username) {
      try {
          String ip = request.getRemoteAddr();
          loginLogRepository.save(vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.LoginLog.builder()
              .username(username)
              .loginTime(java.time.LocalDateTime.now())
              .status(Status.FAILED.getValue())
              .build());
      } catch (Exception e) {
          // ignore
      }
  }

  private void logSuccessLogin(String username) {
        loginLogRepository.save(vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.LoginLog.builder()
            .username(username)
            .loginTime(java.time.LocalDateTime.now())
            .status(Status.SUCCESS.getValue())
            .build());
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
