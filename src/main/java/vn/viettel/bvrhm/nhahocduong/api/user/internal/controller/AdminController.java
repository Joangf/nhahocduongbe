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

  @GetMapping("/waiting")
  public List<UserDTO> getWaitingUsers() {
    log.info("REST request to get waiting users");
    return userService.getWaitingUsers();
  }
}
