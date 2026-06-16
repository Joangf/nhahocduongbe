package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author: longlb1
 * @since: 14-Sep-23
 */
public enum OrganizationType {
  SCHOOL(1, "Trường học"),
  DEPARTMENT(2, "Sở"),
  MINISTRY(3, "Bộ"),
  HCMC_CENTRAL_DENTAL_HOSPITAL(4, "Bệnh viện Răng hàm mặt trung ương TP. Hồ Chí Minh");

  private final Integer code;
  private final String name;

  OrganizationType(int code, String name) {
    this.code = code;
    this.name = name;
  }

  public Integer getCode() {
    return this.code;
  }

  @JsonValue
  public String getName() {
    return this.name;
  }
}
