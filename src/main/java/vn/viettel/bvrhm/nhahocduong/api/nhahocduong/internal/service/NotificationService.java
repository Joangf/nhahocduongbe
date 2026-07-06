package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.util.List;
import org.springframework.data.domain.Page;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.NotificationDTO;

public interface NotificationService {

  NotificationDTO createNotification(NotificationDTO dto);

  List<NotificationDTO> getMyNotifications();

  long countUnread();

  NotificationDTO markAsRead(Long notificationId);

  void markAllAsRead();

  /**
   * Tạo thông báo cho một bác sĩ về lịch khám của họ trong đợt khám.
   *
   * @param userId ID của user (bác sĩ) nhận thông báo
   * @param campaignId ID của đợt khám
   * @param campaignName Tên đợt khám
   * @param scheduleDetails Danh sách chi tiết lịch khám (mỗi phần tử là 1 dòng mô tả)
   */
  void createNotificationForDentist(
      Long userId, Long campaignId, String campaignName, List<String> scheduleDetails);
}
