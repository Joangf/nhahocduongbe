package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.excel;

import lombok.Getter;

@Getter
public enum ExportPatientExcelColumn {
  INDEX("Số thứ tự", 0, false),
  CODE("Mã học sinh", 1, false),
  FULL_NAME("Họ và tên", 2, false),
  BIRTHDAY("Ngày sinh", 3, false),
  GENDER("Giới tính", 4, false),
  SCHOOL_CODE("Mã trường", 5, false),
  CLASS("Lớp", 6, false),
  AREA_TYPE("Vùng địa dư", 7, false),
  NATIONAL_ID_NUMBER("Mã định danh", 8, false),
  ETHNIC("Dân tộc", 9, false),
  HEALTH_INSURANCE_NUMBER("Mã thẻ BHYT", 10, false),
  CARE_TAKER("Người giám hộ", 11, false);

  private final String header;
  private final int index;
  private final boolean isRequired;

  ExportPatientExcelColumn(String header, int index, boolean isRequired) {
    this.header = header;
    this.index = index;
    this.isRequired = isRequired;
  }

  public static ExportPatientExcelColumn getByIndex(int index) {
    for (ExportPatientExcelColumn column : ExportPatientExcelColumn.values()) {
      if (column.index == index) {
        return column;
      }
    }

    return null;
  }

  public static int getTotalColumn() {
    return ExportPatientExcelColumn.values().length;
  }
}
