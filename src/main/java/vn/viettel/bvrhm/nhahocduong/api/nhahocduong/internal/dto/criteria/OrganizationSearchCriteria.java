package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.criteria;

import lombok.Data;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.OrganizationType;

/**
 * @author: longlb1
 * @since: 14-Sep-23
 */
@Data
public class OrganizationSearchCriteria {
  private String searchText;
  private String areaCode;
  private OrganizationType type = OrganizationType.SCHOOL;
  private Boolean status = true;
}
