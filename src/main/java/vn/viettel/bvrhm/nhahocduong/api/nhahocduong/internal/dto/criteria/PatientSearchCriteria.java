package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.criteria;

import lombok.Data;

/**
 * @author: longlb1
 * @since: 18-Sep-23
 */
@Data
public class PatientSearchCriteria {
  String searchText;
  String organizationName;
  String areaCode;
  String schoolClass;
}
