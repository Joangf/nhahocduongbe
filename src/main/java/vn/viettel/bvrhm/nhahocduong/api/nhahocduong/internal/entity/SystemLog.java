package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Table(name = "system_log")
public class SystemLog {

  @Id
  @GeneratedValue(generator = "system_log_id_generator")
  @SequenceGenerator(
      name = "system_log_id_generator",
      sequenceName = "system_log_id_seq",
      allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @Column(name = "session_id", nullable = false)
  private String sessionId;

  @Column(name = "action", nullable = false)
  private String action;

  @Column(name = "entity_type", nullable = false)
  private String entityType;

  @Column(name = "entity_id", nullable = false)
  private Long entityId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "old_value", columnDefinition = "jsonb")
  private String oldValue;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "new_value", columnDefinition = "jsonb")
  private String newValue;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "created_by")
  private String createdBy;
}
