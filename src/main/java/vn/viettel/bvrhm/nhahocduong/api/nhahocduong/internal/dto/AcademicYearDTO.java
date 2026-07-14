package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYearDTO {
  private Long id;
  private String name;
  private LocalDate startDate;
  private LocalDate endDate;
  private String status;
}
