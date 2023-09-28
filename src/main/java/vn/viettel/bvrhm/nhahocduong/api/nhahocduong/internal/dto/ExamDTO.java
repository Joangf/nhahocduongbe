package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamPlace;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.PrescriptionItem;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TreatmentRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * A DTO for the {@link Exam} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
//  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate date;
  private Long teethRecordId;
  private Long plaqueRecordId;
  private Long tartarRecordId;
  private List<TreatmentRecordDTO> treatmentRecords;
  @JsonIgnore
  private Boolean status = true;
//  ExamPlace examPlace;
//  List<PrescriptionItem> prescription;
//  String diagnosis
}