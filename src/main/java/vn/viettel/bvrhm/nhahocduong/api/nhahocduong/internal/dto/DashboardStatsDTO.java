package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
  private Long totalCampaigns;
  private Long activeCampaigns;
  private Long totalStudents;
  private Long totalExamined;
}
