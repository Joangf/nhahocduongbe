package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@nhahocduong.vn}")
    private String fromEmail;

    @Async
    public void sendOtpEmail(String toEmail, String otpCode, int expirationMinutes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Mã xác thực OTP - Khôi phục mật khẩu");

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e5e7eb; border-radius: 8px; background-color: #ffffff;">
                    <div style="text-align: center; margin-bottom: 20px;">
                        <h2 style="color: #4f46e5; margin: 0; font-size: 24px;">Xác Thực Tài Khoản</h2>
                        <p style="color: #6b7280; font-size: 14px; margin-top: 5px;">Hệ thống Quản lý Nha Học Đường</p>
                    </div>
                    <hr style="border: 0; border-top: 1px solid #e5e7eb; margin: 20px 0;">
                    <div style="color: #374151; font-size: 16px; line-height: 1.5;">
                        <p>Xin chào,</p>
                        <p>Chúng tôi nhận được yêu cầu khôi phục mật khẩu cho tài khoản liên kết với email này. Vui lòng sử dụng mã OTP dưới đây để hoàn tất quá trình:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <span style="display: inline-block; padding: 12px 30px; background-color: #f3f4f6; color: #4f46e5; font-size: 32px; font-weight: bold; letter-spacing: 5px; border-radius: 6px; border: 1px dashed #4f46e5;">
                                %s
                            </span>
                        </div>
                        <p style="font-size: 14px; color: #ef4444; font-weight: bold;">Lưu ý: Mã OTP này sẽ hết hạn sau %d phút.</p>
                        <p style="font-size: 13px; color: #6b7280; margin-top: 30px;">Nếu bạn không yêu cầu hành động này, vui lòng bỏ qua email hoặc liên hệ với ban quản trị nếu thấy nghi ngờ.</p>
                    </div>
                    <hr style="border: 0; border-top: 1px solid #e5e7eb; margin: 20px 0;">
                    <div style="text-align: center; font-size: 12px; color: #9ca3af;">
                        <p>© 2026 Chương trình Nha Học Đường - BVRHM. Bảo lưu mọi quyền.</p>
                    </div>
                </div>
                """.formatted(otpCode, expirationMinutes);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Email OTP sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email via SMTP to {}. Error: {}", toEmail, e.getMessage());
            log.warn("==================================================================");
            log.warn("FALLBACK: ĐÃ XẢY RA LỖI SMTP. MÃ OTP CỦA USER [{}] LÀ: ---> {} <---", toEmail, otpCode);
            log.warn("==================================================================");
        }
    }
}
