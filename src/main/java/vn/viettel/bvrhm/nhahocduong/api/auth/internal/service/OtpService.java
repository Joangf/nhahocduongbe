package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.OtpToken;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository.OtpTokenRepository;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.UserRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class OtpService {

    @Autowired
    private OtpTokenRepository otpTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.otp.expiration-minutes:5}")
    private int expirationMinutes;

    @Value("${app.otp.max-attempts-per-hour:5}")
    private int maxAttemptsPerHour;

    private final SecureRandom random = new SecureRandom();

    @Transactional
    public void generateAndSendOtp(String username, String email, String phoneNumber) {
        // Kiểm tra xem tài khoản khớp cả 3 thông tin có tồn tại không
        if (userRepository.findByUsernameAndEmailAndPhoneNumber(username, email, phoneNumber).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Thông tin tài khoản (Tên đăng nhập, Số điện thoại hoặc Email) không chính xác.");
        }

        // Rate limiting: kiểm tra số lượng OTP gửi trong 1 giờ
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long otpCount = otpTokenRepository.countByEmailAndCreatedAtAfter(email, oneHourAgo);
        if (otpCount >= maxAttemptsPerHour) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Bạn đã vượt quá giới hạn gửi OTP (tối đa 5 lần/giờ). Vui lòng thử lại sau.");
        }

        // Generate 6-digit OTP code
        String otpCode = String.format("%06d", random.nextInt(1000000));

        OtpToken otpToken = OtpToken.builder()
                .email(email)
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .verified(false)
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

        otpTokenRepository.save(otpToken);

        // Gửi qua email (chạy async bất đồng bộ)
        emailService.sendOtpEmail(email, otpCode, expirationMinutes, "forgot-password");
    }

    @Transactional
    public void generateAndSendRegisterOtp(String username, String email, String phoneNumber) {
        // Kiểm tra xem tên đăng nhập, email hoặc số điện thoại đã tồn tại chưa
        if (userRepository.getByUsername(username).isPresent()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Tên đăng nhập đã được sử dụng.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Email đã được sử dụng.");
        }
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Số điện thoại đã được sử dụng.");
        }

        // Rate limiting: kiểm tra số lượng OTP gửi trong 1 giờ
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long otpCount = otpTokenRepository.countByEmailAndCreatedAtAfter(email, oneHourAgo);
        if (otpCount >= maxAttemptsPerHour) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS, "Bạn đã vượt quá giới hạn gửi OTP (tối đa 5 lần/giờ). Vui lòng thử lại sau.");
        }

        // Generate 6-digit OTP code
        String otpCode = String.format("%06d", random.nextInt(1000000));

        OtpToken otpToken = OtpToken.builder()
                .email(email)
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .verified(false)
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

        otpTokenRepository.save(otpToken);

        // Gửi qua email (chạy async bất đồng bộ)
        emailService.sendOtpEmail(email, otpCode, expirationMinutes, "register");
    }

    @Transactional
    public void generateAndSendChangePasswordOtp(String username, String email, String phoneNumber) {
        // Kiểm tra xem tài khoản khớp cả 3 thông tin có tồn tại không
        if (userRepository.findByUsernameAndEmailAndPhoneNumber(username, email, phoneNumber).isEmpty()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Thông tin tài khoản (Tên đăng nhập, Số điện thoại hoặc Email) không chính xác.");
        }

        // Rate limiting: kiểm tra số lượng OTP gửi trong 1 giờ
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long otpCount = otpTokenRepository.countByEmailAndCreatedAtAfter(email, oneHourAgo);
        if (otpCount >= maxAttemptsPerHour) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS, "Bạn đã vượt quá giới hạn gửi OTP (tối đa 5 lần/giờ). Vui lòng thử lại sau.");
        }

        // Generate 6-digit OTP code
        String otpCode = String.format("%06d", random.nextInt(1000000));

        OtpToken otpToken = OtpToken.builder()
                .email(email)
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .verified(false)
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

        otpTokenRepository.save(otpToken);

        // Gửi qua email (chạy async bất đồng bộ)
        emailService.sendOtpEmail(email, otpCode, expirationMinutes, "change-password");
    }


    @Transactional
    public String verifyOtp(String email, String otpCode) {
        Optional<OtpToken> otpTokenMaybe = otpTokenRepository
                .findByEmailAndOtpCodeAndVerifiedFalseAndUsedFalse(email, otpCode);

        if (otpTokenMaybe.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã OTP không hợp lệ hoặc đã được sử dụng.");
        }

        OtpToken otpToken = otpTokenMaybe.get();
        if (otpToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã OTP đã hết hạn.");
        }

        // Đánh dấu đã xác thực OTP và sinh UUID reset token
        String resetToken = UUID.randomUUID().toString();
        otpToken.setVerified(true);
        otpToken.setResetToken(resetToken);
        otpTokenRepository.save(otpToken);

        return resetToken;
    }

    public String validateResetToken(String resetToken) {
        Optional<OtpToken> otpTokenMaybe = otpTokenRepository
                .findByResetTokenAndVerifiedTrueAndUsedFalse(resetToken);

        if (otpTokenMaybe.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token khôi phục mật khẩu không hợp lệ.");
        }

        OtpToken otpToken = otpTokenMaybe.get();
        if (otpToken.getExpiresAt().isBefore(LocalDateTime.now().minusMinutes(30))) { // reset token chỉ có hiệu lực 30p sau verify OTP
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token khôi phục mật khẩu đã hết hạn.");
        }

        return otpToken.getEmail();
    }

    @Transactional
    public void markTokenAsUsed(String resetToken) {
        Optional<OtpToken> otpTokenMaybe = otpTokenRepository
                .findByResetTokenAndVerifiedTrueAndUsedFalse(resetToken);
        if (otpTokenMaybe.isPresent()) {
            OtpToken otpToken = otpTokenMaybe.get();
            otpToken.setUsed(true);
            otpTokenRepository.save(otpToken);
        }
    }

    @Transactional
    public void cleanExpiredOtps() {
        // Xóa các OTP đã hết hạn từ 1 ngày trước để làm sạch DB
        otpTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now().minusDays(1));
    }
}
