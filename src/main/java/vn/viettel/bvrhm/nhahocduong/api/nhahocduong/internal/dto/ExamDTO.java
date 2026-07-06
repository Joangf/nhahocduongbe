package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import java.io.Serializable;

/** A DTO for the {@link Exam} entity */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  private Long id;
  private Long patientId;
  private String patientName;
  private Long dentistId;
  private String dentistName;
  private Long organizationId;
  private String organizationName;
  private String schoolClass;
  private String year;
  private Long profileNumber;
  private LocalDate date;
  private Long teethRecordId;
  private Long plaqueRecordId;
  private Long tartarRecordId;
  private List<TreatmentRecordDTO> treatmentRecords;

  private Long campaignId;
  private LocalDate reExamDate;
  private String reExamNote;

  // ── Mục 4: Đánh giá mức độ bệnh lý ──
  private String pathologyAssessment;

  // ── Mục 5: Ghi chú điều trị ──
  private String treatmentNote;

  // ── Mục 6: Ảnh thực tế trước & sau điều trị ──
  private String imageUpperUrl;
  private LocalDateTime imageUpperTime;
  private String imageLowerUrl;
  private LocalDateTime imageLowerTime;

  @JsonIgnore private Boolean status = true;
  //  ExamPlace examPlace;
  //  List<PrescriptionItem> prescription;
  //  String diagnosis
}
