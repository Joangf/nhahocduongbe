package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import java.io.Serializable;

/**
 * A DTO for the {@link vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Dentist} entity
 */
public record DentistDTO(Long id, Long userId, String title) implements Serializable {}
