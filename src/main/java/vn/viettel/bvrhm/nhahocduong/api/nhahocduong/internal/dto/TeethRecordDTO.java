package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Tooth;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ToothProblem;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ToothSide;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ToothTreatment;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public record TeethRecordDTO(
  Long id,
  Map<Tooth, ToothConditionDTO> record) implements Serializable {

  public record ToothConditionDTO(
    ToothProblem problem,
    List<ToothSide> locations,
    ToothTreatment treatment) implements Serializable {
  }

}