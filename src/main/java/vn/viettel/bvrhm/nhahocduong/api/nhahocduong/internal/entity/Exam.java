package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.Where;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.BaseEntity;

@Entity
@Data
@Table(name = "nhahocduong_exam")
public class Exam extends BaseEntity {
  @Id
  @GeneratedValue(generator = "exam_id_generator")
  @SequenceGenerator(
      name = "exam_id_generator",
      sequenceName = "nhahocduong_exam_id_seq",
      allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @Column(name = "patient_id")
  private Long patientId;

  @Column(name = "dentist_id")
  private Long dentistId;

  @Column(name = "organization_id")
  private Long organizationId;

  @Column(name = "class")
  private String schoolClass;

  @Column(name = "year")
  private String year;

  @Column(name = "profile_number")
  private Long profileNumber;

  //  @Convert(converter = ExamPlaceJpaConverter.class)
  //  @Column(name = "exam_place")
  //  private ExamPlace examPlace;

  @Column(name = "date")
  private LocalDate date;

  @Column(name = "teeth_record_id")
  private Long teethRecordId;

  @Column(name = "plaque_record_id")
  private Long plaqueRecordId;

  @Column(name = "tartar_record_id")
  private Long tartarRecordId;

  @ManyToMany()
  @JoinTable(
      name = "nhahocduong_exam_disease",
      joinColumns = {@JoinColumn(name = "exam_id")},
      inverseJoinColumns = {@JoinColumn(name = "disease_id")})
  private List<Disease> chronicConditions;

  @OneToMany(mappedBy = "exam")
  @Where(clause = "status = true")
  private List<TreatmentRecord> treatmentRecords;

  //  @SuppressWarnings("JpaAttributeTypeInspection")
  //  @JdbcTypeCode(SqlTypes.JSON)
  //  @Column(name = "prescription")
  //  private List<PrescriptionItem> prescription;

  //  @SuppressWarnings("JpaAttributeTypeInspection")
  //  @JdbcTypeCode(SqlTypes.JSON)
  //  @Column(name = "treatment_record")
  //  private TreatmentRecord treatmentRecord;
  //
  //  @Column(name = "diagnosis")
  //  private String diagnosis;

}
