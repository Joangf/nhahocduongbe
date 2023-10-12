package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Ethnic {
  KINH(0, "Kinh"),
  TAY(1, "Tày"),
  NUNG(2, "Nùng"),
  CHAM(3, "Chăm"),
  KHMER(4, "Khơme"),
  OTHER(5, "Khác");

  private Integer code;
  private String description;

  Ethnic(Integer code, String description) {
    this.code = code;
    this.description = description;
  }

  public static Ethnic getByCode(int code) {
    for (Ethnic ethnic : Ethnic.values()) {
      if (ethnic.code == code) {
        return ethnic;
      }
    }
    return null;
  }

  public static Ethnic getByDescription(String description) {
    for (Ethnic ethnic : Ethnic.values()) {
      if (ethnic.description.equals(description)) {
        return ethnic;
      }
    }
    return null;
  }

  @JsonValue
  public Integer getCode() {
    return this.code;
  }

  public String getDescription() {
    return this.description;
  }
}
