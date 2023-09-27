package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import lombok.Data;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.PrescriptionItem;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Tooth;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ToothTreatment;

import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TreatmentRecord} entity
 */
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