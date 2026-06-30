package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.BaseEntity;

@Data
@Entity
@Table(name = "nhahocduong_exam_schedule")
public class ExamSchedule extends BaseEntity {
  @Id
  @GeneratedValue(generator = "exam_schedule_id_generator")
  @SequenceGenerator(
      name = "exam_schedule_id_generator",
      sequenceName = "nhahocduong_exam_schedule_id_seq",
      allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "campaign_id", nullable = false)
  private ExamCampaign campaign;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id", nullable = false)
  private Organization organization;

  @Column(name = "school_class", nullable = false)
  private String schoolClass;

  @Column(name = "exam_date", nullable = false)
  private LocalDate examDate;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "nhahocduong_exam_schedule_dentist",
      joinColumns = @JoinColumn(name = "schedule_id"),
      inverseJoinColumns = @JoinColumn(name = "dentist_id"))
  private Set<Dentist> dentists = new HashSet<>();
}
