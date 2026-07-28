package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.OtpToken;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository.OtpTokenRepository;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.User;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("OtpService Unit Tests")
class OtpServiceTest {

    @Mock private OtpTokenRepository otpTokenRepository;
    @Mock private UserRepository userRepository;
    @Mock private EmailService emailService;

    @InjectMocks private OtpService otpService;

    @Captor private ArgumentCaptor<OtpToken> otpTokenCaptor;

    // ─── Helper methods ────────────────────────────────────────────────

    private void setDefaultFieldValues() {
        ReflectionTestUtils.setField(otpService, "expirationMinutes", 5);
        ReflectionTestUtils.setField(otpService, "maxAttemptsPerHour", 5);
    }

    private OtpToken createMockOtpToken(String email, String otpCode, boolean expired) {
        return OtpToken.builder()
            .id(1L)
            .email(email)
            .otpCode(otpCode)
            .expiresAt(expired ? LocalDateTime.now().minusMinutes(10) : LocalDateTime.now().plusMinutes(5))
            .verified(false)
            .used(false)
            .createdAt(LocalDateTime.now())
            .build();
    }

    private OtpToken createVerifiedOtpToken(String email, String resetToken, boolean expired) {
        return OtpToken.builder()
            .id(2L)
            .email(email)
            .otpCode("123456")
            .resetToken(resetToken)
            .expiresAt(expired ? LocalDateTime.now().minusHours(1) : LocalDateTime.now().plusMinutes(25))
            .verified(true)
            .used(false)
            .createdAt(LocalDateTime.now())
            .build();
    }

    private User createMockUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        return user;
    }

    // ─── generateAndSendOtp() Tests ────────────────────────────────────

    @Nested
    @DisplayName("generateAndSendOtp()")
    class GenerateAndSendOtpTests {

        @Test
        @DisplayName("Happy path: generates 6-digit OTP and saves to repository")
        void generateAndSendOtp_happyPath_savesOtpAndSendsEmail() {
            // Arrange
            setDefaultFieldValues();
            when(userRepository.findByUsernameAndEmailAndPhoneNumber("testuser", "test@test.com", "0901234567"))
                .thenReturn(Optional.of(createMockUser()));
            when(otpTokenRepository.countByEmailAndCreatedAtAfter(eq("test@test.com"), any(LocalDateTime.class)))
                .thenReturn(0L);

            // Act
            otpService.generateAndSendOtp("testuser", "test@test.com", "0901234567");

            // Assert — OTP is saved with correct structure
            verify(otpTokenRepository).save(otpTokenCaptor.capture());
            OtpToken saved = otpTokenCaptor.getValue();
            assertThat(saved.getEmail()).isEqualTo("test@test.com");
            assertThat(saved.getOtpCode()).hasSize(6);
            assertThat(saved.getOtpCode()).matches("\\d{6}");
            assertThat(saved.getVerified()).isFalse();
            assertThat(saved.getUsed()).isFalse();
            assertThat(saved.getExpiresAt()).isAfter(LocalDateTime.now());

            // Assert — email was sent
            verify(emailService).sendOtpEmail(eq("test@test.com"), anyString(), eq(5), eq("forgot-password"));
        }

        @Test
        @DisplayName("User not found throws 404 NOT_FOUND")
        void generateAndSendOtp_userNotFound_throws404() {
            // Arrange
            setDefaultFieldValues();
            when(userRepository.findByUsernameAndEmailAndPhoneNumber("unknown", "bad@test.com", "0000000000"))
                .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> otpService.generateAndSendOtp("unknown", "bad@test.com", "0000000000"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });

            verify(otpTokenRepository, never()).save(any());
            verify(emailService, never()).sendOtpEmail(anyString(), anyString(), anyInt(), anyString());
        }

        @Test
        @DisplayName("TC-REG-03: Rate limiting — ≥5 OTPs in past hour throws 429 TOO_MANY_REQUESTS")
        void generateAndSendOtp_rateLimitExceeded_throws429() {
            // Arrange
            setDefaultFieldValues();
            when(userRepository.findByUsernameAndEmailAndPhoneNumber("testuser", "test@test.com", "0901234567"))
                .thenReturn(Optional.of(createMockUser()));
            when(otpTokenRepository.countByEmailAndCreatedAtAfter(eq("test@test.com"), any(LocalDateTime.class)))
                .thenReturn(5L);

            // Act & Assert
            assertThatThrownBy(() -> otpService.generateAndSendOtp("testuser", "test@test.com", "0901234567"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
                });

            verify(otpTokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("Rate limit boundary — exactly 4 OTPs allows generation")
        void generateAndSendOtp_belowRateLimit_succeeds() {
            // Arrange
            setDefaultFieldValues();
            when(userRepository.findByUsernameAndEmailAndPhoneNumber("testuser", "test@test.com", "0901234567"))
                .thenReturn(Optional.of(createMockUser()));
            when(otpTokenRepository.countByEmailAndCreatedAtAfter(eq("test@test.com"), any(LocalDateTime.class)))
                .thenReturn(4L);

            // Act
            otpService.generateAndSendOtp("testuser", "test@test.com", "0901234567");

            // Assert
            verify(otpTokenRepository).save(any(OtpToken.class));
            verify(emailService).sendOtpEmail(eq("test@test.com"), anyString(), eq(5), eq("forgot-password"));
        }
    }

    // ─── generateAndSendRegisterOtp() Tests ────────────────────────────

    @Nested
    @DisplayName("generateAndSendRegisterOtp()")
    class GenerateAndSendRegisterOtpTests {

        @Test
        @DisplayName("Happy path: generates OTP for new user registration")
        void generateAndSendRegisterOtp_happyPath_savesAndSendsEmail() {
            // Arrange
            setDefaultFieldValues();
            when(userRepository.getByUsername("newuser")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
            when(userRepository.findByPhoneNumber("0909090909")).thenReturn(Optional.empty());
            when(otpTokenRepository.countByEmailAndCreatedAtAfter(eq("new@test.com"), any(LocalDateTime.class)))
                .thenReturn(0L);

            // Act
            otpService.generateAndSendRegisterOtp("newuser", "new@test.com", "0909090909");

            // Assert
            verify(otpTokenRepository).save(otpTokenCaptor.capture());
            OtpToken saved = otpTokenCaptor.getValue();
            assertThat(saved.getOtpCode()).matches("\\d{6}");
            assertThat(saved.getEmail()).isEqualTo("new@test.com");
            verify(emailService).sendOtpEmail(eq("new@test.com"), anyString(), eq(5), eq("register"));
        }

        @Test
        @DisplayName("Existing username throws 409 CONFLICT")
        void generateAndSendRegisterOtp_existingUsername_throws409() {
            // Arrange
            setDefaultFieldValues();
            when(userRepository.getByUsername("existinguser")).thenReturn(Optional.of(createMockUser()));

            // Act & Assert
            assertThatThrownBy(() -> otpService.generateAndSendRegisterOtp("existinguser", "new@test.com", "0909090909"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        }

        @Test
        @DisplayName("Existing email throws 409 CONFLICT")
        void generateAndSendRegisterOtp_existingEmail_throws409() {
            // Arrange
            setDefaultFieldValues();
            when(userRepository.getByUsername("newuser")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("existing@test.com")).thenReturn(Optional.of(createMockUser()));

            // Act & Assert
            assertThatThrownBy(() -> otpService.generateAndSendRegisterOtp("newuser", "existing@test.com", "0909090909"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        }

        @Test
        @DisplayName("Existing phone throws 409 CONFLICT")
        void generateAndSendRegisterOtp_existingPhone_throws409() {
            // Arrange
            setDefaultFieldValues();
            when(userRepository.getByUsername("newuser")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
            when(userRepository.findByPhoneNumber("0901234567")).thenReturn(Optional.of(createMockUser()));

            // Act & Assert
            assertThatThrownBy(() -> otpService.generateAndSendRegisterOtp("newuser", "new@test.com", "0901234567"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        }

        @Test
        @DisplayName("TC-REG-03: Rate limiting for register OTP throws 429")
        void generateAndSendRegisterOtp_rateLimitExceeded_throws429() {
            // Arrange
            setDefaultFieldValues();
            when(userRepository.getByUsername("newuser")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
            when(userRepository.findByPhoneNumber("0909090909")).thenReturn(Optional.empty());
            when(otpTokenRepository.countByEmailAndCreatedAtAfter(eq("new@test.com"), any(LocalDateTime.class)))
                .thenReturn(5L);

            // Act & Assert
            assertThatThrownBy(() -> otpService.generateAndSendRegisterOtp("newuser", "new@test.com", "0909090909"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS));
        }
    }

    // ─── generateAndSendChangePasswordOtp() Tests ──────────────────────

    @Nested
    @DisplayName("generateAndSendChangePasswordOtp()")
    class GenerateAndSendChangePasswordOtpTests {

        @Test
        @DisplayName("Happy path: sends change-password OTP")
        void generateAndSendChangePasswordOtp_happyPath_savesAndSends() {
            // Arrange
            setDefaultFieldValues();
            when(userRepository.findByUsernameAndEmailAndPhoneNumber("testuser", "test@test.com", "0901234567"))
                .thenReturn(Optional.of(createMockUser()));
            when(otpTokenRepository.countByEmailAndCreatedAtAfter(eq("test@test.com"), any(LocalDateTime.class)))
                .thenReturn(0L);

            // Act
            otpService.generateAndSendChangePasswordOtp("testuser", "test@test.com", "0901234567");

            // Assert
            verify(otpTokenRepository).save(otpTokenCaptor.capture());
            assertThat(otpTokenCaptor.getValue().getOtpCode()).matches("\\d{6}");
            verify(emailService).sendOtpEmail(eq("test@test.com"), anyString(), eq(5), eq("change-password"));
        }

        @Test
        @DisplayName("User not found throws 404")
        void generateAndSendChangePasswordOtp_userNotFound_throws404() {
            // Arrange
            setDefaultFieldValues();
            when(userRepository.findByUsernameAndEmailAndPhoneNumber("unknown", "bad@test.com", "0000000000"))
                .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> otpService.generateAndSendChangePasswordOtp("unknown", "bad@test.com", "0000000000"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        }

        @Test
        @DisplayName("Rate limit exceeded throws 429")
        void generateAndSendChangePasswordOtp_rateLimitExceeded_throws429() {
            // Arrange
            setDefaultFieldValues();
            when(userRepository.findByUsernameAndEmailAndPhoneNumber("testuser", "test@test.com", "0901234567"))
                .thenReturn(Optional.of(createMockUser()));
            when(otpTokenRepository.countByEmailAndCreatedAtAfter(eq("test@test.com"), any(LocalDateTime.class)))
                .thenReturn(6L);

            // Act & Assert
            assertThatThrownBy(() -> otpService.generateAndSendChangePasswordOtp("testuser", "test@test.com", "0901234567"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS));
        }
    }

    // ─── verifyOtp() Tests ─────────────────────────────────────────────

    @Nested
    @DisplayName("verifyOtp()")
    class VerifyOtpTests {

        @Test
        @DisplayName("Happy path: valid OTP returns reset token and marks as verified")
        void verifyOtp_validOtp_returnsResetTokenAndSaves() {
            // Arrange
            OtpToken otpToken = createMockOtpToken("test@test.com", "654321", false);
            when(otpTokenRepository.findByEmailAndOtpCodeAndVerifiedFalseAndUsedFalse("test@test.com", "654321"))
                .thenReturn(Optional.of(otpToken));

            // Act
            String resetToken = otpService.verifyOtp("test@test.com", "654321");

            // Assert
            assertThat(resetToken).isNotBlank();
            // UUID format check
            assertThat(resetToken).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

            verify(otpTokenRepository).save(otpTokenCaptor.capture());
            OtpToken saved = otpTokenCaptor.getValue();
            assertThat(saved.getVerified()).isTrue();
            assertThat(saved.getResetToken()).isEqualTo(resetToken);
        }

        @Test
        @DisplayName("Invalid OTP code throws 400 BAD_REQUEST")
        void verifyOtp_invalidCode_throws400() {
            // Arrange
            when(otpTokenRepository.findByEmailAndOtpCodeAndVerifiedFalseAndUsedFalse("test@test.com", "000000"))
                .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> otpService.verifyOtp("test@test.com", "000000"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));

            verify(otpTokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("TC-REG-04: Expired OTP throws 400 BAD_REQUEST")
        void verifyOtp_expiredOtp_throws400() {
            // Arrange
            OtpToken expiredToken = createMockOtpToken("test@test.com", "654321", true);
            when(otpTokenRepository.findByEmailAndOtpCodeAndVerifiedFalseAndUsedFalse("test@test.com", "654321"))
                .thenReturn(Optional.of(expiredToken));

            // Act & Assert
            assertThatThrownBy(() -> otpService.verifyOtp("test@test.com", "654321"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(rse.getReason()).contains("hết hạn");
                });

            // Verify OTP was NOT saved (not verified)
            verify(otpTokenRepository, never()).save(any());
        }
    }

    // ─── validateResetToken() Tests ────────────────────────────────────

    @Nested
    @DisplayName("validateResetToken()")
    class ValidateResetTokenTests {

        @Test
        @DisplayName("Valid reset token returns associated email")
        void validateResetToken_validToken_returnsEmail() {
            // Arrange
            OtpToken verifiedToken = createVerifiedOtpToken("test@test.com", "valid-reset-token", false);
            when(otpTokenRepository.findByResetTokenAndVerifiedTrueAndUsedFalse("valid-reset-token"))
                .thenReturn(Optional.of(verifiedToken));

            // Act
            String email = otpService.validateResetToken("valid-reset-token");

            // Assert
            assertThat(email).isEqualTo("test@test.com");
        }

        @Test
        @DisplayName("Invalid reset token throws 400 BAD_REQUEST")
        void validateResetToken_invalidToken_throws400() {
            // Arrange
            when(otpTokenRepository.findByResetTokenAndVerifiedTrueAndUsedFalse("invalid-token"))
                .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> otpService.validateResetToken("invalid-token"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        }

        @Test
        @DisplayName("Expired reset token (>30 min after verify) throws 400 BAD_REQUEST")
        void validateResetToken_expiredToken_throws400() {
            // Arrange — token with expiresAt far in the past
            OtpToken expiredToken = OtpToken.builder()
                .id(3L)
                .email("test@test.com")
                .otpCode("123456")
                .resetToken("expired-reset-token")
                .expiresAt(LocalDateTime.now().minusHours(2))  // Well past the 30-min window
                .verified(true)
                .used(false)
                .build();
            when(otpTokenRepository.findByResetTokenAndVerifiedTrueAndUsedFalse("expired-reset-token"))
                .thenReturn(Optional.of(expiredToken));

            // Act & Assert
            assertThatThrownBy(() -> otpService.validateResetToken("expired-reset-token"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        }
    }

    // ─── markTokenAsUsed() Tests ───────────────────────────────────────

    @Nested
    @DisplayName("markTokenAsUsed()")
    class MarkTokenAsUsedTests {

        @Test
        @DisplayName("Existing token is marked as used")
        void markTokenAsUsed_existingToken_marksUsed() {
            // Arrange
            OtpToken token = createVerifiedOtpToken("test@test.com", "reset-token", false);
            when(otpTokenRepository.findByResetTokenAndVerifiedTrueAndUsedFalse("reset-token"))
                .thenReturn(Optional.of(token));

            // Act
            otpService.markTokenAsUsed("reset-token");

            // Assert
            verify(otpTokenRepository).save(otpTokenCaptor.capture());
            assertThat(otpTokenCaptor.getValue().getUsed()).isTrue();
        }

        @Test
        @DisplayName("Non-existent token does nothing")
        void markTokenAsUsed_nonExistentToken_doesNothing() {
            // Arrange
            when(otpTokenRepository.findByResetTokenAndVerifiedTrueAndUsedFalse("missing"))
                .thenReturn(Optional.empty());

            // Act
            otpService.markTokenAsUsed("missing");

            // Assert
            verify(otpTokenRepository, never()).save(any());
        }
    }

    // ─── cleanExpiredOtps() Tests ──────────────────────────────────────

    @Nested
    @DisplayName("cleanExpiredOtps()")
    class CleanExpiredOtpsTests {

        @Test
        @DisplayName("Deletes OTPs expired more than 1 day ago")
        void cleanExpiredOtps_callsDeleteWithCorrectCutoff() {
            // Act
            otpService.cleanExpiredOtps();

            // Assert
            ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
            verify(otpTokenRepository).deleteByExpiresAtBefore(dateCaptor.capture());
            assertThat(dateCaptor.getValue()).isBefore(LocalDateTime.now().minusHours(23));
        }
    }
}
