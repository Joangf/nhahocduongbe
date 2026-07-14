package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AffiliationStatus {
  STUDYING("Đang học"),
  GRADUATED("Đã tốt nghiệp"),
  TRANSFERRED("Đã chuyển trường"),
  DROPPED_OUT("Đã rời trường");

  private final String description;

  AffiliationStatus(String description) {
    this.description = description;
  }

  @JsonValue
  public String getDescription() {
    return this.description;
  }
}
