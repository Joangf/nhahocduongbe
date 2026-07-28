package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService Unit Tests")
class EmailServiceTest {

  @Mock
  private JavaMailSender mailSender;

  @InjectMocks
  private EmailService emailService;

  @Captor
  private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

  private MimeMessage realMimeMessage;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(emailService, "fromEmail", "test-noreply@nhahocduong.vn");
    Session session = Session.getInstance(new Properties());
    realMimeMessage = new MimeMessage(session);
  }

  @Nested
  @DisplayName("sendOtpEmail() — Happy Path (Email Generation)")
  class HappyPathTests {

    @Test
    @DisplayName("Send register OTP email — builds correct subject and invokes send()")
    void sendOtpEmail_registerType_sendsEmailSuccessfully() throws Exception {
      when(mailSender.createMimeMessage()).thenReturn(realMimeMessage);

      assertThatCode(() ->
          emailService.sendOtpEmail("student@nhahocduong.vn", "123456", 5, "register")
      ).doesNotThrowAnyException();

      verify(mailSender, times(1)).send(mimeMessageCaptor.capture());
      MimeMessage sentMessage = mimeMessageCaptor.getValue();

      assertThat(sentMessage.getSubject()).isEqualTo("Mã xác thực OTP - Đăng ký tài khoản");
      assertThat(sentMessage.getAllRecipients()[0].toString()).isEqualTo("student@nhahocduong.vn");
    }

    @Test
    @DisplayName("Send change-password OTP email — builds correct subject and invokes send()")
    void sendOtpEmail_changePasswordType_sendsEmailSuccessfully() throws Exception {
      when(mailSender.createMimeMessage()).thenReturn(realMimeMessage);

      assertThatCode(() ->
          emailService.sendOtpEmail("user@nhahocduong.vn", "654321", 10, "change-password")
      ).doesNotThrowAnyException();

      verify(mailSender, times(1)).send(mimeMessageCaptor.capture());
      MimeMessage sentMessage = mimeMessageCaptor.getValue();

      assertThat(sentMessage.getSubject()).isEqualTo("Mã xác thực OTP - Thay đổi mật khẩu");
      assertThat(sentMessage.getAllRecipients()[0].toString()).isEqualTo("user@nhahocduong.vn");
    }

    @Test
    @DisplayName("Send forgot-password (default) OTP email — builds correct subject and invokes send()")
    void sendOtpEmail_forgotPasswordType_sendsEmailSuccessfully() throws Exception {
      when(mailSender.createMimeMessage()).thenReturn(realMimeMessage);

      assertThatCode(() ->
          emailService.sendOtpEmail("admin@nhahocduong.vn", "888888", 15, "forgot-password")
      ).doesNotThrowAnyException();

      verify(mailSender, times(1)).send(mimeMessageCaptor.capture());
      MimeMessage sentMessage = mimeMessageCaptor.getValue();

      assertThat(sentMessage.getSubject()).isEqualTo("Mã xác thực OTP - Khôi phục mật khẩu");
      assertThat(sentMessage.getAllRecipients()[0].toString()).isEqualTo("admin@nhahocduong.vn");
    }

    @Test
    @DisplayName("Send unknown type OTP email — defaults to forgot-password flow")
    void sendOtpEmail_unknownType_defaultsToForgotPassword() throws Exception {
      when(mailSender.createMimeMessage()).thenReturn(realMimeMessage);

      assertThatCode(() ->
          emailService.sendOtpEmail("other@nhahocduong.vn", "999999", 5, "unknown-flow")
      ).doesNotThrowAnyException();

      verify(mailSender, times(1)).send(mimeMessageCaptor.capture());
      MimeMessage sentMessage = mimeMessageCaptor.getValue();

      assertThat(sentMessage.getSubject()).isEqualTo("Mã xác thực OTP - Khôi phục mật khẩu");
    }
  }

  @Nested
  @DisplayName("sendOtpEmail() — Edge Case 1 (SMTP Failure & Console Fallback)")
  class SmtpFailureTests {

    @Test
    @DisplayName("Simulate SMTP MailException on send() — catches gracefully and logs console fallback")
    void sendOtpEmail_smtpSendFailure_catchesAndLogsFallback() {
      when(mailSender.createMimeMessage()).thenReturn(realMimeMessage);
      doThrow(new MailSendException("SMTP connection refused"))
          .when(mailSender).send(any(MimeMessage.class));

      assertThatCode(() ->
          emailService.sendOtpEmail("fail@nhahocduong.vn", "111222", 5, "register")
      ).doesNotThrowAnyException();

      verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Simulate error on createMimeMessage() — catches gracefully without crashing")
    void sendOtpEmail_createMessageFailure_catchesAndLogsFallback() {
      when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail server down"));

      assertThatCode(() ->
          emailService.sendOtpEmail("error@nhahocduong.vn", "333444", 5, "change-password")
      ).doesNotThrowAnyException();

      verify(mailSender, times(1)).createMimeMessage();
    }
  }
}
