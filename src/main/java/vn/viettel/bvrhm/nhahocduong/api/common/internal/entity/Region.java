package vn.viettel.bvrhm.nhahocduong.api.common.internal.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Region {
  NORTH("north", "Các tỉnh phía bắc"),
  SOUTH("south", "Các tỉnh phía nam"),
  ;

  private final String code;
  private final String description;

  Region(String code, String description) {
    this.code = code;
    this.description = description;
  }

  @JsonValue
  public String getCode() {
    return this.code;
  }

  public String getDescription() {
    return this.description;
  }
}
