package vn.viettel.bvrhm.nhahocduong.api.common.internal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author: longlb1
 * @since: 19-Sep-23
 */
@Getter
@Setter
// @Inheritance
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
  @Column(name = "STATUS", nullable = false)
  private Boolean status;

  @CreatedDate
  @Column(name = "CREATED_DATE", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Column(name = "UPDATED_DATE", nullable = false)
  private LocalDateTime updatedDate;

  //    @CreatedBy
  @Column(name = "CREATED_BY", updatable = false)
  private String createdBy;

  //    @LastModifiedBy
  @Column(name = "UPDATED_BY")
  private String updatedBy;
}
