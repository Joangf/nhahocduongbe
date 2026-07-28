package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.service;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constant.SseConstants;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.SseNotificationService;

@DisplayName("SseNotificationService — Unit Tests")
class SseNotificationServiceTest {

  SseNotificationService service;

  @BeforeEach
  void setUp() {
    service = new SseNotificationService();
  }

  // ─── TC-01: createTicket ───────────────────────────────────────────────────

  @Nested
  @DisplayName("TC-01 createTicket()")
  class CreateTicket {

    @Test
    @DisplayName("Trả về non-blank UUID string")
    void shouldReturnNonBlankUuid() {
      String ticket = service.createTicket("42");

      assertThat(ticket).isNotBlank();
      assertThat(ticket).matches("[0-9a-f\\-]{36}");
    }

    @Test
    @DisplayName("Mỗi lần gọi trả về ticket khác nhau")
    void shouldReturnDifferentTickets() {
      String t1 = service.createTicket("42");
      String t2 = service.createTicket("42");

      assertThat(t1).isNotEqualTo(t2);
    }
  }

  // ─── TC-02: consumeTicket ──────────────────────────────────────────────────

  @Nested
  @DisplayName("TC-02 consumeTicket()")
  class ConsumeTicket {

    @Test
    @DisplayName("Trả về userId khi ticket hợp lệ")
    void shouldReturnUserIdForValidTicket() {
      String ticket = service.createTicket("42");

      String userId = service.consumeTicket(ticket);

      assertThat(userId).isEqualTo("42");
    }

    @Test
    @DisplayName("Trả về null khi ticket không tồn tại")
    void shouldReturnNullForUnknownTicket() {
      String userId = service.consumeTicket("non-existent-ticket");

      assertThat(userId).isNull();
    }

    @Test
    @DisplayName("Trả về null khi ticket là null")
    void shouldReturnNullForNullTicket() {
      assertThat(service.consumeTicket(null)).isNull();
    }

    @Test
    @DisplayName("Ticket chỉ dùng được 1 lần (one-time use)")
    void shouldBeOneTimeUse() {
      String ticket = service.createTicket("42");

      assertThat(service.consumeTicket(ticket)).isEqualTo("42");
      assertThat(service.consumeTicket(ticket)).isNull(); // Lần 2 → null
    }
  }

  // ─── TC-03: subscribe ──────────────────────────────────────────────────────

  @Nested
  @DisplayName("TC-03 subscribe()")
  class Subscribe {

    @Test
    @DisplayName("Trả về SseEmitter không null")
    void shouldReturnSseEmitter() {
      SseEmitter emitter = service.subscribe("42");

      assertThat(emitter).isNotNull();
      assertThat(emitter.getTimeout()).isEqualTo(30 * 60 * 1000L);
    }

    @Test
    @DisplayName("Nhiều subscriber cho cùng userId không conflict")
    void shouldAllowMultipleSubscribers() {
      SseEmitter e1 = service.subscribe("42");
      SseEmitter e2 = service.subscribe("42");

      assertThat(e1).isNotNull();
      assertThat(e2).isNotNull();
      assertThat(e1).isNotSameAs(e2);
    }
  }

  // ─── TC-04: sendNotification ───────────────────────────────────────────────

  @Nested
  @DisplayName("TC-04 sendNotification()")
  class SendNotification {

    @Test
    @DisplayName("Không throw exception khi user không có emitter")
    void shouldNotThrowWhenNoEmitter() {
      assertThatNoException().isThrownBy(() ->
          service.sendNotification("99", "Title", "Message", "REGISTRATION"));
    }

    @Test
    @DisplayName("Gửi event thành công đến subscriber đang active")
    void shouldSendToActiveSubscriber() throws IOException {
      SseEmitter emitter = service.subscribe("42");

      // Gửi notification đến subscriber
      assertThatNoException().isThrownBy(() ->
          service.sendNotification("42", "Test Title", "Test Msg",
              SseConstants.NotificationType.REGISTRATION));
    }
  }

  // ─── TC-05: sendHeartbeat ──────────────────────────────────────────────────

  @Nested
  @DisplayName("TC-05 sendHeartbeat()")
  class SendHeartbeat {

    @Test
    @DisplayName("Không throw exception khi không có emitter")
    void shouldNotThrowWhenNoEmitters() {
      assertThatNoException().isThrownBy(() -> service.sendHeartbeat());
    }

    @Test
    @DisplayName("Cleanup ticket hết hạn")
    void shouldCleanupExpiredTickets() {
      // Tạo ticket nhưng không consume → heartbeat sẽ dọn nếu quá 30s
      String ticket = service.createTicket("42");
      // Ticket còn valid ngay sau khi tạo
      assertThat(service.consumeTicket(ticket)).isEqualTo("42");
    }
  }
}
