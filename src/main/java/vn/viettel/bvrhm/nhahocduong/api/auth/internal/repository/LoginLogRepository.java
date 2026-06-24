package vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.LoginLog;

import java.util.List;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    List<LoginLog> findAllByOrderByLoginTimeDesc();

    List<LoginLog> findByUsernameNotOrderByLoginTimeDesc(String username);

    List<LoginLog> findByUsernameAndLogoutTimeIsNullOrderByLoginTimeDesc(String username);
}
