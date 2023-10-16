package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria;

import lombok.Data;

/**
 * @author: longlb1
 * @since: 18-Sep-23
 */
@Data
public class PatientSearchCriteria {
  private String searchText;
  private String organizationName;
  private String areaCode;
  private String schoolClass;
  private boolean status = true;
}
