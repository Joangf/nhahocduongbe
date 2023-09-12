package vn.viettel.bvrhm.nhahocduong.api.auth.internal;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.auth.JwtService;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginResponse;
import vn.viettel.bvrhm.nhahocduong.api.auth.exception.InvalidCredentialException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository.UserPasswordRepository;
import vn.viettel.bvrhm.nhahocduong.api.user.UserService;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthenticationService {

  private UserService userService;
  private JwtService jwtService;
  private UserPasswordRepository userPasswordRepository;
  private PasswordEncoder passwordEncoder;

  public LoginResponse authenticate(LoginRequest loginRequest) throws InvalidCredentialException {
    String loginName = loginRequest.loginName();
    // TODO expand logic to allow login with email, phoneNumber, security key, etc...
    String username = loginName;
    Long userId = userService.getUserIdFromUsername(username);
    String password = loginRequest.password();

    if (!userService.checkValidUserIdPassword(userId, password)) {
      throw new InvalidCredentialException();
    }

    // FIXME query user roles
    List<String> userRoles = List.of("a", "b", "c");
    String token = jwtService.makeTokenWithUserIdAndRoles(userId, userRoles);

    return new LoginResponse(token);
  }

  //  public boolean createPasswordForUser(Long userId, String inputPassword) {
  //    String newEncodedPassword = passwordEncoder.encode(inputPassword);
  //    UserPassword newUserPasswordEntity = new UserPassword(null, userId, newEncodedPassword);
  //    UserPassword createdUserPassword = userPasswordRepository.save(newUserPasswordEntity);
  //    return true; // Create password OK
  //  }
}
