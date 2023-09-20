package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginResponse;
import vn.viettel.bvrhm.nhahocduong.api.auth.exception.InvalidCredentialException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.object.UserAuthDetails;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.mapper.UserAuthDetailsMapper;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository.UserPasswordRepository;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.UserDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.RoleService;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class AuthenticationService implements UserDetailsService {
  @Autowired private UserService userService;
  @Autowired private RoleService roleService;
  @Autowired private JwtService jwtService;
  @Autowired private UserAuthDetailsMapper userAuthDetailsMapper;

  private UserPasswordRepository userPasswordRepository;
  private PasswordEncoder passwordEncoder;

  public LoginResponse authenticate(LoginRequest loginRequest) throws InvalidCredentialException {
    String username = loginRequest.username();
    // TODO expand logic to allow login with email, phoneNumber, security key, etc...
//    String username = username;
//    Long userId = userService.getUserIdFromUsername(username);
    UserAuthDetails userAuthDetails;
    try {
      userAuthDetails = loadUserByUsername(username);
    } catch (Exception e) {
      throw new InvalidCredentialException();
    }

    String password = loginRequest.password();

    if (!userService.checkValidUserIdPassword(userAuthDetails.getUserId(), password)) {
      throw new InvalidCredentialException();
    }

    List<RoleDTO> userRoles = roleService.getActiveRoleByUsername(username);
//    String token = jwtService.makeTokenWithUserIdAndRoles(userDTO.id(), userRoles);
    String token = jwtService.makeToken(userAuthDetails.getUserId(),
                                        Map.of("roles", userRoles,
                                               "username", userAuthDetails.getUsername()));

    return new LoginResponse(token);
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
