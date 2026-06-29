package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CampaignStatus {
  UPCOMING("Sắp tới"),
  IN_PROGRESS("Đang diễn ra"),
  COMPLETED("Đã xong"),
  CANCELLED("Đã hủy");

  private final String description;

  CampaignStatus(String description) {
    this.description = description;
  }

  @JsonValue
  public String getDescription() {
    return this.description;
  }
}
