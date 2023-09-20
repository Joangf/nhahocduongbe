package vn.viettel.bvrhm.nhahocduong.api.user.internal.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.UserDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.User;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.UserRepository;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.mapper.UserMapper;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private UserRepository userRepository;
  @Autowired private UserMapper userMapper;


  @Transactional
  public UserDTO createUser(UserDTO newUserDTO) throws Exception {
    User newUser = userMapper.userFromUserDTO(newUserDTO);

    String inputPassword = newUserDTO.password();
    // TODO validate password strength
    String hashedPassword = passwordEncoder.encode(inputPassword);

    newUser.setId(null);
    newUser.setPassword(hashedPassword);

    User createdUser = userRepository.save(newUser);
    UserDTO createdUserDTO = userMapper.userDTOFromUser(createdUser);

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
    User user = userRepository.getByUsername(username).orElseThrow();
    return userMapper.userDTOFromUser(user);
  }

  public boolean checkValidUserIdPassword(Long userId, String inputPassword) {
    User user = userRepository.getReferenceById(userId);
    if (user.getPassword() == null) {
      return false;
    }
    return passwordEncoder.matches(inputPassword, user.getPassword());
  }

  //  UserDTO saveUser(UserDTO userDTO) {
  //    // TODO validate input
  //    User user = userMapper.userFromUserDTO(userDTO);
  //    User savedUser = userRepository.save(user);
  //    UserDTO savedUserDTO = userMapper.userDTOFromUser(savedUser);
  //    return savedUserDTO;
  //  }

}
