package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TreatmentRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.TreatmentRecordService;

@RestController
@RequestMapping("/api")
public class TreatmentRecordController {
  @Autowired private TreatmentRecordService treatmentRecordService;

  @GetMapping("/patients/{patientId}/exams/{examId}/treatmentRecord")
  public List<TreatmentRecordDTO> getTreatmentRecordByPatientIdAndExamId(
      @PathVariable("patientId") Long patientId, @PathVariable("examId") Long examId) {
    // TODO check permission ?
    return treatmentRecordService.getTreatmentRecordsByExamId(examId);
  }

  @PostMapping("/patients/{patientId}/exams/{examId}/treatmentRecord")
  public List<TreatmentRecordDTO> upsertTreatmentRecordByPatientIdAndExamId(
      @PathVariable("patientId") Long patientId,
      @PathVariable("examId") Long examId,
      @RequestBody List<TreatmentRecordDTO> treatmentRecords) {
    return treatmentRecordService.upsertTreatmentRecordsByExamIdAndPatientId(examId, patientId,treatmentRecords);
  }

  @DeleteMapping("/patients/{patientId}/exams/{examId}/treatmentRecord/{treatmentRecordId}")
  public boolean deleteTreatmentRecord(
      @PathVariable("patientId") Long patientId,
      @PathVariable("examId") Long examId,
      @PathVariable("treatmentRecordId") Long treatmentRecordId) {
    return treatmentRecordService.deleteTreatmentRecord(examId, patientId, treatmentRecordId);
  }
}
