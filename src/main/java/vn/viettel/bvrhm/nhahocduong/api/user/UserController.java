package vn.viettel.bvrhm.nhahocduong.api.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired UserService userService;
  private Logger log = LoggerFactory.getLogger(UserController.class);

  @PostMapping("/register")
  UserDTO createUser(@RequestBody UserDTO newUserDTO) {
    UserDTO createdUser = null;
    try {
      createdUser = userService.createUser(newUserDTO);
    } catch (Exception e) {
      e.printStackTrace();
      log.error("Error creating new user");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return createdUser;
  }

  @RequestMapping("/hello")
  String hello() {
    return "Hello";
  }
}
