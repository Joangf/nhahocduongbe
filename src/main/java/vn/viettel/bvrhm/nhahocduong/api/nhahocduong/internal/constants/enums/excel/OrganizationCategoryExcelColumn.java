package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.excel;

import lombok.Getter;

/**
 * @author: longlb1
 * @since: 16-Oct-23
 */
@Getter
public enum OrganizationCategoryExcelColumn {
  INDEX("STT", 0, false),
  AREA_CODE("Mã tỉnh/thành", 1, false),
  AREA("Tỉnh/Thành", 2, false),
  SCHOOL_CODE("Mã trường", 3, false),
  SCHOOL_NAME("Trường", 4, false),
  CLASS("Lớp", 5, false);

  private final String header;
  private final int index;
  private final boolean isRequired;

  OrganizationCategoryExcelColumn(String header, int index, boolean isRequired) {
    this.header = header;
    this.index = index;
    this.isRequired = isRequired;
  }

  public static OrganizationCategoryExcelColumn getByIndex(int index) {
    for (OrganizationCategoryExcelColumn column : OrganizationCategoryExcelColumn.values()) {
      if (column.index == index) {
        return column;
      }
    }

    return null;
  }

  public static int getTotalColumn() {
    return OrganizationCategoryExcelColumn.values().length;
  }
}
