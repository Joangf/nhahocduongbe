package vn.viettel.bvrhm.nhahocduong.api.common.internal.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author: longlb1
 * @since: 12-Oct-23
 */
@Data
@Builder
public class ResponseModel {
  int status;
  String message;
  Object content;
}
