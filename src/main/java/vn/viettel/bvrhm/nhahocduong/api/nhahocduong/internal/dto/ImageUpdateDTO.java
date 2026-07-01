package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for PATCH /api/exams/{examId}/images — cập nhật hoặc xóa ảnh (mục 6).
 * Truyền null cho url để xóa ảnh tương ứng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageUpdateDTO implements Serializable {
  private String imageBeforeUrl;
  private LocalDateTime imageBeforeTime;
  private String imageAfterUrl;
  private LocalDateTime imageAfterTime;
}
