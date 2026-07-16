package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.UserRepository;

/**
 * @author: longlb1
 * @since: 18-Sep-23
 */
@Service
public class AuthorizationService {

  @Autowired UserRepository userRepository;

  public AuthorizationData authorize() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) authentication.getPrincipal();

    AuthorizationData data = new AuthorizationData();
    if ("anonymousUser".equals(userId)) {
      return data;
    }

    // Single query with JOIN FETCH to avoid N+1 (user + organization in one roundtrip)
    userRepository.findByIdWithOrganization(Long.parseLong(userId))
        .ifPresent(user -> {
          if (user.getOrganization() != null) {
            // Only SCHOOL accounts are restricted to their own organization's data.
            // DEPARTMENT, MINISTRY, HCMC, and other roles see all data.
            if (user.getOrganization().getType() ==
                vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums
                    .OrganizationType.SCHOOL) {
              data.setOrganizationId(user.getOrganization().getId());
            }
          }
        });

    return data;
  }

  @Data
  public static class AuthorizationData {
    private String areaCode;
    private Long organizationId;
  }
}
