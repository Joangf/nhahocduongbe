package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TartarRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamServiceImpl;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.TartarRecordService;

@RestController
@RequestMapping("/api")
public class TartarRecordController {
  @Autowired TartarRecordService tartarRecordService;
  @Autowired
  ExamServiceImpl examService;


  @GetMapping("/patients/{patientId}/exams/{examId}/tartarRecord")
  TartarRecordDTO getTartarRecord(
      @PathVariable("patientId") Long patientId, @PathVariable("examId") Long examId) {
    TartarRecordDTO tartarRecordDTO =
        tartarRecordService.getTartarRecordByPatientIdAndExamId(patientId, examId);
    return tartarRecordDTO;
  }

  @PostMapping("/patients/{patientId}/exams/{examId}/tartarRecord")
  TartarRecordDTO upsertTartarRecordOfExam(
      @PathVariable("patientId") Long patientId,
      @PathVariable("examId") Long examId,
      @RequestBody TartarRecordDTO tartarRecordDTO) {

    TartarRecordDTO savedTartarRecordDTO = tartarRecordService.upsertTartarRecord(tartarRecordDTO);
    examService.updateTartarRecordIdOfExam(examId, savedTartarRecordDTO.id());
    return savedTartarRecordDTO;
  }
}
