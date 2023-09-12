package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ToothSide {
  CHEW("Nh", "Mặt nhai"),
  OUTSIDE("N", "Mặt ngoài"),
  INSIDE("T", "Mặt trong"),
  NEAR("G", "Mặt gần"), // Mặt gần
  FAR("X", "Mặt xa"), // Mặt xa
  ;

  private String code;
  private String description;

  ToothSide(String code, String description) {
    this.code = code;
    this.description = description;
  }

  @JsonValue
  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }
}
