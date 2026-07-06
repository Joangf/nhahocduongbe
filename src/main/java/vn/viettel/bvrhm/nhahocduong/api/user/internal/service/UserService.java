package vn.viettel.bvrhm.nhahocduong.api.user.internal.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
import vn.viettel.bvrhm.nhahocduong.api.user.internal.validator.UserValidator;

@Service
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private UserRepository userRepository;
  @Autowired private UserMapper userMapper;
  @Autowired private RoleRepository roleRepository;
  @Autowired private NotificationService notificationService;

  @Transactional
  public UserDTO createUser(UserDTO newUserDTO) throws Exception {
    // Kiểm tra username đã tồn tại chưa
    String inputUsername = newUserDTO.username();
    if (userRepository.getByUsername(inputUsername).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, ResponseMessage.USER_USERNAME_ALREADY_EXIST);
    }

    User newUser = userMapper.userFromUserDTO(newUserDTO);

    // MapStruct không tự động map RoleDTO -> Role entity,
    // nên cần thực hiện thủ công để gán role cho user.
    if (newUserDTO.roleList() != null && !newUserDTO.roleList().isEmpty()) {
      List<Role> roles = new ArrayList<>();
      for (RoleDTO roleDTO : newUserDTO.roleList()) {
        Long roleId = Long.valueOf(roleDTO.id());
        Role role = roleRepository.getReferenceById(roleId);
        roles.add(role);
      }
      newUser.setRoleList(roles);
    }

    String inputPassword = newUserDTO.password();
    boolean isValidPassword = UserValidator.validatePasswordStrength(inputPassword);
    if (!isValidPassword) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ResponseMessage.USER_WEAK_PASSWORD);
    }

    String hashedPassword = passwordEncoder.encode(inputPassword);

    newUser.setId(null);
    newUser.setPassword(hashedPassword);
    newUser.setRegisterStatus(false);
    newUser.setStatus(true); // Default to unlocked

    User createdUser = userRepository.save(newUser);
    UserDTO createdUserDTO = userMapper.userDTOFromUser(createdUser);

    // Gửi thông báo cho tất cả admin về tài khoản mới cần duyệt
    try {
      List<User> admins = userRepository.findUsersByRoleCode("ADMIN");
      if (!admins.isEmpty()) {
        String fullName = (createdUser.getLastName() != null ? createdUser.getLastName() : "")
            + " " + (createdUser.getFirstName() != null ? createdUser.getFirstName() : "");
        String notificationTitle = "Tài khoản mới cần duyệt";
        String notificationMessage = "Người dùng \"" + createdUser.getUsername()
            + "\" (" + fullName.trim() + ") vừa đăng ký tài khoản và đang chờ được duyệt.";
        for (User admin : admins) {
          List<String> details = new ArrayList<>();
          details.add(notificationMessage);
          notificationService.createNotificationForDentist(
              admin.getId(), null, notificationTitle, details);
        }
      }
    } catch (Exception e) {
      log.error("Không thể gửi thông báo cho admin về tài khoản mới", e);
    }

    return createdUserDTO;
  }

  public Long getUserIdFromUsername(String inputUsername) {
    Optional<User> userMaybe = userRepository.getByUsername(inputUsername);
    if (userMaybe.isEmpty()) {
      return null;
    }

    return userMaybe.get().getId();
  }

  public UserDTO getUserById(Long userId) {
    User user = userRepository.getReferenceById(userId);
    return userMapper.userDTOFromUser(user);
  }

  public UserDTO getUserByUsername(String username) throws NoSuchElementException {
    User user = userRepository.getByUsername(username).orElseThrow(NoSuchElementException::new);
    return userMapper.userDTOFromUser(user);
  }

  public boolean checkValidUserIdPassword(Long userId, String inputPassword) {
    User user = userRepository.getReferenceById(userId);
    if (user.getPassword() == null) {
      return false;
    }
    return passwordEncoder.matches(inputPassword, user.getPassword());
  }

  public List<UserDTO> getWaitingUsers() {
    return userRepository.findByRegisterStatus(false).stream()
        .map(userMapper::userDTOFromUser)
        .toList();
  }

  @Transactional
  public UserDTO approveUser(Long userId) {
    User user = userRepository.getReferenceById(userId);
    user.setRegisterStatus(true);
    user.setStatus(true); // Ensure account is unlocked when approved
    User savedUser = userRepository.save(user);
    return userMapper.userDTOFromUser(savedUser);
  }

  @Transactional
  public void rejectUser(Long userId) {
    userRepository.deleteById(userId);
  }

  public List<UserDTO> getAllUsers() {
    return userRepository.findAll().stream()
        .map(userMapper::userDTOFromUser)
        .toList();
  }

  @Transactional
  public void lockUser(Long userId) {
    User user = userRepository.getReferenceById(userId);
    user.setStatus(false);
    userRepository.save(user);
  }

  @Transactional
  public void unlockUser(Long userId) {
    User user = userRepository.getReferenceById(userId);
    user.setStatus(true);
    userRepository.save(user);
  }

  //  UserDTO saveUser(UserDTO userDTO) {
  //    // TODO validate input
  //    User user = userMapper.userFromUserDTO(userDTO);
  //    User savedUser = userRepository.save(user);
  //    UserDTO savedUserDTO = userMapper.userDTOFromUser(savedUser);
  //    return savedUserDTO;
  //  @Transactional
  public void resetPassword(String email, String newPassword) throws Exception {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy user với email này."));

    boolean isValidPassword = UserValidator.validatePasswordStrength(newPassword);
    if (!isValidPassword) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ResponseMessage.USER_WEAK_PASSWORD);
    }

    String hashedPassword = passwordEncoder.encode(newPassword);
    user.setPassword(hashedPassword);
    userRepository.save(user);
  }

}
