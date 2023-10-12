package vn.viettel.bvrhm.nhahocduong.api.common.internal.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: longlb1
 * @since: 12-Oct-23
 */
@Data
@Builder
public class UpsertResponseModel {
    int successCount;
    int errorCount;
    List<ResponseModel> successList;
    List<ResponseModel> errorList;
}
