package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "nhahocduong_disease")
public class Disease {
  @Id
  @GeneratedValue(generator = "disease_id_generator")
  @SequenceGenerator(
      name = "disease_id_generator",
      sequenceName = "nhahocduong_disease_id_seq",
      allocationSize = 1)
  private Long id;

  @Column(name = "code")
  private String code;

  @Column(name = "name")
  private String name;
}
