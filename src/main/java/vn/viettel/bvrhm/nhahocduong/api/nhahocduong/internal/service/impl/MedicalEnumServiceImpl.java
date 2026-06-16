package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.MedicalEnumService;

@Service
public class MedicalEnumServiceImpl implements MedicalEnumService {
  @Override
  public List<TartarConditionDTO> getListTartarCondition() {
    List<TartarConditionDTO> categoryDTOList = new ArrayList<>();
    for (var condition : TartarCondition.values()) {
      categoryDTOList.add(new TartarConditionDTO(condition.getCode(), condition.getDescription()));
    }

    return categoryDTOList;
  }

  @Override
  public List<PlaqueConditionDTO> getListPlaqueCondition() {
    List<PlaqueConditionDTO> categoryDTOList = new ArrayList<>();
    for (var condition : PlaqueCondition.values()) {
      categoryDTOList.add(new PlaqueConditionDTO(condition.getCode(), condition.getDescription()));
    }

    return categoryDTOList;
  }

  @Override
  public List<ToothProblemDTO> getListToothProblem() {
    List<ToothProblemDTO> categoryDTOList = new ArrayList<>();
    for (var condition : ToothProblem.values()) {
      categoryDTOList.add(new ToothProblemDTO(condition.getCode(), condition.getDescription()));
    }

    return categoryDTOList;
  }

  @Override
  public List<ToothSideDTO> getListToothSide() {
    List<ToothSideDTO> categoryDTOList = new ArrayList<>();
    for (var condition : ToothSide.values()) {
      categoryDTOList.add(new ToothSideDTO(condition.getCode(), condition.getDescription()));
    }

    return categoryDTOList;
  }

  @Override
  public List<ToothTreatmentDTO> getListToothTreatment() {
    List<ToothTreatmentDTO> categoryDTOList = new ArrayList<>();
    for (var condition : ToothTreatment.values()) {
      categoryDTOList.add(new ToothTreatmentDTO(condition.getCode(), condition.getDescription()));
    }

    return categoryDTOList;
  }

  @Override
  public List<EthnicDTO> getListEthnics() {
    List<EthnicDTO> ethnicDTOList = new ArrayList<>();
    for (var ethnic : Ethnic.values()) {
      ethnicDTOList.add(new EthnicDTO(ethnic.getCode(), ethnic.getDescription()));
    }

    return ethnicDTOList;
  }
}
