package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria;

import java.time.LocalDate;
import lombok.Data;

/**
 * @author: longlb1
 * @since: 16-Oct-23
 */
@Data
public class ExamSearchCriteria {
  private Long patientId;
  private Long id;
  private LocalDate fromDate;
  private LocalDate toDate;
  private boolean status = true;
}
