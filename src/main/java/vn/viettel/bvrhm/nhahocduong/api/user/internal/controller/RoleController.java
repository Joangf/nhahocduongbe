package vn.viettel.bvrhm.nhahocduong.api.user.internal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: longlb1
 * @since: 19-Sep-23
 */

@RestController
@RequestMapping("/api")
public class RoleController {
    @GetMapping("/users/{id}/roles")
    List<Role> getRolesByUserId() {
        return new ArrayList<>();
    }

}
