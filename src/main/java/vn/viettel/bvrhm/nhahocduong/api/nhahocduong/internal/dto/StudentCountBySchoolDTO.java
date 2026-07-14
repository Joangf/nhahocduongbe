package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCountBySchoolDTO {
  private String schoolName;
  private String grade;
  private Long studentCount;
}
