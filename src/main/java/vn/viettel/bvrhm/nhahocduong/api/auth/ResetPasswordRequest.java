package vn.viettel.bvrhm.nhahocduong.api.auth;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
    @NotBlank(message = "Token không được để trống")
    String resetToken,

    @NotBlank(message = "Mật khẩu mới không được để trống")
    String newPassword
) {}
