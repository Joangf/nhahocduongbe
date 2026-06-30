package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentExamStatusDTO {
  private Long patientId;
  private String fullName;
  private String code;
  private String schoolClass;
  private String phoneNumber;
  
  private Long examId;
  private LocalDate examDate;
  private String status; // "EXAMINED" or "NOT_EXAMINED"
}
