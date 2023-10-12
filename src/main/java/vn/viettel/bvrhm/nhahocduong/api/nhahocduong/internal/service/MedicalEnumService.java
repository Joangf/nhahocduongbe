package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.util.List;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.*;

/**
 * @author: longlb1
 * @since: 29-Sep-23
 */
public interface MedicalEnumService {
  List<TartarConditionDTO> getListTartarCondition();

  List<PlaqueConditionDTO> getListPlaqueCondition();

  List<ToothProblemDTO> getListToothProblem();

  List<ToothSideDTO> getListToothSide();

  List<ToothTreatmentDTO> getListToothTreatment();

  List<EthnicDTO> getListEthnics();
}
