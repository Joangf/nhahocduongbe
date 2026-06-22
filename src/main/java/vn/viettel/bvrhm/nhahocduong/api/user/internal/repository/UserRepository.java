package vn.viettel.bvrhm.nhahocduong.api.user.internal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> getByUsername(String username);

  // public List<User> findByName(String name); // TODO find by combine firstname + lastname
  Optional<User> findByEmail(String email);

  Optional<User> findByPhoneNumber(String phoneNumber);

  List<User> findByRegisterStatus(Boolean registerStatus);
}
