package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Tooth;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "nhahocduong_teeth_record")
public class TeethRecord {
  @Id
  @GeneratedValue(generator = "teeth_record_id_generator")
  @SequenceGenerator(
      name = "teeth_record_id_generator",
      sequenceName = "nhahocduong_teeth_record_id_seq",
      allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @SuppressWarnings("JpaAttributeTypeInspection")
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "record")
  private Map<Tooth, ToothCondition> record;
}
