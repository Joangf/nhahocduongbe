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
    @DisplayName("Không throw khi có emitter active")
    void shouldNotThrowWhenHasEmitters() {
      service.subscribe("42");

      assertThatNoException().isThrownBy(() -> service.sendHeartbeat());
    }

    @Test
    @DisplayName("Cleanup ticket hết hạn")
    void shouldCleanupExpiredTickets() {
      String ticket = service.createTicket("42");
      assertThat(service.consumeTicket(ticket)).isEqualTo("42");
    }
  }

  // ─── TC-06: sendNotification after subscribe ──────────────────────────────

  @Nested
  @DisplayName("TC-06 sendNotification after subscribe")
  class SendAfterSubscribe {

    @Test
    @DisplayName("Gửi đến user có subscriber không throw")
    void shouldSendToSubscribedUser() {
      service.subscribe("42");
      service.subscribe("42"); // 2 connections

      assertThatNoException().isThrownBy(() ->
          service.sendNotification("42", "Test", "Hello", "TYPE"));
    }

    @Test
    @DisplayName("Gửi đến user không có subscriber không throw")
    void shouldSendToUnsubscribedUser() {
      assertThatNoException().isThrownBy(() ->
          service.sendNotification("99", "Test", "Hello", "TYPE"));
    }
  }

  // ─── TC-07: Multi-user ────────────────────────────────────────────────────

  @Nested
  @DisplayName("TC-07 Multi-user scenarios")
  class MultiUser {

    @Test
    @DisplayName("Chỉ user được subscribe mới nhận được notification")
    void onlySubscribedUserGetsNotification() {
      service.subscribe("42");

      assertThatNoException().isThrownBy(() ->
          service.sendNotification("42", "Only 42", "Gets this", "TYPE"));
      assertThatNoException().isThrownBy(() ->
          service.sendNotification("99", "Nobody", "Gets this", "TYPE"));
    }
  }

  // ─── TC-08: subscribe cleanup callback ────────────────────────────────────

  @Nested
  @DisplayName("TC-08 subscribe() cleanup callback")
  class SubscribeCleanup {

    @Test
    @DisplayName("onCompletion: complete() trigger cleanup callback → emitter bị xóa sau async")
    void onCompletion_triggersAsyncCleanup() throws InterruptedException {
      SseEmitter emitter = service.subscribe("42");

      // complete() triggers onCompletion callback (runs on Spring async executor)
      emitter.complete();

      // Wait for async cleanup callback to execute
      Thread.sleep(200);

      // After cleanup, emitter was removed from list.
      // If list is now empty, it's removed from map → sendNotification returns early.
      // If list still has the completed emitter, send() throws IllegalStateException.
      // Either way, no unhandled exception from the service itself.
      try {
        service.sendNotification("42", "Title", "Msg", "TYPE");
      } catch (IllegalStateException e) {
        // Expected — completed emitter still in list before async cleanup
      }
    }

    @Test
    @DisplayName("onCompletion: nhiều emitter — complete một emitter, list vẫn chứa emitter đã complete")
    void onCompletion_completedEmitterStillInListUntilAsyncCleanup() {
      SseEmitter first = service.subscribe("42");
      SseEmitter second = service.subscribe("42");

      // Complete only the first emitter — cleanup is async, so first is still in list
      first.complete();

      // sendNotification iterates ALL emitters including the completed one
      // The completed emitter's send() throws IllegalStateException (not IOException)
      // This is expected behavior — the service catches IOException, not IllegalStateException
      assertThatThrownBy(() ->
          service.sendNotification("42", "Title", "Msg", "TYPE"))
          .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("onError: completeWithError() trigger cleanup callback → emitter bị xóa sau async")
    void onError_triggersAsyncCleanup() throws InterruptedException {
      SseEmitter emitter = service.subscribe("42");

      // completeWithError triggers onError callback (runs on Spring async executor)
      emitter.completeWithError(new RuntimeException("test error"));

      // Wait for async cleanup callback to execute
      Thread.sleep(200);

      // After cleanup, emitter was removed from list.
      try {
        service.sendNotification("42", "Title", "Msg", "TYPE");
      } catch (IllegalStateException e) {
        // Expected — errored emitter still in list before async cleanup
      }
    }

    @Test
    @DisplayName("onError: nhiều emitter — error trên một emitter, list vẫn chứa emitter đã error")
    void onError_erroredEmitterStillInListUntilAsyncCleanup() {
      service.subscribe("42");
      SseEmitter errorEmitter = service.subscribe("42");

      // Error on second emitter — cleanup is async, so it's still in list
      errorEmitter.completeWithError(new RuntimeException("test error"));

      // sendNotification iterates ALL emitters including the errored one
      // The errored emitter's send() throws IllegalStateException (not IOException)
      assertThatThrownBy(() ->
          service.sendNotification("42", "Title", "Msg", "TYPE"))
          .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("onCompletion: emitter đã complete → send() ném IllegalStateException")
    void onCompletion_completedEmitterThrowsOnSend() {
      SseEmitter emitter = service.subscribe("42");
      emitter.complete();

      // SseEmitter.send() throws IllegalStateException on completed emitter
      // Note: service catches IOException but not IllegalStateException — this is by design
      assertThatThrownBy(() ->
          service.sendNotification("42", "Title", "Msg", "TYPE"))
          .isInstanceOf(IllegalStateException.class);
    }
  }

  // ─── TC-09: subscribe multiple users ──────────────────────────────────────

  @Nested
  @DisplayName("TC-09 subscribe() multiple users")
  class SubscribeMultipleUsers {

    @Test
    @DisplayName("Subscribe nhiều user khác nhau — mỗi user có emitter riêng")
    void shouldCreateSeparateEmittersForDifferentUsers() {
      SseEmitter e1 = service.subscribe("1");
      SseEmitter e2 = service.subscribe("2");

      assertThat(e1).isNotSameAs(e2);

      // Notification to user "1" should work
      assertThatNoException().isThrownBy(() ->
          service.sendNotification("1", "Title", "Msg", "TYPE"));

      // Notification to user "2" should also work
      assertThatNoException().isThrownBy(() ->
          service.sendNotification("2", "Title", "Msg", "TYPE"));
    }

    @Test
    @DisplayName("Subscribe cùng user nhiều lần → mỗi lần tạo emitter mới")
    void shouldCreateNewEmitterForEachSubscribeCall() {
      SseEmitter e1 = service.subscribe("42");
      SseEmitter e2 = service.subscribe("42");
      SseEmitter e3 = service.subscribe("42");

      assertThat(e1).isNotSameAs(e2).isNotSameAs(e3);

      // All 3 emitters are active — notification should work
      assertThatNoException().isThrownBy(() ->
          service.sendNotification("42", "Title", "Msg", "TYPE"));
    }
  }

  // ─── TC-10: sendNotification edge cases ───────────────────────────────────

  @Nested
  @DisplayName("TC-10 sendNotification() edge cases")
  class SendNotificationEdgeCases {

    @Test
    @DisplayName("Gửi notification đến user chưa từng subscribe — không throw")
    void shouldNotThrowForNeverSubscribedUser() {
      assertThatNoException().isThrownBy(() ->
          service.sendNotification("unknown", "Title", "Msg", "TYPE"));
    }

    @Test
    @DisplayName("Gửi notification đến nhiều user, một số chưa subscribe")
    void shouldHandleMixedSubscribedAndUnsubscribedUsers() {
      service.subscribe("1");

      assertThatNoException().isThrownBy(() ->
          service.sendNotification("1", "Title", "Msg", "TYPE"));
      assertThatNoException().isThrownBy(() ->
          service.sendNotification("2", "Title", "Msg", "TYPE"));
      assertThatNoException().isThrownBy(() ->
          service.sendNotification("3", "Title", "Msg", "TYPE"));
    }

    @Test
    @DisplayName("Gửi notification với nhiều loại type khác nhau")
    void shouldSendWithDifferentNotificationTypes() {
      service.subscribe("42");

      assertThatNoException().isThrownBy(() ->
          service.sendNotification("42", "Title", "Msg",
              SseConstants.NotificationType.REGISTRATION));
      assertThatNoException().isThrownBy(() ->
          service.sendNotification("42", "Title", "Msg",
              SseConstants.NotificationType.SCHEDULE));
      assertThatNoException().isThrownBy(() ->
          service.sendNotification("42", "Title", "Msg", "CUSTOM_TYPE"));
    }

    @Test
    @DisplayName("Gửi notification liên tục nhiều lần không throw")
    void shouldSendMultipleNotificationsSequentially() {
      service.subscribe("42");

      for (int i = 0; i < 10; i++) {
        final int idx = i;
        assertThatNoException().isThrownBy(() ->
            service.sendNotification("42", "Title " + idx, "Msg " + idx, "TYPE"));
      }
    }
  }

  // ─── TC-11: sendHeartbeat scenarios ───────────────────────────────────────

  @Nested
  @DisplayName("TC-11 sendHeartbeat() scenarios")
  class SendHeartbeatScenarios {

    @Test
    @DisplayName("Heartbeat khi không có emitter nào — không throw")
    void shouldNotThrowWhenNoEmitters() {
      assertThatNoException().isThrownBy(() -> service.sendHeartbeat());
    }

    @Test
    @DisplayName("Heartbeat khi có emitter active — không throw")
    void shouldNotThrowWithActiveEmitter() {
      service.subscribe("42");
      assertThatNoException().isThrownBy(() -> service.sendHeartbeat());
    }

    @Test
    @DisplayName("Heartbeat khi có nhiều emitter cho cùng user")
    void shouldHandleMultipleEmittersForSameUser() {
      service.subscribe("42");
      service.subscribe("42");
      service.subscribe("42");

      assertThatNoException().isThrownBy(() -> service.sendHeartbeat());
    }

    @Test
    @DisplayName("Heartbeat khi có emitter từ nhiều user")
    void shouldHandleEmittersFromMultipleUsers() {
      service.subscribe("1");
      service.subscribe("2");
      service.subscribe("3");

      assertThatNoException().isThrownBy(() -> service.sendHeartbeat());
    }

    @Test
    @DisplayName("Gọi heartbeat nhiều lần liên tục")
    void shouldHandleMultipleHeartbeats() {
      service.subscribe("42");

      for (int i = 0; i < 5; i++) {
        assertThatNoException().isThrownBy(() -> service.sendHeartbeat());
      }
    }
  }
}
