package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.util.List;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TreatmentRecordDTO;

/**
 * @author: longlb1
 * @since: 29-Sep-23
 */
public interface TreatmentRecordService {
  List<TreatmentRecordDTO> upsertTreatmentRecordsByExamIdAndPatientId(
      Long examId, Long patientId, List<TreatmentRecordDTO> treatmentRecordDTOS);

  List<TreatmentRecordDTO> getTreatmentRecordsByExamId(Long examId);

  boolean deleteTreatmentRecord(Long examId, Long patientId, Long treatmentRecordId);
}
