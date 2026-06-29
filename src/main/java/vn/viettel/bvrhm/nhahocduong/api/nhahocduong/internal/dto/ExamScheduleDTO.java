package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamScheduleDTO implements Serializable {
  private Long id;
  private Long campaignId;
  private Long organizationId;
  private String organizationName;
  private String schoolClass;
  private LocalDate examDate;
  private Boolean status = true;
}
