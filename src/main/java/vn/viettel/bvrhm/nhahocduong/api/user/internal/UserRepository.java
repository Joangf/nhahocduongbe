package vn.viettel.bvrhm.nhahocduong.api.user.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  public Optional<User> getByUsername(String username);
  // public List<User> findByName(String name); // TODO find by combine firstname + lastname
  public Optional<User> findByEmail(String email);

  public Optional<User> findByPhoneNumber(String phoneNumber);
}
