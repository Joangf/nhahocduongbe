package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

/**
 * A DTO for the {@link vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Medication} entity
 */
public record MedicationDTO(
  Long id,
  String code,
  String name,
  String unit) {}