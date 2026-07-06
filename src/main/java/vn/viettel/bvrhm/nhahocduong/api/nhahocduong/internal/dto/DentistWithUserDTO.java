package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import java.io.Serializable;

/**
 * DTO kết hợp thông tin Dentist + User (firstName, lastName, phoneNumber)
 * để hiển thị trên dropdown chọn bác sĩ.
 */
public record DentistWithUserDTO(
    Long dentistId,
    Long userId,
    String fullName,
    String phoneNumber) implements Serializable {}
