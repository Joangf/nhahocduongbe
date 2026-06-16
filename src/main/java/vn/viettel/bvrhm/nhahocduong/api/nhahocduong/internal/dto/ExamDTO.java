package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.List;
import lombok.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;

/** A DTO for the {@link Exam} entity */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamDTO {
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
  @JsonIgnore private Boolean status = true;
  //  ExamPlace examPlace;
  //  List<PrescriptionItem> prescription;
  //  String diagnosis
}
