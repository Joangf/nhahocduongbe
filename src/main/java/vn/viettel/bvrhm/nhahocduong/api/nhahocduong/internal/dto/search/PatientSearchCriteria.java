package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.search;

import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author: longlb1
 * @since: 18-Sep-23
 */
@Data
public class PatientSearchCriteria {
    String searchText;
    String organizationName;
    String areaCode;
    List<String> schoolClass;
}
