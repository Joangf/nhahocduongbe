package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.SystemLog;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

  List<SystemLog> findBySessionIdOrderByCreatedDateDesc(String sessionId);

  void deleteBySessionId(String sessionId);
}
