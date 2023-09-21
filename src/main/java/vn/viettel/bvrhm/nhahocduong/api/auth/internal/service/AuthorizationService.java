package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.User;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.UserRepository;

/**
 * @author: longlb1
 * @since: 18-Sep-23
 */
@Service
public class AuthorizationService {

    @Data
    public class AuthorizationData {
        private String areaCode;
        private Long organizationId;
    }

    @Autowired
    UserRepository userRepository;

    public AuthorizationData authorize() {
        // TODO: Optimize author
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getPrincipal();
        User user = userRepository.getReferenceById(Long.parseLong(userId));

        AuthorizationData data = new AuthorizationData();
        if (user.getOrganization() != null) {
            switch (user.getOrganization().getType()) {
                case SCHOOL -> data.setOrganizationId(user.getOrganization().getId());
                case DEPARTMENT -> data.setAreaCode(user.getOrganization().getAreaCode());
            }
        }

        return data;
    }
}
