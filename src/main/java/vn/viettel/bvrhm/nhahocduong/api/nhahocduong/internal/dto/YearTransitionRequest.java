package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearTransitionRequest {
  private String newYearName;
  private LocalDate startDate;
  private LocalDate endDate;
}
