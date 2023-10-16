package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author: longlb1
 * @since: 16-Oct-23
 */
@Data
public class ExamSearchCriteria {
  private Long id;
  private LocalDate fromDate;
  private LocalDate toDate;
  private boolean status = true;
}
