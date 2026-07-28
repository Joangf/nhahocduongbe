package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.NotificationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.NotificationService;

@DisplayName("NotificationController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

  @Mock NotificationService notificationService;
  @InjectMocks NotificationController controller;

  @Nested
  @DisplayName("GET /api/notifications")
  class GetMyNotifications {

    @Test
    @DisplayName("Trả về danh sách thông báo")
    void shouldReturnNotifications() {
      NotificationDTO dto = mock(NotificationDTO.class);
      when(notificationService.getMyNotifications()).thenReturn(List.of(dto));

      var result = controller.getMyNotifications();

      assertThat(result).hasSize(1);
    }
  }

  @Nested
  @DisplayName("GET /api/notifications/unread-count")
  class CountUnread {

    @Test
    @DisplayName("Trả về số thông báo chưa đọc")
    void shouldReturnUnreadCount() {
      when(notificationService.countUnread()).thenReturn(5L);

      Map<String, Long> result = controller.countUnread();

      assertThat(result).containsEntry("count", 5L);
    }
  }

  @Nested
  @DisplayName("PUT /api/notifications/{id}/read")
  class MarkAsRead {

    @Test
    @DisplayName("Đánh dấu thông báo đã đọc")
    void shouldMarkAsRead() {
      NotificationDTO dto = mock(NotificationDTO.class);
      when(notificationService.markAsRead(1L)).thenReturn(dto);

      var result = controller.markAsRead(1L);

      assertThat(result).isSameAs(dto);
    }
  }

  @Nested
  @DisplayName("PUT /api/notifications/read-all")
  class MarkAllAsRead {

    @Test
    @DisplayName("Đánh dấu tất cả đã đọc và trả về message")
    void shouldMarkAllAsRead() {
      Map<String, String> result = controller.markAllAsRead();

      assertThat(result).containsEntry("message", "Đã đánh dấu tất cả thông báo là đã đọc");
      verify(notificationService).markAllAsRead();
    }
  }
}
