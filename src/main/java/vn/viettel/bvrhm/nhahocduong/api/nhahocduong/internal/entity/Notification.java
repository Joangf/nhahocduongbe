package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nhahocduong_notification")
public class Notification {

  @Id
  @GeneratedValue(generator = "notification_id_generator")
  @SequenceGenerator(
      name = "notification_id_generator",
      sequenceName = "nhahocduong_notification_id_seq",
      allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @Column(name = "recipient_id", nullable = false)
  private Long recipientId;

  @Column(name = "campaign_id")
  private Long campaignId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "message", nullable = false, columnDefinition = "TEXT")
  private String message;

  @Column(name = "is_read", nullable = false)
  private Boolean isRead = false;

  @Column(name = "status", nullable = false)
  private Boolean status = true;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Column(name = "updated_date", nullable = false)
  private LocalDateTime updatedDate;

  @PrePersist
  protected void onCreate() {
    createdDate = LocalDateTime.now();
    updatedDate = LocalDateTime.now();
    if (isRead == null) {
      isRead = false;
    }
    if (status == null) {
      status = true;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedDate = LocalDateTime.now();
  }
}
