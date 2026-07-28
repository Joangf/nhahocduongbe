package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.NotificationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Notification;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.NotificationMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.NotificationRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.SseNotificationService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.NotificationServiceImpl;

@DisplayName("NotificationServiceImpl — Unit Tests")
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplUnitTest {

  @Mock NotificationRepository notificationRepository;
  @Mock NotificationMapper notificationMapper;
  @Mock SseNotificationService sseNotificationService;
  @Mock Authentication authentication;
  @Mock SecurityContext securityContext;

  @InjectMocks NotificationServiceImpl service;

  private static final Long USER_ID = 42L;

  @BeforeEach
  void setUp() {
    // Mock SecurityContext để getCurrentUserId() trả về USER_ID
    // Dùng lenient() vì một số test method không cần SecurityContext
    lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    lenient().when(authentication.getPrincipal()).thenReturn(USER_ID);
    SecurityContextHolder.setContext(securityContext);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  // ─── Helpers ───────────────────────────────────────────────────────────────

  private Notification notif(Long id, Long recipientId, String title, String message, boolean isRead) {
    Notification n = new Notification();
    n.setId(id);
    n.setRecipientId(recipientId);
    n.setTitle(title);
    n.setMessage(message);
    n.setIsRead(isRead);
    n.setStatus(true);
    return n;
  }

  private NotificationDTO dto(Long id, Long recipientId, String title, String message, boolean isRead) {
    return new NotificationDTO(id, recipientId, null, title, message, isRead, null);
  }

  // ─── TC-01: getMyNotifications ─────────────────────────────────────────────

  @Nested
  @DisplayName("TC-01 getMyNotifications()")
  class GetMyNotifications {

    @Test
    @DisplayName("Trả về danh sách thông báo của user hiện tại")
    void shouldReturnCurrentUserNotifications() {
      Notification entity = notif(1L, USER_ID, "Test", "Msg", false);
      NotificationDTO dto = dto(1L, USER_ID, "Test", "Msg", false);

      when(notificationRepository.findByRecipientIdAndStatusOrderByCreatedDateDesc(USER_ID, true))
          .thenReturn(List.of(entity));
      when(notificationMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

      List<NotificationDTO> result = service.getMyNotifications();

      assertThat(result).hasSize(1);
      assertThat(result.get(0).title()).isEqualTo("Test");
      verify(notificationRepository).findByRecipientIdAndStatusOrderByCreatedDateDesc(USER_ID, true);
    }

    @Test
    @DisplayName("Trả về danh sách rỗng khi không có thông báo")
    void shouldReturnEmptyList() {
      when(notificationRepository.findByRecipientIdAndStatusOrderByCreatedDateDesc(USER_ID, true))
          .thenReturn(Collections.emptyList());
      when(notificationMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<NotificationDTO> result = service.getMyNotifications();

      assertThat(result).isEmpty();
    }
  }

  // ─── TC-02: countUnread ────────────────────────────────────────────────────

  @Nested
  @DisplayName("TC-02 countUnread()")
  class CountUnread {

    @Test
    @DisplayName("Trả về số thông báo chưa đọc")
    void shouldReturnUnreadCount() {
      when(notificationRepository.countByRecipientIdAndIsReadAndStatus(USER_ID, false, true))
          .thenReturn(5L);

      long count = service.countUnread();

      assertThat(count).isEqualTo(5L);
    }

    @Test
    @DisplayName("Trả về 0 khi không có thông báo chưa đọc")
    void shouldReturnZeroWhenNone() {
      when(notificationRepository.countByRecipientIdAndIsReadAndStatus(USER_ID, false, true))
          .thenReturn(0L);

      long count = service.countUnread();

      assertThat(count).isEqualTo(0L);
    }
  }

  // ─── TC-03: markAsRead ─────────────────────────────────────────────────────

  @Nested
  @DisplayName("TC-03 markAsRead()")
  class MarkAsRead {

    @Test
    @DisplayName("Đánh dấu đã đọc thành công")
    void shouldMarkAsRead() {
      Notification entity = notif(1L, USER_ID, "Test", "Msg", false);
      NotificationDTO dto = dto(1L, USER_ID, "Test", "Msg", true);

      when(notificationRepository.findById(1L)).thenReturn(Optional.of(entity));
      when(notificationRepository.save(any(Notification.class))).thenReturn(entity);
      when(notificationMapper.toDto(entity)).thenReturn(dto);

      NotificationDTO result = service.markAsRead(1L);

      assertThat(result.isRead()).isTrue();
      verify(notificationRepository).save(entity);
    }

    @Test
    @DisplayName("Ném 404 khi thông báo không tồn tại")
    void shouldThrowWhenNotFound() {
      when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.markAsRead(99L))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("Không tìm thấy thông báo");
    }

    @Test
    @DisplayName("Ném 403 khi user không phải chủ sở hữu")
    void shouldThrowWhenNotOwner() {
      Notification entity = notif(1L, 999L, "Test", "Msg", false);

      when(notificationRepository.findById(1L)).thenReturn(Optional.of(entity));

      assertThatThrownBy(() -> service.markAsRead(1L))
          .isInstanceOf(ResponseStatusException.class)
          .extracting("status")
          .isEqualTo(HttpStatus.FORBIDDEN);
    }
  }

  // ─── TC-04: markAllAsRead ──────────────────────────────────────────────────

  @Nested
  @DisplayName("TC-04 markAllAsRead()")
  class MarkAllAsRead {

    @Test
    @DisplayName("Gọi batch update query")
    void shouldCallBatchUpdate() {
      service.markAllAsRead();

      verify(notificationRepository).markAllAsReadByRecipientId(USER_ID);
    }
  }

  // ─── TC-05: getCurrentUserId ───────────────────────────────────────────────

  @Nested
  @DisplayName("TC-05 getCurrentUserId() authentication")
  class GetCurrentUserId {

    @Test
    @DisplayName("Ném UNAUTHORIZED khi không có authentication")
    void shouldThrowWhenNoAuth() {
      SecurityContextHolder.clearContext();

      assertThatThrownBy(() -> service.getMyNotifications())
          .isInstanceOf(ResponseStatusException.class)
          .extracting("status")
          .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Ném UNAUTHORIZED khi principal null")
    void shouldThrowWhenPrincipalNull() {
      when(authentication.getPrincipal()).thenReturn(null);

      assertThatThrownBy(() -> service.getMyNotifications())
          .isInstanceOf(ResponseStatusException.class)
          .extracting("status")
          .isEqualTo(HttpStatus.UNAUTHORIZED);
    }
  }

  // ─── TC-06: createNotificationForAdmin ─────────────────────────────────────

  @Nested
  @DisplayName("TC-06 createNotificationForAdmin()")
  class CreateNotificationForAdmin {

    @Test
    @DisplayName("Lưu notification và push SSE")
    void shouldSaveAndPushSse() {
      service.createNotificationForAdmin(10L, "Tiêu đề", "Nội dung");

      verify(notificationRepository).save(argThat(n ->
          n.getRecipientId().equals(10L) &&
              n.getTitle().equals("Tiêu đề") &&
              n.getMessage().equals("Nội dung")
      ));
      verify(sseNotificationService).sendNotification(
          eq("10"), eq("Tiêu đề"), eq("Nội dung"),
          eq(vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constant.SseConstants.NotificationType.REGISTRATION));
    }
  }

  // ─── TC-07: createNotificationForDentist ───────────────────────────────────

  @Nested
  @DisplayName("TC-07 createNotificationForDentist()")
  class CreateNotificationForDentist {

    @Test
    @DisplayName("Lưu notification và push SSE cho bác sĩ")
    void shouldSaveAndPushSse() {
      List<String> details = List.of("Trường TH A - Lớp 5A - Ngày 2026-06-15");

      service.createNotificationForDentist(10L, 1L, "Đợt Q1", details);

      verify(notificationRepository).save(argThat(n ->
          n.getRecipientId().equals(10L) &&
              n.getCampaignId().equals(1L) &&
              n.getTitle().contains("Đợt Q1") &&
              n.getMessage().contains("Trường TH A")
      ));
      verify(sseNotificationService).sendNotification(
          eq("10"), contains("Đợt Q1"), anyString(),
          eq(vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constant.SseConstants.NotificationType.SCHEDULE));
    }
  }
}
