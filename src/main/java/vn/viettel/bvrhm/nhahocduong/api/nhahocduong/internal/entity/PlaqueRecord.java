package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import lombok.Data;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.PlaqueCondition;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter.PlaqueConditionJpaConverter;

@Entity
@Data
@Table(name = "nhahocduong_plaque_record")
public class PlaqueRecord {
  @Id
  @GeneratedValue(generator = "plaque_record_id_generator")
  @SequenceGenerator(
      name = "plaque_record_id_generator",
      sequenceName = "nhahocduong_plaque_record_id_seq",
      allocationSize = 1)
  private Long id;

  @Convert(converter = PlaqueConditionJpaConverter.class)
  @Column(name = "17-16n")
  private PlaqueCondition _17_16n;

  @Convert(converter = PlaqueConditionJpaConverter.class)
  @Column(name = "11n")
  private PlaqueCondition _11n;

  @Convert(converter = PlaqueConditionJpaConverter.class)
  @Column(name = "26-27n")
  private PlaqueCondition _26_27n;

  @Convert(converter = PlaqueConditionJpaConverter.class)
  @Column(name = "47-46t")
  private PlaqueCondition _47_46t;

  @Convert(converter = PlaqueConditionJpaConverter.class)
  @Column(name = "31n")
  private PlaqueCondition _31n;

  @Convert(converter = PlaqueConditionJpaConverter.class)
  @Column(name = "36-37t")
  private PlaqueCondition _36_37t;
}
