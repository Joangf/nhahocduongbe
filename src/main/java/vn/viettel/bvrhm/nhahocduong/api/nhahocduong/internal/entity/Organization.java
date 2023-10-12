package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.BaseEntity;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Grade;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.OrganizationType;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter.OrganizationTypeConverter;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.User;

@Data
@Entity
@Table(name = "nhahocduong_organization")
public class Organization extends BaseEntity {
  @Id
  @GeneratedValue(generator = "organization_id_generator")
  @SequenceGenerator(
      name = "organization_id_generator",
      sequenceName = "nhahocduong_organization_id_seq",
      allocationSize = 1)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "code")
  private String code;

  @Column(name = "address")
  private String address;

  @Column(name = "area_code")
  private String areaCode;

  @OneToOne
  @JoinColumn(name = "head_member", referencedColumnName = "id")
  private User headMember;

  @OneToMany(mappedBy = "organization")
  private List<User> directMembers;

  @SuppressWarnings("JpaAttributeTypeInspection")
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "classes")
  private Map<Grade, List<String>> classes;

  @OneToOne
  @JoinColumn(name = "parent", referencedColumnName = "id")
  private Organization parent;

  @Column(name = "type")
  @Convert(converter = OrganizationTypeConverter.class)
  private OrganizationType type;

  @Column(name = "status")
  private Boolean status;
}
