package vn.viettel.bvrhm.nhahocduong.api.common.internal.model.response;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

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
