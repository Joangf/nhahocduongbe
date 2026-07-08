package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AcademicYearStatus {
  UPCOMING("Chưa bắt đầu"),
  CURRENT("Đang diễn ra"),
  COMPLETED("Đã kết thúc");

  private final String description;

  AcademicYearStatus(String description) {
    this.description = description;
  }

  @JsonValue
  public String getDescription() {
    return this.description;
  }
}
