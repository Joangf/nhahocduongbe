package vn.viettel.bvrhm.nhahocduong.api.auth.internal.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.BaseEntity;

/**
 * @author: longlb1
 * @since: 20-Sep-23
 */
@Entity
@Data
@NoArgsConstructor
public class Policy extends BaseEntity {
  @OneToMany List<Resource> resourceList;
  @Id
  @GeneratedValue(generator = "policy_id_generator")
  @SequenceGenerator(
      name = "policy_id_generator",
      sequenceName = "nhahocduong_policy_id_seq",
      allocationSize = 1)
  @Column(name = "id")
  private Long id;
  @Column(name = "code")
  private String code;
  @Column(name = "description")
  private String description;
}
