package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TreatmentRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamServiceImpl;

@RestController
@RequestMapping("/api")
public class TreatmentRecordController {
  @Autowired private ExamServiceImpl examService;

  @GetMapping("/patients/{patientId}/exams/{examId}/treatmentRecord")
  public TreatmentRecord getTreatmentRecordByPatientIdAndExamId(
      @PathVariable("patientId") Long patientId, @PathVariable("examId") Long examId) {
    // TODO check permission ?
    return examService.getTreatmentRecordByExamId(examId);
  }

  @PostMapping("/patients/{patientId}/exams/{examId}/treatmentRecord")
  public TreatmentRecord updateTreatmentRecordByPatientIdAndExamId(
      @PathVariable("patientId") Long patientId,
      @PathVariable("examId") Long examId,
      @RequestBody TreatmentRecord treatmentRecord) {
    TreatmentRecord updatedRecord =
        examService.updateTreatmentRecordByExamId(examId, treatmentRecord);
    return updatedRecord;
  }
}
