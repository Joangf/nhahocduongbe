package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamServiceImpl;

@RestController
@RequestMapping("/api")
public class PrescriptionController {
  @Autowired private ExamServiceImpl examService;
  //
  //  @GetMapping("/patients/{patientId}/exams/{examId}/prescription")
  //  public List<PrescriptionItem> getPrescriptionByPatientIdAndExamId(
  //      @PathVariable("patientId") Long patientId, @PathVariable("examId") Long examId) {
  //    // TODO check permission ?
  //    return examService.getPrescriptionByExamId(examId);
  //  }
  //
  //  @PostMapping("/patients/{patientId}/exams/{examId}/prescription")
  //  public List<PrescriptionItem> updatePrescriptionByPatientIdAndExamId(
  //      @PathVariable("patientId") Long patientId,
  //      @PathVariable("examId") Long examId,
  //      @RequestBody List<PrescriptionItem> prescriptionItemList) {
  //    List<PrescriptionItem> updatedList =
  //        examService.updatePrescriptionByExamId(examId, prescriptionItemList);
  //    return updatedList;
  //  }
}
