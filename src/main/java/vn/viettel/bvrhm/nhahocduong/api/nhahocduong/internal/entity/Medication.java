package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "nhahocduong_medication")
public class Medication {
  @Id
  @GeneratedValue(generator = "medication_id_generator")
  @SequenceGenerator(
      name = "medication_id_generator",
      sequenceName = "nhahocduong_medication_id_seq",
      allocationSize = 1)
  private Long id;

  @Column(name = "code")
  private String code;

  @Column(name = "name")
  private String name;

  @Column(name = "unit")
  private String unit;

  //  @ManyToMany(mappedBy = "medications")
  //  private List<Exam> exams;
}
