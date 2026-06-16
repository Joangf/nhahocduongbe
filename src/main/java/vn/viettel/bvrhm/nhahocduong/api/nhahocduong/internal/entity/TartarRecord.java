package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import lombok.Data;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.TartarCondition;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter.TartarConditionJpaConverter;

@Entity
@Data
@Table(name = "nhahocduong_tartar_record")
public class TartarRecord {
  @Id
  @GeneratedValue(generator = "tartar_record_id_generator")
  @SequenceGenerator(
      name = "tartar_record_id_generator",
      sequenceName = "nhahocduong_tartar_record_id_seq",
      allocationSize = 1)
  private Long id;

  @Convert(converter = TartarConditionJpaConverter.class)
  @Column(name = "17-16n")
  private TartarCondition _17_16n;

  @Convert(converter = TartarConditionJpaConverter.class)
  @Column(name = "11n")
  private TartarCondition _11n;

  @Convert(converter = TartarConditionJpaConverter.class)
  @Column(name = "26-27n")
  private TartarCondition _26_27n;

  @Convert(converter = TartarConditionJpaConverter.class)
  @Column(name = "47-46t")
  private TartarCondition _47_46t;

  @Convert(converter = TartarConditionJpaConverter.class)
  @Column(name = "31n")
  private TartarCondition _31n;

  @Convert(converter = TartarConditionJpaConverter.class)
  @Column(name = "36-37t ")
  private TartarCondition _36_37t;
}
