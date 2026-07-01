package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.ExamSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TeethRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothProblem;

@RestController
@RequestMapping("/api")
public class ExamController {

  @Autowired ExamService examService;
  @Autowired ExamRepository examRepository;

  @GetMapping("/exams/re-exams")
  public List<Map<String, Object>> getReExams() {
    List<Exam> exams = examRepository.findAll().stream()
        .filter(e -> e.getStatus() == null || e.getStatus())
        .collect(Collectors.toList());

    List<Map<String, Object>> reExams = new ArrayList<>();
    for (Exam exam : exams) {
      // Check if student has decayed teeth (caries)
      TeethRecord tr = exam.getTeethRecord();
      boolean needsReExam = false;
      if (tr != null && tr.getRecord() != null) {
        needsReExam = tr.getRecord().values().stream()
            .anyMatch(cond -> cond != null && cond.getProblem() == ToothProblem.CARIES);
      }

      if (needsReExam) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", exam.getId());
        map.put("patientName", exam.getPatient() != null ? exam.getPatient().getFullName() : "Học sinh");
        map.put("schoolClass", exam.getSchoolClass());
        map.put("date", exam.getDate());
        map.put("reExamDate", exam.getDate() != null ? exam.getDate().plusMonths(6) : java.time.LocalDate.now().plusMonths(6));
        map.put("reExamNote", "Cần tái khám điều trị sâu răng");
        reExams.add(map);
      }
    }
    return reExams;
  }

  @GetMapping("/patients/{patientId}/exams")
  List<ExamDTO> getExamsByPatientId(
      @PathVariable Long patientId,
      @RequestParam(value = "status", defaultValue = "true") boolean status) {
    return examService.getExamsByPatientIdAndStatus(patientId, status);
  }

  @GetMapping("/patients/{patientId}/exams/{examId}")
  ExamDTO getExamByExamId(
      @PathVariable("patientId") Long patientId,
      @PathVariable("examId") Long examId,
      @RequestParam(value = "status", defaultValue = "true") boolean status) {
    return examService.getExamByIdAndPatientIdAndStatus(examId, patientId, status);
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
    examDTO.setPatientId(patientId);
    var createdExamDTO = examService.createExam(examDTO);
    return createdExamDTO;
  }

  @PutMapping("/patients/{patientId}/exams")
  ExamDTO updateExam(@PathVariable("patientId") Long patientId, @RequestBody ExamDTO examDTO) {
    examDTO.setPatientId(patientId);
    return examService.updateExam(examDTO);
  }

  @DeleteMapping("/exams/{id}")
  boolean deleteExam(@PathVariable Long id) {
    return examService.delete(id);
  }

  @GetMapping("/patients/{patientId}/exams/search")
  public Page<ExamDTO> search(ExamSearchCriteria searchCriteria, @PathVariable("patientId") Long patientId, Pageable pageable) {
    searchCriteria.setPatientId(patientId);
    return examService.search(searchCriteria, pageable);
  }
}
