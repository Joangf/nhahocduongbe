package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import java.util.List;

public record Treatment(
    String dentistName,
    String diagnosis,
    List<Tooth> teeth,
    List<PrescriptionItem> prescription
) {}
