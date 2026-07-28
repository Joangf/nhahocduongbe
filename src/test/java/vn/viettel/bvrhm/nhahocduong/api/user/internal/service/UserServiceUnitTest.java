package vn.viettel.bvrhm.nhahocduong.api.user.internal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.NotificationService;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.UserDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.User;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.Role;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.mapper.UserMapper;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.RoleRepository;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.UserRepository;

@DisplayName("UserService — Unit Tests")
@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

  @Mock PasswordEncoder passwordEncoder;
  @Mock UserRepository userRepository;
  @Mock UserMapper userMapper;
  @Mock RoleRepository roleRepository;
  @Mock NotificationService notificationService;

  @InjectMocks UserService service;

  // ─── Helpers ───────────────────────────────────────────────────────────────

  private UserDTO createUserDTO(String username, String password) {
    return new UserDTO(null, username, password, "Test", "User",
        "test@test.com", "0909000001", null, null, null, false, true, null);
  }

  private User createUserEntity(Long id, String username) {
    User u = new User();
    u.setId(id);
    u.setUsername(username);
    u.setFirstName("Test");
    u.setLastName("User");
    u.setPassword("encodedPass");
    u.setEmail("test@test.com");
    u.setRegisterStatus(false);
    u.setStatus(true);
    return u;
  }

  // ─── TC-01: createUser — Happy Path ────────────────────────────────────────

  @Nested
  @DisplayName("TC-01 createUser() — Happy Path")
  class CreateUserHappy {

    @Test
    @DisplayName("Tạo user thành công và gửi thông báo cho admin")
    void shouldCreateUserAndNotifyAdmins() throws Exception {
      UserDTO inputDto = createUserDTO("newuser", "StrongP@ss1");
      User entity = createUserEntity(null, "newuser");
      User savedEntity = createUserEntity(10L, "newuser");
      UserDTO savedDto = createUserDTO("newuser", "encodedPass");
      User admin = createUserEntity(1L, "admin");

      when(userRepository.getByUsername("newuser")).thenReturn(Optional.empty());
      when(userMapper.userFromUserDTO(inputDto)).thenReturn(entity);
      when(passwordEncoder.encode("StrongP@ss1")).thenReturn("encodedPass");
      when(userRepository.save(entity)).thenReturn(savedEntity);
      when(userMapper.userDTOFromUser(savedEntity)).thenReturn(savedDto);
      when(userRepository.findUsersByRoleCode("ADMIN")).thenReturn(List.of(admin));

      UserDTO result = service.createUser(inputDto);

      assertThat(result).isNotNull();
      verify(userRepository).save(argThat(u ->
          u.getRegisterStatus() == false && u.getStatus() == true));
      // Phải gửi thông báo cho từng admin
      verify(notificationService).createNotificationForAdmin(
          eq(1L), contains("Tài khoản mới cần duyệt"), contains("newuser"));
    }

    @Test
    @DisplayName("Không gửi thông báo nếu không có admin")
    void shouldNotNotifyWhenNoAdmins() throws Exception {
      UserDTO inputDto = createUserDTO("newuser", "StrongP@ss1");
      User entity = createUserEntity(null, "newuser");
      User savedEntity = createUserEntity(10L, "newuser");
      UserDTO savedDto = createUserDTO("newuser", "encodedPass");

      when(userRepository.getByUsername("newuser")).thenReturn(Optional.empty());
      when(userMapper.userFromUserDTO(inputDto)).thenReturn(entity);
      when(passwordEncoder.encode("StrongP@ss1")).thenReturn("encodedPass");
      when(userRepository.save(entity)).thenReturn(savedEntity);
      when(userMapper.userDTOFromUser(savedEntity)).thenReturn(savedDto);
      when(userRepository.findUsersByRoleCode("ADMIN")).thenReturn(Collections.emptyList());

      UserDTO result = service.createUser(inputDto);

      assertThat(result).isNotNull();
      verify(notificationService, never()).createNotificationForAdmin(anyLong(), anyString(), anyString());
    }
  }

  // ─── TC-02: createUser — Validation ────────────────────────────────────────

  @Nested
  @DisplayName("TC-02 createUser() — Validation")
  class CreateUserValidation {

    @Test
    @DisplayName("Ném 409 CONFLICT khi username đã tồn tại")
    void shouldThrowConflictWhenUsernameExists() {
      UserDTO inputDto = createUserDTO("existing", "StrongP@ss1");

      when(userRepository.getByUsername("existing")).thenReturn(Optional.of(new User()));

      assertThatThrownBy(() -> service.createUser(inputDto))
          .isInstanceOf(ResponseStatusException.class)
          .extracting("status")
          .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Ném 400 BAD_REQUEST khi password yếu")
    void shouldThrowBadRequestWhenWeakPassword() {
      UserDTO inputDto = createUserDTO("newuser", "123"); // quá ngắn

      when(userRepository.getByUsername("newuser")).thenReturn(Optional.empty());
      when(userMapper.userFromUserDTO(inputDto)).thenReturn(new User());

      assertThatThrownBy(() -> service.createUser(inputDto))
          .isInstanceOf(ResponseStatusException.class)
          .extracting("status")
          .isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  // ─── TC-03: approveUser / lockUser / unlockUser ────────────────────────────

  @Nested
  @DisplayName("TC-03 Account management")
  class AccountManagement {

    @Test
    @DisplayName("approveUser: set registerStatus=true, status=true")
    void shouldApproveUser() {
      User user = createUserEntity(1L, "test");
      when(userRepository.getReferenceById(1L)).thenReturn(user);
      when(userRepository.save(user)).thenReturn(user);
      when(userMapper.userDTOFromUser(user)).thenReturn(createUserDTO("test", "x"));

      UserDTO result = service.approveUser(1L);

      assertThat(user.getRegisterStatus()).isTrue();
      assertThat(user.getStatus()).isTrue();
      verify(userRepository).save(user);
    }

    @Test
    @DisplayName("lockUser: set status=false")
    void shouldLockUser() {
      User user = createUserEntity(1L, "test");
      when(userRepository.getReferenceById(1L)).thenReturn(user);

      service.lockUser(1L);

      assertThat(user.getStatus()).isFalse();
      verify(userRepository).save(user);
    }

    @Test
    @DisplayName("unlockUser: set status=true")
    void shouldUnlockUser() {
      User user = createUserEntity(1L, "test");
      user.setStatus(false);
      when(userRepository.getReferenceById(1L)).thenReturn(user);

      service.unlockUser(1L);

      assertThat(user.getStatus()).isTrue();
      verify(userRepository).save(user);
    }
  }
}
