package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PlaqueRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.PlaqueRecordService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamServiceImpl;

@RestController
@RequestMapping("/api")
public class PlaqueRecordController {
  @Autowired PlaqueRecordService plaqueRecordService;
  @Autowired ExamServiceImpl examService;

  @GetMapping("/patients/{patientId}/exams/{examId}/plaqueRecord")
  PlaqueRecordDTO getPlaqueRecord(
      @PathVariable("patientId") Long patientId, @PathVariable("examId") Long examId) {
    PlaqueRecordDTO plaqueRecordDTO =
        plaqueRecordService.getPlaqueRecordByPatientIdAndExamId(patientId, examId);
    return plaqueRecordDTO;
  }

  @PostMapping("/patients/{patientId}/exams/{examId}/plaqueRecord")
  PlaqueRecordDTO upsertPlaqueRecordOfExam(
      @PathVariable("patientId") Long patientId,
      @PathVariable("examId") Long examId,
      @RequestBody PlaqueRecordDTO plagueRecordDTO) {

    PlaqueRecordDTO plaqueRecordDTO = plaqueRecordService.upsertPlaqueRecord(plagueRecordDTO);
    examService.updatePlaqueRecordIdOfExam(examId, plaqueRecordDTO.id());
    return plaqueRecordDTO;
  }
}
