package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for PATCH /api/exams/{examId}/assessment — cập nhật mục 4 & 5 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentUpdateDTO implements Serializable {
  private String pathologyAssessment;
  private String treatmentNote;
}
