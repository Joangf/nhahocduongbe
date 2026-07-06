package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.NotificationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

  @Autowired private NotificationService notificationService;

  @GetMapping
  public List<NotificationDTO> getMyNotifications() {
    return notificationService.getMyNotifications();
  }

  @GetMapping("/unread-count")
  public Map<String, Long> countUnread() {
    long count = notificationService.countUnread();
    return Map.of("count", count);
  }

  @PutMapping("/{id}/read")
  public NotificationDTO markAsRead(@PathVariable Long id) {
    return notificationService.markAsRead(id);
  }

  @PutMapping("/read-all")
  public Map<String, String> markAllAsRead() {
    notificationService.markAllAsRead();
    return Map.of("message", "Đã đánh dấu tất cả thông báo là đã đọc");
  }
}
