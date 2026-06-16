package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Tooth;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothTreatment;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.PrescriptionItem;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TreatmentRecord;

/** A DTO for the {@link TreatmentRecord} entity */
@Data
public class TreatmentRecordDTO implements Serializable {
  Long id;
  ToothTreatment service;
  String dentistName;
  String diagnosis;
  Tooth tooth;
  List<PrescriptionItem> prescription;
  Boolean status = true;
  Long examId;
}
