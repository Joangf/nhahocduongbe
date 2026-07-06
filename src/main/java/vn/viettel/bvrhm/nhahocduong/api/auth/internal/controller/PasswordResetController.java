package vn.viettel.bvrhm.nhahocduong.api.auth.internal.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.bvrhm.nhahocduong.api.auth.ForgotPasswordRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.ResetPasswordRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.VerifyOtpRequest;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.OtpService;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.service.UserService;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/auth")
public class PasswordResetController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserService userService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        otpService.generateAndSendOtp(request.username(), request.email(), request.phoneNumber());
        return ResponseEntity.ok(Map.of("message", "Mã xác thực OTP đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        String resetToken = otpService.verifyOtp(request.email(), request.otp());
        return ResponseEntity.ok(Map.of("resetToken", resetToken));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) throws Exception {
        // Validate reset token và lấy email tương ứng
        String email = otpService.validateResetToken(request.resetToken());
        
        // Reset password
        userService.resetPassword(email, request.newPassword());
        
        // Đánh dấu token đã được sử dụng
        otpService.markTokenAsUsed(request.resetToken());
        
        return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại với mật khẩu mới."));
    }
}
