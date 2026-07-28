package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthorizationService.AuthorizationData;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.OrganizationType;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.User;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.UserRepository;

@DisplayName("AuthorizationService — Unit Tests")
@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

  @Mock UserRepository userRepository;
  @Mock Authentication authentication;
  @Mock SecurityContext securityContext;
  @InjectMocks AuthorizationService service;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.setContext(securityContext);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Trả về data trống khi user là anonymousUser")
  void shouldReturnEmptyForAnonymous() {
    lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn("anonymousUser");

    AuthorizationData data = service.authorize();

    assertThat(data.getOrganizationId()).isNull();
    assertThat(data.getAreaCode()).isNull();
  }

  @Test
  @DisplayName("Set organizationId khi user thuộc SCHOOL")
  void shouldSetOrgIdForSchoolUser() {
    Organization org = new Organization();
    org.setId(10L);
    org.setType(OrganizationType.SCHOOL);
    User user = new User();
    user.setOrganization(org);

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn("5");
    when(userRepository.findByIdWithOrganization(5L)).thenReturn(Optional.of(user));

    AuthorizationData data = service.authorize();

    assertThat(data.getOrganizationId()).isEqualTo(10L);
  }

  @Test
  @DisplayName("Không set organizationId khi user không có organization")
  void shouldNotSetOrgIdWhenNoOrg() {
    User user = new User();

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn("5");
    when(userRepository.findByIdWithOrganization(5L)).thenReturn(Optional.of(user));

    AuthorizationData data = service.authorize();

    assertThat(data.getOrganizationId()).isNull();
  }
}
