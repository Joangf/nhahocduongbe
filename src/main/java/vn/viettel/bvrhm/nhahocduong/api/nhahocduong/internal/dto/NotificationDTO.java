package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record NotificationDTO(
    Long id,
    Long recipientId,
    Long campaignId,
    String title,
    String message,
    Boolean isRead,
    LocalDateTime createdDate) implements Serializable {}
