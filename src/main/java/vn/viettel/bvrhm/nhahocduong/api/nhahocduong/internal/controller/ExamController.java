package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.ExamSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.AssessmentUpdateDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ImageUpdateDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamService;

@RestController
@RequestMapping("/api")
public class ExamController {

  @Autowired ExamService examService;

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

  @GetMapping("/exams/re-exams")
  public List<ExamDTO> getReExams() {
    return examService.getReExams();
  }

  @GetMapping("/dashboard/campaign-stats")
  public vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DashboardStatsDTO getCampaignStats() {
    return examService.getDashboardStats();
  }

  /** PATCH: Cập nhật đánh giá bệnh lý & ghi chú điều trị (mục 4 & 5) */
  @PatchMapping("/exams/{examId}/assessment")
  public ExamDTO updateAssessment(
      @PathVariable Long examId, @RequestBody AssessmentUpdateDTO dto) {
    return examService.updateAssessment(examId, dto);
  }

  /** PATCH: Cập nhật hoặc xóa ảnh trước/sau điều trị (mục 6) */
  @PatchMapping("/exams/{examId}/images")
  public ExamDTO updateImages(
      @PathVariable Long examId, @RequestBody ImageUpdateDTO dto) {
    return examService.updateImages(examId, dto);
  }
}
