package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExamController {

  @Autowired ExamService examService;

  @GetMapping("/patients/{patientId}/exams")
  List<ExamDTO> getExamsByPatientId(@PathVariable Long patientId) throws JsonProcessingException {
    List<ExamDTO> result = examService.getExamsByPatientId(patientId);
    return result;
  }

  @GetMapping("/patients/{patientId}/exams/{examId}")
  ExamDTO getExamByExamId(
      @PathVariable("patientId") Long patientId, @PathVariable("examId") Long examId) {
    // TODO check ownership
    ExamDTO examDTO = examService.getExamById(examId);
    return examDTO;
  }

  @PostMapping("/patients/{patientId}/exams")
  ExamDTO createExam(@PathVariable("patientId") Long patientId, @RequestBody ExamDTO examDTO) {
//    var newExamDTO =
//        new ExamDTO(
//            null,
//            patientId,
//            null,
//            examDTO.dentistId(),
//            null,
//            examDTO.organizationId(),
//            null,
//            examDTO.schoolClass(),
//            examDTO.year(),
//            examDTO.profileNumber(),
//            examDTO.date(),
//            examDTO.teethRecordId(),
//            examDTO.plaqueRecordId(),
//            examDTO.tartarRecordId(),
//                examDTO.examPlace(),
//                examDTO.prescription());
    var newExamDTO = examDTO.toBuilder()
      .id(null)
      .patientId(patientId)
      .patientName(null)
      .dentistName(null)
      .organizationName(null)
      .build();
    var createdExamDTO = examService.createExam(newExamDTO);
    return createdExamDTO;
  }

  @DeleteMapping("/exams/{id}")
  void deleteExam(@PathVariable Long id) {
    examService.delete(id);
  }
}
