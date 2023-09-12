package vn.viettel.bvrhm.nhahocduong.api.user;

import java.time.LocalDate;

public record UserDTO (
  Long id,
  String username,
  String password,
  String firstName,
  String lastName,
  String email,
  String phoneNumber,
  LocalDate birthDate
) {}
