package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DiseaseDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.DiseaseServiceImpl;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamServiceImpl;

@RestController
@RequestMapping("/api")
public class DiseaseController {
  @Autowired private ExamServiceImpl examService;
  @Autowired private DiseaseServiceImpl diseaseService;

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
