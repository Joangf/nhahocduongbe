package vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.UserPassword;

import java.util.Optional;

public interface UserPasswordRepository extends JpaRepository<UserPassword, Long> {
  public Optional<UserPassword> getUserPasswordByUserId(Long userId);
}
