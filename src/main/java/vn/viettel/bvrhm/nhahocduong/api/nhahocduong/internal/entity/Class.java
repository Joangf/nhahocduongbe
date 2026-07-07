package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import lombok.Data;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.BaseEntity;

@Data
@Entity
@Table(name = "class")
public class Class extends BaseEntity {

  @Id
  @GeneratedValue(generator = "class_id_generator")
  @SequenceGenerator(
      name = "class_id_generator",
      sequenceName = "class_id_seq",
      allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "grade", nullable = false)
  private String grade;

  @Column(name = "room", nullable = false)
  private String room;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_id", nullable = false)
  private Organization school;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "academic_year_id", nullable = false)
  private AcademicYear academicYear;
}
