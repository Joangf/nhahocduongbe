package vn.viettel.bvrhm.nhahocduong.api.user.internal.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;

public record UserDTO(
    Long id,
    String username,
    String password,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    LocalDate birthDate,
    List<RoleDTO> roleList,
    OrganizationDTO organization,
    Boolean registerStatus,
    LocalDateTime createdDate) {}
