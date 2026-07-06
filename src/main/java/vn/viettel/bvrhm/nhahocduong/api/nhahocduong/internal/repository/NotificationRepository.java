package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  List<Notification> findByRecipientIdAndStatusOrderByCreatedDateDesc(Long recipientId, Boolean status);

  Page<Notification> findByRecipientIdAndStatus(Long recipientId, Boolean status, Pageable pageable);

  long countByRecipientIdAndIsReadAndStatus(Long recipientId, Boolean isRead, Boolean status);
}
