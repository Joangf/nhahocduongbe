package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PlaqueRecordDTO;

/**
 * @author: longlb1
 * @since: 29-Sep-23
 */
public interface PlaqueRecordService {
  PlaqueRecordDTO getPlaqueRecordByPatientIdAndExamId(Long patientId, Long examId);

  PlaqueRecordDTO upsertPlaqueRecord(PlaqueRecordDTO plaqueRecordDTO);
}
