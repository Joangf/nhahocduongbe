package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.util.List;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.NotificationDTO;

public interface NotificationService {

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

  /**
   * Tạo thông báo cho admin về tài khoản mới cần duyệt.
   *
   * @param adminId ID của admin nhận thông báo
   * @param title Tiêu đề thông báo
   * @param message Nội dung thông báo
   */
  void createNotificationForAdmin(Long adminId, String title, String message);
}
