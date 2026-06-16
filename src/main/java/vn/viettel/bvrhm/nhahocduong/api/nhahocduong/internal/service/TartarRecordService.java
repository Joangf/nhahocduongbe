package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TartarRecordDTO;

/**
 * @author: longlb1
 * @since: 29-Sep-23
 */
public interface TartarRecordService {
  TartarRecordDTO getTartarRecordByPatientIdAndExamId(Long patientId, Long examId);

  TartarRecordDTO upsertTartarRecord(TartarRecordDTO tartarRecordDTO);
}
