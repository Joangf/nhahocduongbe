package vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.UserPassword;

public interface UserPasswordRepository extends JpaRepository<UserPassword, Long> {
  public Optional<UserPassword> getUserPasswordByUserId(Long userId);
}
