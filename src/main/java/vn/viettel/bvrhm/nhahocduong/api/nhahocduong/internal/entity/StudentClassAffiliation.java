package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.AffiliationStatus;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter.AffiliationStatusConverter;

@Data
@Entity
@Table(name = "student_class_affiliation")
public class StudentClassAffiliation {

  @Id
  @GeneratedValue(generator = "student_class_affiliation_id_generator")
  @SequenceGenerator(
      name = "student_class_affiliation_id_generator",
      sequenceName = "student_class_affiliation_id_seq",
      allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private Patient student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "class_id", nullable = false)
  private Class studentClass;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "academic_year_id", nullable = false)
  private AcademicYear academicYear;

  @Convert(converter = AffiliationStatusConverter.class)
  @Column(name = "status", nullable = false)
  private AffiliationStatus status = AffiliationStatus.STUDYING;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "updated_date")
  private LocalDateTime updatedDate;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "updated_by")
  private String updatedBy;
}
