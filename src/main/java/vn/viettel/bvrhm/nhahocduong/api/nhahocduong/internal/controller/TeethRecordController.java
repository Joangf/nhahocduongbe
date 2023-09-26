package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TeethRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.TeethRecordMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.TeethRecordRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamServiceImpl;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.TeethRecordService;

@RestController
@RequestMapping("/api")
public class TeethRecordController {

  Logger log = LoggerFactory.getLogger(TeethRecordController.class);
  @Autowired TeethRecordService teethRecordService;
  @Autowired TeethRecordRepository teethRecordRepository;
  @Autowired TeethRecordMapper teethRecordMapper;

  @Autowired
  ExamServiceImpl examService;

  @GetMapping("/patients/{patientId}/exams/{examId}/teethRecord")
  TeethRecordDTO getTeethRecord(
      @PathVariable("patientId") Long patientId, @PathVariable("examId") Long examId) {
    TeethRecordDTO teethRecordDTO =
        teethRecordService.getTeethRecordByPatientIdAndExamId(patientId, examId);
    return teethRecordDTO;
  }

  @PostMapping("/patients/{patientId}/exams/{examId}/teethRecord")
  TeethRecordDTO createTeethRecordOfExam(
    @PathVariable("patientId") Long patientId,
    @PathVariable("examId") Long examId,
    @RequestBody TeethRecordDTO teethRecordDTO) {
    TeethRecordDTO createdDto = teethRecordService.createTeethRecord(teethRecordDTO);
    var updatedExam = examService.updateTeethRecordIdOfExamId(examId, createdDto.id());
    return createdDto;
  }

  @PostMapping("/teethRecord")
  public TeethRecordDTO createTeethRecord(@RequestBody TeethRecordDTO teethRecordDTO) {
    TeethRecordDTO createdDto = teethRecordService.createTeethRecord(teethRecordDTO);
    return createdDto;
  }

  @GetMapping("/teethRecord/{id}")
  public TeethRecordDTO getTeethRecordById(@PathVariable Long id) {
    TeethRecordDTO dto = teethRecordService.getTeethRecordById(id);
    return dto;
  }
}
