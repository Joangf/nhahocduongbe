package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.BaseEntity;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.CampaignStatus;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter.CampaignStatusConverter;

@Data
@Entity
@Table(name = "nhahocduong_exam_campaign")
public class ExamCampaign extends BaseEntity {
  @Id
  @GeneratedValue(generator = "exam_campaign_id_generator")
  @SequenceGenerator(
      name = "exam_campaign_id_generator",
      sequenceName = "nhahocduong_exam_campaign_id_seq",
      allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "campaign_status", nullable = false)
  @Convert(converter = CampaignStatusConverter.class)
  private CampaignStatus campaignStatus = CampaignStatus.UPCOMING;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @Column(name = "description")
  private String description;
}
