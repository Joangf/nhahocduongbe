package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ToothProblem {
  NO_PROBLEM("0", "Bình thường"),
  CARIES("1", "Sâu"),
  CARIES_FILLING("2", "Sâu trám lại"), // ? TODO sâu trám lại
  FILLING_NO_PROBLEM("3", "Trám tốt"),
  LOST_CARIES("4", "Mất do sâu"),
  LOST_OTHER("5", "Mất lý do khác"),
  BIT_HO_RANH("6", "Bít hố rãnh"), // TODO Bít hố rãnh
  TRU_CAU("7", "Trụ cầu"), // TODO
  YET_TO_GROW("8", "Chưa mọc"), // Răng chưa mọc
  OTHERS("9", "Loại trừ"), // Loai tru
  ;

  private String code;
  private String description;

  ToothProblem(String code, String description) {
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
