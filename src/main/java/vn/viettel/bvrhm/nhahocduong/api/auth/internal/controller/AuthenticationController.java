package vn.viettel.bvrhm.nhahocduong.api.auth.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.LoginResponse;
import vn.viettel.bvrhm.nhahocduong.api.auth.exception.InvalidCredentialException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthenticationService;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthenticationController {

  @Autowired AuthenticationService authenticationService;

  @PostMapping("/login")
  public LoginResponse login(@RequestBody LoginRequest loginRequest) {
    try {
      LoginResponse loginResponse = authenticationService.authenticate(loginRequest);
      return loginResponse;
    } catch (InvalidCredentialException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }

  @PostMapping("/guest-login")
  public LoginResponse guestLogin() {
    return authenticationService.guestLogin();
  }
}
