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

@RestController
@RequestMapping("/api/user")
public class UserController {

  private final Logger log = LoggerFactory.getLogger(UserController.class);
  @Autowired UserService userService;

  @PostMapping("/register")
  UserDTO createUser(@RequestBody UserDTO newUserDTO) throws Exception {
    UserDTO createdUser = userService.createUser(newUserDTO);
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
