package vn.viettel.bvrhm.nhahocduong.api.user.internal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> getByUsername(String username);

  // public List<User> findByName(String name); // TODO find by combine firstname + lastname
  Optional<User> findByEmail(String email);

  Optional<User> findByPhoneNumber(String phoneNumber);

  Optional<User> findByUsernameAndEmailAndPhoneNumber(String username, String email, String phoneNumber);

  List<User> findByRegisterStatus(Boolean registerStatus);

  @Query("SELECT DISTINCT u FROM User u JOIN u.roleList r WHERE r.code = :roleCode AND u.status = true")
  List<User> findUsersByRoleCode(@Param("roleCode") String roleCode);

  // Single query: user + organization to avoid N+1 in AuthorizationService
  @Query("SELECT u FROM User u LEFT JOIN FETCH u.organization WHERE u.id = :id")
  Optional<User> findByIdWithOrganization(@Param("id") Long id);
}
