package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.CampaignStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamCampaignDTO implements Serializable {
  private Long id;
  private String name;
  private CampaignStatus campaignStatus = CampaignStatus.UPCOMING;
  private LocalDate startDate;
  private LocalDate endDate;
  private String description;
  private Boolean status = true;
}
