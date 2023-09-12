package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TreatmentStatus {
  DONE("Hoàn tất khám"),
  TRANSFER("Chuyển TYT/TTYT"),
  ;

  private String description;

  private TreatmentStatus(String description) {
    this.description = description;
  }

  @JsonValue
  public String getDescription() {
    return this.description;
  }
}
