package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ToothTreatment {
  NO_TREATMENT("0", "Không"),
  ONE_SIDE_FILLING("1", "Trám 1 mặt"),
  TWO_OR_MORE_SIDE_FILLING("2", "Trám 2 mặt trở lên"),
  CROWN("3", "Mão răng"),
  VENEER("4", "Veneer"),
  ROOT_CANAL("5", "Điều trị tuỷ"),
  REMOVE("6", "Nhổ răng"),
  SEALANT("F", "Sealants"), //
  PREVENTATIVE_FILLING("P", "Trám phòng ngừa");

  private String code;
  private String description;

  ToothTreatment(String code, String description) {
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
