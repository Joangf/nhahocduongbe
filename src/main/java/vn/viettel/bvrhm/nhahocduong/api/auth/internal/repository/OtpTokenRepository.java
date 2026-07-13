package vn.viettel.bvrhm.nhahocduong.api.auth.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity.OtpToken;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findByEmailAndOtpCodeAndVerifiedFalseAndUsedFalse(String email, String otpCode);

    Optional<OtpToken> findByResetTokenAndVerifiedTrueAndUsedFalse(String resetToken);

    long countByEmailAndCreatedAtAfter(String email, LocalDateTime time);

    void deleteByExpiresAtBefore(LocalDateTime time);
}
