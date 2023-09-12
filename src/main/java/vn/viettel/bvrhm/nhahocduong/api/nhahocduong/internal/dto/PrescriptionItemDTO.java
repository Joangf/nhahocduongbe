package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

public record PrescriptionItemDTO(
  Long id,
  String medication_code,
  Integer quantity) {}