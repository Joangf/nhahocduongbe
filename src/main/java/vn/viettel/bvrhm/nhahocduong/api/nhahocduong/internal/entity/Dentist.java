package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "nhahocduong_dentist")
public class Dentist {
  @Id
  @GeneratedValue(generator = "dentist_id_generator")
  @SequenceGenerator(
      name = "dentist_id_generator",
      sequenceName = "nhahocduong_dentist_id_seq",
      allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "title")
  private String title;
}
