package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import jakarta.persistence.Convert;
import java.util.List;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter.TreatmentStatusJpaConverter;

public record TreatmentRecord(
    List<Treatment> treatmentList
) {
}
