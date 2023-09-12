package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import java.util.List;

public record Treatment(
    Long dentistId,
    String toothTreatmentCode,
    List<Tooth> teeth,
    List<PrescriptionItem> prescription
) {}
