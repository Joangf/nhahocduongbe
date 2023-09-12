package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DiseaseDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.DiseaseService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DiseaseController {
  @Autowired private ExamService examService;
  @Autowired private DiseaseService diseaseService;

  @GetMapping("/patients/{patientId}/exams/{examId}/chronicConditions")
  public List<String> getChronicDiseaseListOfUserExam(
      @PathVariable("patientId") Long patientId, @PathVariable("examId") Long examId) {
    // TODO check ownership permission
    List<String> codeList = examService.getChronicDiseasesCodesByExamId(examId);
    return codeList;
  }

  @PostMapping("/patients/{patientId}/exams/{examId}/chronicConditions")
  public List<String> updateChronicDiseaseListOfUserExam(
      @PathVariable("patientId") Long patientId,
      @PathVariable("examId") Long examId,
      @RequestBody List<String> diseaseCodeList) {
    List<String> updatedCodeList =
        examService.updateChronicDiseasesCodesByExamId(examId, diseaseCodeList);
    return updatedCodeList;
  }

  @GetMapping("/diseases")
  public List<DiseaseDTO> getAllDiseases() {
    return diseaseService.getAllDiseases();
  }
}
