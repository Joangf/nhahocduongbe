package vn.viettel.bvrhm.nhahocduong.api.user.internal.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.UserDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.UserService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final Logger log = LoggerFactory.getLogger(AdminController.class);

  @Autowired 
  private UserService userService;

  @Autowired
  private vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository.LoginLogRepository loginLogRepository;

  @GetMapping("/waiting")
  public List<UserDTO> getWaitingUsers() {
    log.info("REST request to get waiting users");
    return userService.getWaitingUsers();
  }

  @GetMapping("/users")
  public List<UserDTO> getAllUsers() {
    return userService.getAllUsers();
  }

  @PutMapping("/users/{id}/lock")
  public void lockUser(@org.springframework.web.bind.annotation.PathVariable Long id) {
    userService.lockUser(id);
  }

  @PutMapping("/users/{id}/unlock")
  public void unlockUser(@org.springframework.web.bind.annotation.PathVariable Long id) {
    userService.unlockUser(id);
  }

  @GetMapping("/login-logs")
  public org.springframework.http.ResponseEntity<?> getLoginLogs() {
      log.info("REST request to get login logs");
      return org.springframework.http.ResponseEntity.ok(loginLogRepository.findByUsernameNotOrderByLoginTimeDesc("guest"));
  }
}
