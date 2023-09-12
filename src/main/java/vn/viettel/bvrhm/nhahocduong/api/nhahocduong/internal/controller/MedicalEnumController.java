package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.MedicalEnumService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MedicalEnumController {
  @Autowired
  private MedicalEnumService medicalEnumService;

  @GetMapping("/tartarCondition")
  List<TartarConditionDTO> getListTartarCondition() {
    return medicalEnumService.getListTartarCondition();
  }
  @GetMapping("/plaqueCondition")
  List<PlaqueConditionDTO> getListPlaqueConditionDTO() {
    return medicalEnumService.getListPlaqueCondition();
  }
  @GetMapping("/toothProblem")
  List<ToothProblemDTO> getListToothProblemDTO() {
    return medicalEnumService.getListToothProblem();
  }
  @GetMapping("/toothSide")
  List<ToothSideDTO> getListToothSideDTO() {
    return medicalEnumService.getListToothSide();
  }
  @GetMapping("/toothTreatment")
  List<ToothTreatmentDTO> getListToothTreatmentDTO() {
    return medicalEnumService.getListToothTreatment();
  }
  @GetMapping("/ethnics")
  List<EthnicDTO> getListEthnics() {
    return medicalEnumService.getListEthnics();
  }

}
