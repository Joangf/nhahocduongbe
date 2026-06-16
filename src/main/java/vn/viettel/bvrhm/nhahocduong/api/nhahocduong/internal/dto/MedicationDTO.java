package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Medication;

/** A DTO for the {@link Medication} entity */
public record MedicationDTO(Long id, String code, String name, String unit) {}
