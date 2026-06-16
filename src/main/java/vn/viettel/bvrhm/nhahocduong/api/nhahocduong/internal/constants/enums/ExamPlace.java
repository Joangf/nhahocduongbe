package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ExamPlace {
  YTTH("YTTH"),
  TYT_TTYT("TYT/TTYT"),
  ;

  private final String code;

  ExamPlace(String code) {
    this.code = code;
  }

  @JsonValue
  public String getCode() {
    return this.code;
  }
}
