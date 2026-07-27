package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  List<Notification> findByRecipientIdAndStatusOrderByCreatedDateDesc(Long recipientId, Boolean status);

  Page<Notification> findByRecipientIdAndStatus(Long recipientId, Boolean status, Pageable pageable);

  long countByRecipientIdAndIsReadAndStatus(Long recipientId, Boolean isRead, Boolean status);

  @Modifying
  @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipientId = :recipientId AND n.isRead = false AND n.status = true")
  void markAllAsReadByRecipientId(@Param("recipientId") Long recipientId);
}
