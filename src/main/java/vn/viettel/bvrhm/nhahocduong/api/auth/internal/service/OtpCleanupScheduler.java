package vn.viettel.bvrhm.nhahocduong.api.auth.internal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class OtpCleanupScheduler {

    private final Logger log = LoggerFactory.getLogger(OtpCleanupScheduler.class);

    @Autowired
    private OtpService otpService;

    // Chạy vào lúc 3h sáng mỗi ngày
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanExpiredOtps() {
        log.info("Starting daily cleanup of expired OTP tokens...");
        try {
            otpService.cleanExpiredOtps();
            log.info("Cleanup of expired OTP tokens completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred during OTP token cleanup", e);
        }
    }
}
