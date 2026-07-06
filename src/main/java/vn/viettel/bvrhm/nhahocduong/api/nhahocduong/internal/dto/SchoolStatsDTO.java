package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolStatsDTO {
  private Long schoolId;
  private String schoolName;
  private Long totalStudents;
  private Long examinedStudents;
  private Double examRate;
}
