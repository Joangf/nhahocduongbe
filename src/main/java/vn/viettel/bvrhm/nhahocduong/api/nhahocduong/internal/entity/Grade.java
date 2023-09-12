package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Grade {
  _1("1"),
  _2("2"),
  _3("3"),
  _4("4"),
  _5("5"),
  _6("6"),
  _7("7"),
  _8("8"),
  _9("9"),
  _10("10"),
  _11("11"),
  _12("12"),
  ;

  String code;

  Grade(String code) {
    this.code = code;
  }

  @JsonValue
  public String getCode() {
    return this.code;
  }

}
