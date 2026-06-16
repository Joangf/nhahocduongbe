package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TeethRecordDTO;

/**
 * @author: longlb1
 * @since: 29-Sep-23
 */
public interface TeethRecordService {
  TeethRecordDTO getTeethRecordById(Long id);

  TeethRecordDTO getTeethRecordByPatientIdAndExamId(Long patientId, Long examId);

  TeethRecordDTO upsertTeethRecord(TeethRecordDTO teethRecordDTO);
}
