// package vn.viettel.bvrhm.nhahocduong.api.auth.internal.controller;

// import static java.util.Objects.nonNull;

// import jakarta.transaction.Transactional;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import vn.viettel.bvrhm.nhahocduong.api.auth.LoginRequest;
// import vn.viettel.bvrhm.nhahocduong.api.auth.LoginResponse;
// import vn.viettel.bvrhm.nhahocduong.api.auth.exception.InvalidCredentialException;
// import vn.viettel.bvrhm.nhahocduong.api.auth.internal.mapper.UserAuthDetailsMapper;
// import vn.viettel.bvrhm.nhahocduong.api.auth.internal.object.UserAuthDetails;
// import vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository.UserPasswordRepository;
// import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
// import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;
// import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.UserDTO;
// import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.RoleService;
// import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.UserService;

// @RestController
// public class AuthenticationController implements UserDetailsService {
//   @Autowired private UserService userService;
//   @Autowired private RoleService roleService;
//   @Autowired private JwtService jwtService;
//   @Autowired private UserAuthDetailsMapper userAuthDetailsMapper;

//   private UserPasswordRepository userPasswordRepository;
//   private PasswordEncoder passwordEncoder;

//   public LoginResponse authenticate(LoginRequest loginRequest) throws InvalidCredentialException {
//     String username = loginRequest.username();
//     // TODO expand logic to allow login with email, phoneNumber, security key, etc...

//     // Check username
//     UserAuthDetails userAuthDetails;
//     try {
//       userAuthDetails = loadUserByUsername(username);
//     } catch (Exception e) {
//       throw new InvalidCredentialException();
//     }

//     // Check register status (tài khoản phải được admin duyệt)
//     if (userAuthDetails.getRegisterStatus() == null || !userAuthDetails.getRegisterStatus()) {
//       throw new InvalidCredentialException();
//     }

//     // Verify password
//     String password = loginRequest.password();
//     if (!userService.checkValidUserIdPassword(userAuthDetails.getUserId(), password)) {
//       throw new InvalidCredentialException();
//     }

//     // Get role
//     List<RoleDTO> userRoles = roleService.getActiveRoleByUsername(username);

//     // Get organization
//     OrganizationDTO organization = userService.getUserByUsername(username).organization();

//     Map<String, Object> claims = new LinkedHashMap<>();
//     claims.put("roles", userRoles);
//     claims.put("username", userAuthDetails.getUsername());
//     if (nonNull(organization)) {
//       claims.put("organization", organization);
//     }

//     String token = jwtService.makeToken(userAuthDetails.getUserId(), claims);

//     return new LoginResponse(token);
//   }

//   @PostMapping("/guest-login")
//   public LoginResponse guestLogin() {
//     return authenticationService.guestLogin();
//   }

//   private void logFailedLogin(String username) {
//       try {
//           String ip = request.getRemoteAddr();
//           loginLogRepository.save(vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.LoginLog.builder()
//               .username(username)
//               .ipAddress(ip)
//               .loginTime(java.time.LocalDateTime.now())
//               .status("FAILED")
//               .build());
//       } catch (Exception e) {
//           // ignore
//       }
//   }

//   private void logSuccessLogin(String username) {
//       try {
//           String ip = request.getRemoteAddr();
//           loginLogRepository.save(vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.LoginLog.builder()
//               .username(username)
//               .ipAddress(ip)
//               .loginTime(java.time.LocalDateTime.now())
//               .status("SUCCESS")
//               .build());
//       } catch (Exception e) {
//           // ignore
//       }
//   }

//   @Override
//   @Transactional()
//   public UserAuthDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//     UserDTO userDTO = userService.getUserByUsername(username);
//     return userDTO == null ? null : userAuthDetailsMapper.userAuthDetailsFromUserDTO(userDTO);
//   }

//   //  public boolean createPasswordForUser(Long userId, String inputPassword) {
//   //    String newEncodedPassword = passwordEncoder.encode(inputPassword);
//   //    UserPassword newUserPasswordEntity = new UserPassword(null, userId, newEncodedPassword);
//   //    UserPassword createdUserPassword = userPasswordRepository.save(newUserPasswordEntity);
//   //    return true; // Create password OK
//   //  }
// }

package vn.viettel.bvrhm.nhahocduong.api.auth.internal.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginResponse;
import vn.viettel.bvrhm.nhahocduong.api.auth.exception.InvalidCredentialException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthenticationService;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.JwtService;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthenticationController {

  @Autowired AuthenticationService authenticationService;
  @Autowired JwtService jwtService;
  @Autowired HttpServletRequest request;

  @PostMapping("/login")
  public org.springframework.http.ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    try {
      LoginResponse loginResponse = authenticationService.authenticate(loginRequest);
      return org.springframework.http.ResponseEntity.ok(loginResponse);
    } catch (InvalidCredentialException e) {
      return org.springframework.http.ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(java.util.Map.of("error", "Tên đăng nhập hoặc mật khẩu không đúng"));
    }
  }

  @PostMapping("/guest-login")
  public LoginResponse guestLogin() {
    return authenticationService.guestLogin();
  }

  @PostMapping("/logout")
  public org.springframework.http.ResponseEntity<?> logout() {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      if (jwtService.isTokenValid(token)) {
        String username = jwtService.extractUsername(token);
        authenticationService.logout(username);
      }
    }
    return org.springframework.http.ResponseEntity.ok().build();
  }
}