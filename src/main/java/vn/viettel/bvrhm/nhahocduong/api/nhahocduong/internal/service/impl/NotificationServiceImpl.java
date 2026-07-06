package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.NotificationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Notification;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.NotificationMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.NotificationRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

  private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

  @Autowired private NotificationRepository notificationRepository;
  @Autowired private NotificationMapper notificationMapper;

  private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication.getPrincipal() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Người dùng chưa đăng nhập");
    }
    try {
      return Long.valueOf(authentication.getPrincipal().toString());
    } catch (NumberFormatException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không thể xác định người dùng");
    }
  }

  @Override
  @Transactional
  public NotificationDTO createNotification(NotificationDTO dto) {
    Notification notification = notificationMapper.toEntity(dto);
    Notification saved = notificationRepository.save(notification);
    return notificationMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<NotificationDTO> getMyNotifications() {
    Long userId = getCurrentUserId();
    List<Notification> notifications =
        notificationRepository.findByRecipientIdAndStatusOrderByCreatedDateDesc(userId, true);
    return notificationMapper.toDtoList(notifications);
  }

  @Override
  @Transactional(readOnly = true)
  public long countUnread() {
    Long userId = getCurrentUserId();
    return notificationRepository.countByRecipientIdAndIsReadAndStatus(userId, false, true);
  }

  @Override
  @Transactional
  public NotificationDTO markAsRead(Long notificationId) {
    Long userId = getCurrentUserId();
    Notification notification =
        notificationRepository
            .findById(notificationId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy thông báo với id: " + notificationId));

    // Chỉ chủ sở hữu thông báo mới được đánh dấu đã đọc
    if (!notification.getRecipientId().equals(userId)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác trên thông báo này");
    }

    notification.setIsRead(true);
    Notification saved = notificationRepository.save(notification);
    return notificationMapper.toDto(saved);
  }

  @Override
  @Transactional
  public void markAllAsRead() {
    Long userId = getCurrentUserId();
    List<Notification> unreadNotifications =
        notificationRepository.findByRecipientIdAndStatusOrderByCreatedDateDesc(userId, true);
    for (Notification notification : unreadNotifications) {
      if (!notification.getIsRead()) {
        notification.setIsRead(true);
      }
    }
    notificationRepository.saveAll(unreadNotifications);
  }

  @Override
  @Transactional
  public void createNotificationForDentist(
      Long userId, Long campaignId, String campaignName, List<String> scheduleDetails) {

    String title = "Lịch khám: " + campaignName;

    StringBuilder messageBuilder = new StringBuilder();
    messageBuilder.append("Bạn được phân công khám trong đợt \"").append(campaignName).append("\":\n");
    for (int i = 0; i < scheduleDetails.size(); i++) {
      messageBuilder.append("  • ").append(scheduleDetails.get(i));
      if (i < scheduleDetails.size() - 1) {
        messageBuilder.append("\n");
      }
    }

    Notification notification = new Notification();
    notification.setRecipientId(userId);
    notification.setCampaignId(campaignId);
    notification.setTitle(title);
    notification.setMessage(messageBuilder.toString());

    notificationRepository.save(notification);
    log.info("Created notification for userId={}, campaignId={}", userId, campaignId);
  }
}
