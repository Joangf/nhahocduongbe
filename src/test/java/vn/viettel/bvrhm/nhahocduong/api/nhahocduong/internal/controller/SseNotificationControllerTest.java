package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.JwtService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.SseNotificationService;

@DisplayName("SseNotificationController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class SseNotificationControllerTest {

  @Mock SseNotificationService service;
  @Mock JwtService jwtService;
  @InjectMocks SseNotificationController controller;

  @Nested
  @DisplayName("TC-01 createTicket()")
  class CreateTicket {

    @Test
    @DisplayName("Tạo ticket thành công với Bearer token hợp lệ")
    void shouldCreateTicket() {
      when(jwtService.isTokenValid("valid-token")).thenReturn(true);
      when(jwtService.extractUserId("valid-token")).thenReturn("42");
      when(service.createTicket("42")).thenReturn("ticket-123");

      Map<String, String> result = controller.createTicket("Bearer valid-token");

      assertThat(result).containsKey("ticket");
      assertThat(result.get("ticket")).isEqualTo("ticket-123");
    }

    @Test
    @DisplayName("Ném 401 khi Authorization header không có Bearer")
    void shouldThrowWhenNoBearer() {
      assertThatThrownBy(() -> controller.createTicket(null))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("Thiếu Authorization header");
    }

    @Test
    @DisplayName("Ném 401 khi token không hợp lệ")
    void shouldThrowWhenTokenInvalid() {
      when(jwtService.isTokenValid("bad-token")).thenReturn(false);

      assertThatThrownBy(() -> controller.createTicket("Bearer bad-token"))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("Token không hợp lệ");
    }
  }

  @Nested
  @DisplayName("TC-02 subscribe()")
  class Subscribe {

    @Test
    @DisplayName("Subscribe thành công với ticket hợp lệ")
    void shouldSubscribeWithValidTicket() {
      when(service.consumeTicket("valid-ticket")).thenReturn("42");
      var emitter = new org.springframework.web.servlet.mvc.method.annotation.SseEmitter();
      when(service.subscribe("42")).thenReturn(emitter);

      var result = controller.subscribe("valid-ticket");

      assertThat(result).isSameAs(emitter);
    }

    @Test
    @DisplayName("Ném 401 khi ticket không hợp lệ")
    void shouldThrowWhenTicketInvalid() {
      when(service.consumeTicket("bad-ticket")).thenReturn(null);

      assertThatThrownBy(() -> controller.subscribe("bad-ticket"))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("Ticket không hợp lệ");
    }

    @Test
    @DisplayName("Ném 401 khi ticket null")
    void shouldThrowWhenTicketNull() {
      assertThatThrownBy(() -> controller.subscribe(null))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("Ticket không hợp lệ");
    }
  }
}
