package vn.viettel.bvrhm.nhahocduong.api.user.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.ResponseMessage;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.NotificationService;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.UserDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.Role;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.User;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.mapper.UserMapper;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.RoleRepository;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UserRepository userRepository;
  @Mock private UserMapper userMapper;
  @Mock private RoleRepository roleRepository;
  @Mock private NotificationService notificationService;

  @InjectMocks private UserService userService;

  private User createMockUser(Long id, String username, boolean registerStatus, boolean status) {
    User user = new User();
    user.setId(id);
    user.setUsername(username);
    user.setPassword("encoded_secret");
    user.setFirstName("An");
    user.setLastName("Nguyen");
    user.setRegisterStatus(registerStatus);
    user.setStatus(status);
    return user;
  }

  private UserDTO createMockUserDTO(String username, String password, List<RoleDTO> roles) {
    return new UserDTO(
        null,
        username,
        password,
        "An",
        "Nguyen",
        "an@nhahocduong.vn",
        "0901234567",
        null,
        roles,
        null,
        false,
        true,
        null
    );
  }

  @Nested
  @DisplayName("createUser() — Registration & Approval Workflow Tests (TC-REG-01, TC-REG-02)")
  class CreateUserTests {

    // TODO
    // @Test
    // @DisplayName("TC-REG-01: Valid UserDTO — creates user with registerStatus=false and notifies admins")
    // void createUser_validDto_createsUserAndNotifiesAdmins() throws Exception {
    //   // Arrange
    //   RoleDTO roleDTO = new RoleDTO("1", "DENTIST", "Dentist", true, null);
    //   UserDTO inputDto = createMockUserDTO("dentist1", "StrongPass@123", List.of(roleDTO));

    //   User mappedUser = createMockUser(null, "dentist1", false, true);
    //   Role mockRole = new Role();
    //   mockRole.setId(1L);
    //   mockRole.setCode("DENTIST");

    //   User savedUser = createMockUser(100L, "dentist1", false, true);
    //   UserDTO expectedDto = createMockUserDTO("dentist1", "encoded", List.of(roleDTO));

    //   User adminUser = createMockUser(1L, "admin", true, true);

    //   when(userRepository.getByUsername("dentist1")).thenReturn(Optional.empty());
    //   when(userMapper.userFromUserDTO(inputDto)).thenReturn(mappedUser);
    //   when(roleRepository.getReferenceById(1L)).thenReturn(mockRole);
    //   when(passwordEncoder.encode("StrongPass@123")).thenReturn("hashed_password");
    //   when(userRepository.save(mappedUser)).thenReturn(savedUser);
    //   when(userMapper.userDTOFromUser(savedUser)).thenReturn(expectedDto);
    //   when(userRepository.findUsersByRoleCode("ADMIN")).thenReturn(List.of(adminUser));

    //   // Act
    //   UserDTO result = userService.createUser(inputDto);

    //   // Assert
    //   assertThat(result).isNotNull().isEqualTo(expectedDto);
    //   assertThat(mappedUser.getRegisterStatus()).isFalse();
    //   assertThat(mappedUser.getStatus()).isTrue();
    //   verify(userRepository, times(1)).save(mappedUser);
    //   verify(notificationService, times(1)).createNotificationForDentist(
    //       eq(1L), isNull(), eq("Tài khoản mới cần duyệt"), anyList());
    // }

    @Test
    @DisplayName("TC-REG-02: Duplicate Username — throws 409 CONFLICT ResponseStatusException")
    void createUser_duplicateUsername_throws409Conflict() {
      UserDTO inputDto = createMockUserDTO("existingUser", "StrongPass@123", null);
      User existingUser = createMockUser(50L, "existingUser", true, true);

      when(userRepository.getByUsername("existingUser")).thenReturn(Optional.of(existingUser));

      assertThatThrownBy(() -> userService.createUser(inputDto))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("409 CONFLICT")
          .hasMessageContaining(ResponseMessage.USER_USERNAME_ALREADY_EXIST);

      verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Weak password — throws 400 BAD_REQUEST")
    void createUser_weakPassword_throws400BadRequest() {
      UserDTO inputDto = createMockUserDTO("newUser", "123", null); // Weak password

      when(userRepository.getByUsername("newUser")).thenReturn(Optional.empty());
      when(userMapper.userFromUserDTO(inputDto)).thenReturn(new User());

      assertThatThrownBy(() -> userService.createUser(inputDto))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("400 BAD_REQUEST")
          .hasMessageContaining(ResponseMessage.USER_WEAK_PASSWORD);

      verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Admin notification failure — logs error without breaking user registration")
    void createUser_adminNotificationFails_logsErrorButReturnsCreatedUser() throws Exception {
      UserDTO inputDto = createMockUserDTO("user2", "Strong@1234", null);
      User mappedUser = new User();
      User savedUser = createMockUser(101L, "user2", false, true);
      UserDTO expectedDto = createMockUserDTO("user2", "hashed", null);

      when(userRepository.getByUsername("user2")).thenReturn(Optional.empty());
      when(userMapper.userFromUserDTO(inputDto)).thenReturn(mappedUser);
      when(passwordEncoder.encode("Strong@1234")).thenReturn("hashed_password");
      when(userRepository.save(mappedUser)).thenReturn(savedUser);
      when(userMapper.userDTOFromUser(savedUser)).thenReturn(expectedDto);
      when(userRepository.findUsersByRoleCode("ADMIN")).thenThrow(new RuntimeException("DB offline"));

      UserDTO result = userService.createUser(inputDto);

      assertThat(result).isEqualTo(expectedDto);
      verify(userRepository, times(1)).save(mappedUser);
    }
  }

  @Nested
  @DisplayName("approveUser() and rejectUser() — TC-ADMIN-01")
  class AdminApprovalTests {

    @Test
    @DisplayName("TC-ADMIN-01: approveUser() — sets registerStatus=true and status=true")
    void approveUser_existingUser_setsRegisterStatusTrueAndStatusTrue() {
      User pendingUser = createMockUser(10L, "dentist", false, true);
      User approvedUser = createMockUser(10L, "dentist", true, true);
      UserDTO approvedDto = createMockUserDTO("dentist", "pass", null);

      when(userRepository.getReferenceById(10L)).thenReturn(pendingUser);
      when(userRepository.save(pendingUser)).thenReturn(approvedUser);
      when(userMapper.userDTOFromUser(approvedUser)).thenReturn(approvedDto);

      UserDTO result = userService.approveUser(10L);

      assertThat(result).isEqualTo(approvedDto);
      assertThat(pendingUser.getRegisterStatus()).isTrue();
      assertThat(pendingUser.getStatus()).isTrue();
      verify(userRepository, times(1)).save(pendingUser);
    }

    @Test
    @DisplayName("rejectUser() — deletes user by ID from repository")
    void rejectUser_existingUser_deletesUserById() {
      userService.rejectUser(10L);

      verify(userRepository, times(1)).deleteById(10L);
    }
  }

  @Nested
  @DisplayName("lockUser() and unlockUser() — TC-ADMIN-02")
  class AdminLockUnlockTests {

    @Test
    @DisplayName("TC-ADMIN-02: lockUser() — sets user.status = false and saves")
    void lockUser_existingUser_setsStatusFalseAndSaves() {
      User activeUser = createMockUser(20L, "student1", true, true);
      when(userRepository.getReferenceById(20L)).thenReturn(activeUser);

      userService.lockUser(20L);

      assertThat(activeUser.getStatus()).isFalse();
      verify(userRepository, times(1)).save(activeUser);
    }

    @Test
    @DisplayName("TC-ADMIN-02: unlockUser() — sets user.status = true and saves")
    void unlockUser_existingUser_setsStatusTrueAndSaves() {
      User lockedUser = createMockUser(20L, "student1", true, false);
      when(userRepository.getReferenceById(20L)).thenReturn(lockedUser);

      userService.unlockUser(20L);

      assertThat(lockedUser.getStatus()).isTrue();
      verify(userRepository, times(1)).save(lockedUser);
    }
  }

  @Nested
  @DisplayName("Query & Lookup Methods")
  class QueryTests {

    @Test
    @DisplayName("getUserIdFromUsername() — returns ID when user exists")
    void getUserIdFromUsername_userExists_returnsId() {
      User user = createMockUser(55L, "john", true, true);
      when(userRepository.getByUsername("john")).thenReturn(Optional.of(user));

      Long id = userService.getUserIdFromUsername("john");

      assertThat(id).isEqualTo(55L);
    }

    @Test
    @DisplayName("getUserIdFromUsername() — returns null when user not found")
    void getUserIdFromUsername_notFound_returnsNull() {
      when(userRepository.getByUsername("unknown")).thenReturn(Optional.empty());

      Long id = userService.getUserIdFromUsername("unknown");

      assertThat(id).isNull();
    }

    @Test
    @DisplayName("getUserById() — returns mapped UserDTO")
    void getUserById_existingId_returnsUserDto() {
      User user = createMockUser(15L, "alice", true, true);
      UserDTO dto = createMockUserDTO("alice", "pass", null);
      when(userRepository.getReferenceById(15L)).thenReturn(user);
      when(userMapper.userDTOFromUser(user)).thenReturn(dto);

      UserDTO result = userService.getUserById(15L);

      assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("getUserByUsername() — returns UserDTO when exists")
    void getUserByUsername_existingUsername_returnsUserDto() {
      User user = createMockUser(16L, "bob", true, true);
      UserDTO dto = createMockUserDTO("bob", "pass", null);
      when(userRepository.getByUsername("bob")).thenReturn(Optional.of(user));
      when(userMapper.userDTOFromUser(user)).thenReturn(dto);

      UserDTO result = userService.getUserByUsername("bob");

      assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("getUserByUsername() — throws NoSuchElementException when not found")
    void getUserByUsername_notFound_throwsNoSuchElementException() {
      when(userRepository.getByUsername("missing")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.getUserByUsername("missing"))
          .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("getWaitingUsers() — returns list of users where registerStatus=false")
    void getWaitingUsers_returnsUnapprovedUsers() {
      User waitingUser = createMockUser(17L, "pending", false, true);
      UserDTO dto = createMockUserDTO("pending", "pass", null);
      when(userRepository.findByRegisterStatusWithRolesAndOrganization(false))
          .thenReturn(List.of(waitingUser));
      when(userMapper.userDTOFromUser(waitingUser)).thenReturn(dto);

      List<UserDTO> list = userService.getWaitingUsers();

      assertThat(list).hasSize(1).containsExactly(dto);
    }

    @Test
    @DisplayName("getAllUsers() — returns all users mapped to DTOs")
    void getAllUsers_returnsAllUsers() {
      User user = createMockUser(18L, "anyone", true, true);
      UserDTO dto = createMockUserDTO("anyone", "pass", null);
      when(userRepository.findAllWithRolesAndOrganization()).thenReturn(List.of(user));
      when(userMapper.userDTOFromUser(user)).thenReturn(dto);

      List<UserDTO> list = userService.getAllUsers();

      assertThat(list).hasSize(1).containsExactly(dto);
    }
  }

  @Nested
  @DisplayName("Password Checking & Reset Tests")
  class PasswordTests {

    @Test
    @DisplayName("checkValidUserIdPassword() — returns true when password matches")
    void checkValidUserIdPassword_correctPassword_returnsTrue() {
      User user = createMockUser(30L, "user30", true, true);
      when(userRepository.getReferenceById(30L)).thenReturn(user);
      when(passwordEncoder.matches("Secret@123", "encoded_secret")).thenReturn(true);

      boolean isValid = userService.checkValidUserIdPassword(30L, "Secret@123");

      assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("checkValidUserIdPassword() — returns false when user password is null")
    void checkValidUserIdPassword_nullPassword_returnsFalse() {
      User user = createMockUser(31L, "user31", true, true);
      user.setPassword(null);
      when(userRepository.getReferenceById(31L)).thenReturn(user);

      boolean isValid = userService.checkValidUserIdPassword(31L, "Secret@123");

      assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("checkValidUserIdPassword() — returns false when password does not match")
    void checkValidUserIdPassword_wrongPassword_returnsFalse() {
      User user = createMockUser(32L, "user32", true, true);
      when(userRepository.getReferenceById(32L)).thenReturn(user);
      when(passwordEncoder.matches("WrongPass", "encoded_secret")).thenReturn(false);

      boolean isValid = userService.checkValidUserIdPassword(32L, "WrongPass");

      assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("resetPassword() — hashes new valid password and saves user")
    void resetPassword_validEmailAndPassword_hashesAndSaves() throws Exception {
      User user = createMockUser(40L, "resetUser", true, true);
      when(userRepository.findByEmail("reset@nhahocduong.vn")).thenReturn(Optional.of(user));
      when(passwordEncoder.encode("NewStrong@123")).thenReturn("new_hashed_secret");

      userService.resetPassword("reset@nhahocduong.vn", "NewStrong@123");

      assertThat(user.getPassword()).isEqualTo("new_hashed_secret");
      verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("resetPassword() — throws 404 NOT_FOUND when email does not exist")
    void resetPassword_notFoundEmail_throws404NotFound() {
      when(userRepository.findByEmail("missing@nhahocduong.vn")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.resetPassword("missing@nhahocduong.vn", "NewStrong@123"))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("404 NOT_FOUND");

      verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("resetPassword() — throws 400 BAD_REQUEST when password is weak")
    void resetPassword_weakPassword_throws400BadRequest() {
      User user = createMockUser(41L, "resetUser", true, true);
      when(userRepository.findByEmail("weak@nhahocduong.vn")).thenReturn(Optional.of(user));

      assertThatThrownBy(() -> userService.resetPassword("weak@nhahocduong.vn", "weak"))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("400 BAD_REQUEST")
          .hasMessageContaining(ResponseMessage.USER_WEAK_PASSWORD);

      verify(userRepository, never()).save(any());
    }
  }
}
