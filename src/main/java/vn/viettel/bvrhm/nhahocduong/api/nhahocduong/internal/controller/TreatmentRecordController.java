package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TreatmentRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TreatmentRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TreatmentRecordController {
  @Autowired private ExamService examService;

  @GetMapping("/patients/{patientId}/exams/{examId}/treatmentRecord")
  public List<TreatmentRecordDTO> getTreatmentRecordByPatientIdAndExamId(
      @PathVariable("patientId") Long patientId, @PathVariable("examId") Long examId) {
    // TODO check permission ?
    return examService.getTreatmentRecordsByExamId(examId);
  }

  @PostMapping("/patients/{patientId}/exams/{examId}/treatmentRecord")
  public List<TreatmentRecordDTO> upsertTreatmentRecordByPatientIdAndExamId(
      @PathVariable("patientId") Long patientId,
      @PathVariable("examId") Long examId,
      @RequestBody List<TreatmentRecordDTO> treatmentRecords) {
    return examService.upsertTreatmentRecordsByExamId(examId, treatmentRecords);
  }

  @DeleteMapping("/patients/{patientId}/exams/{examId}/treatmentRecord/{treatmentRecordId}")
  public boolean deleteTreatmentRecord(@PathVariable("patientId") Long patientId,
                                       @PathVariable("examId") Long examId,
                                       @PathVariable("treatmentRecordId") Long treatmentRecordId) {
    return examService.deleteTreatmentRecord(examId, treatmentRecordId);
  }
}
