package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.BaseEntity;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Tooth;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothTreatment;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.PrescriptionItem;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter.ToothJpaConverter;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter.ToothTreatmentJpaConverter;

@Entity
@Table(name = "nhahocduong_treatment_record")
@Data
public class TreatmentRecord extends BaseEntity {
  @Id
  @GeneratedValue(generator = "treatment_record_id_generator")
  @SequenceGenerator(
      name = "treatment_record_id_generator",
      sequenceName = "nhahocduong_treatment_record_id_seq",
      allocationSize = 1)
  private Long id;

  @Column(name = "treatment_service")
  @Convert(converter = ToothTreatmentJpaConverter.class)
  private ToothTreatment service;

  @Column(name = "dentist_name")
  private String dentistName;

  @Column(name = "diagnosis")
  private String diagnosis;

  @Column(name = "tooth")
  @Convert(converter = ToothJpaConverter.class)
  private Tooth tooth;

  @SuppressWarnings("JpaAttributeTypeInspection")
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "prescription")
  private List<PrescriptionItem> prescription;

  @ManyToOne
  @JoinColumn(name = "exam")
  private Exam exam;
}
