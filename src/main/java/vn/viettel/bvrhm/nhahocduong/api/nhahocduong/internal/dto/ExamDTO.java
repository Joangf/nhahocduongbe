package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamPlace;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.PrescriptionItem;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TreatmentRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * A DTO for the {@link Exam} entity
 */
@Builder(toBuilder = true)
public record ExamDTO(
  Long id,
  Long patientId,
  String patientName,
  Long dentistId,
  String dentistName,
  Long organizationId,
  String organizationName,
  String schoolClass,
  String year,
  Long profileNumber,
  @JsonFormat(pattern = "dd/MM/yyyy")
  LocalDate date,
  Long teethRecordId,
  Long plaqueRecordId,
  Long tartarRecordId,
  List<TreatmentRecordDTO> treatmentRecords
//  ExamPlace examPlace,
//  List<PrescriptionItem> prescription,
//  String diagnosis
  ){}