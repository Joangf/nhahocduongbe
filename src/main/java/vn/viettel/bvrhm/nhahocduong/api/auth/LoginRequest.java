package vn.viettel.bvrhm.nhahocduong.api.auth;

public record LoginRequest (
  String loginName,
  String password
) {}
