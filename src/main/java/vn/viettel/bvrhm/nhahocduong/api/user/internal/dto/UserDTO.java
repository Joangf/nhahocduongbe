package vn.viettel.bvrhm.nhahocduong.api.user.internal.dto;

import java.time.LocalDate;
import java.util.List;

public record UserDTO (
  Long id,
  String username,
  String password,
  String firstName,
  String lastName,
  String email,
  String phoneNumber,
  LocalDate birthDate,
  List<RoleDTO> roleList
) {}
