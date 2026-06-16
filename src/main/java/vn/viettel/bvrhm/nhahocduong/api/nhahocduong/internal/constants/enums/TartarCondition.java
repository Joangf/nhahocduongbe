package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TartarCondition {
  TOOTH_MISSING("4", "Không có răng"),
  CLEAN("0", "Không có vôi"),
  ONE_THIRD("1", "Vôi răng 1/3 cổ răng"),
  TWO_THIRD("2", "Vôi răng 2/3 răng"),
  TWO_THIRD_OR_MORE("3", "Vôi răng > 2/3 răng"),
  ;

  private final String code;

  private final String description;

  TartarCondition(String code, String description) {
    this.code = code;
    this.description = description;
  }

  @JsonValue
  public String getCode() {
    return this.code;
  }

  //  @JsonValue
  public String getDescription() {
    return this.description;
  }
}
