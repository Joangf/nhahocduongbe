package vn.viettel.bvrhm.nhahocduong.api.user.internal.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.UserDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;


import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.OtpService;

@RestController
@RequestMapping("/api/user")
public class UserController {

  private final Logger log = LoggerFactory.getLogger(UserController.class);
  @Autowired UserService userService;
  @Autowired OtpService otpService;

  @GetMapping("/{id}")
  public UserDTO getUserById(@PathVariable Long id) {
      return this.userService.getUserById(id);
  }
  

  @PostMapping("/register")
  UserDTO createUser(
      @RequestBody UserDTO newUserDTO,
      @RequestParam("token") String token
  ) throws Exception {
    // Xác thực OTP token và kiểm tra email trùng khớp
    String verifiedEmail = otpService.validateResetToken(token);
    if (!verifiedEmail.equalsIgnoreCase(newUserDTO.email())) {
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.BAD_REQUEST,
          "Email đăng ký không khớp với email đã xác thực OTP."
      );
    }

    UserDTO createdUser = userService.createUser(newUserDTO);

    // Đánh dấu token đã được sử dụng
    otpService.markTokenAsUsed(token);

    return createdUser;
  }

  @GetMapping("/waiting")
  List<UserDTO> getWaitingUsers() {
    return userService.getWaitingUsers();
  }

  @PutMapping("/{id}/approve")
  UserDTO approveUser(@PathVariable Long id) {
    return userService.approveUser(id);
  }

  @DeleteMapping("/{id}/reject")
  void rejectUser(@PathVariable Long id) {
    userService.rejectUser(id);
  }

  @RequestMapping("/hello")
  String hello() {
    return "Hello";
  }
}
