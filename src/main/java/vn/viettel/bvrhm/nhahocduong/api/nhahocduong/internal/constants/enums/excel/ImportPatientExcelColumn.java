package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.excel;

import lombok.Getter;

@Getter
public enum ImportPatientExcelColumn {
  INDEX("Số thứ tự", 0, false),
  FULL_NAME("Họ và tên", 1, true),
  BIRTHDAY("Ngày sinh", 2, true),
  GENDER("Giới tính", 3, true),
  SCHOOL_CODE("Mã trường", 4, true),
  CLASS("Lớp", 5, true),
  AREA_TYPE("Vùng địa dư", 6, false),
  NATIONAL_ID_NUMBER("Mã định danh", 7, false),
  ETHNIC("Dân tộc", 8, false),
  HEALTH_INSURANCE_NUMBER("Mã thẻ BHYT", 9, false),
  CARE_TAKER("Người giám hộ", 10, false);

  private final String header;
  private final int index;
  private final boolean isRequired;

  ImportPatientExcelColumn(String header, int index, boolean isRequired) {
    this.header = header;
    this.index = index;
    this.isRequired = isRequired;
  }

  public static ImportPatientExcelColumn getByIndex(int index) {
    for (ImportPatientExcelColumn column : ImportPatientExcelColumn.values()) {
      if (column.index == index) {
        return column;
      }
    }

    return null;
  }
}
