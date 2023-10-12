package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Tooth;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothProblem;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothSide;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothTreatment;

public record TeethRecordDTO(Long id, Map<Tooth, ToothConditionDTO> record)
    implements Serializable {

  public record ToothConditionDTO(
      ToothProblem problem, List<ToothSide> locations, ToothTreatment treatment)
      implements Serializable {}
}
