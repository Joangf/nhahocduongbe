package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TreatmentRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;

import java.util.List;

/**
 * @author: longlb1
 * @since: 26-Sep-23
 */
public interface ExamService {
    List<ExamDTO> getExamsByPatientIdAndStatus(Long patientId, boolean status);

    ExamDTO getExamByIdAndStatus(Long id, boolean status);

    ExamDTO createExam(ExamDTO newExamDTO);
    
    ExamDTO updateTeethRecordIdOfExamId(Long examId, Long teethRecordId);
    
    ExamDTO updatePlaqueRecordIdOfExam(Long examId, Long plaqueRecordId);
    
    ExamDTO updateTartarRecordIdOfExam(Long examId, Long tartarRecordId);

    List<String> getChronicDiseasesCodesByExamId(Long examId);
    List<String> updateChronicDiseasesCodesByExamId(Long examId, List<String> diseaseCodeList);

    ExamDTO injectChildObject(Exam entity);

    boolean delete(Long id);

    List<TreatmentRecordDTO> upsertTreatmentRecordsByExamId(Long examId, List<TreatmentRecordDTO> treatmentRecordDTOS);

    List<TreatmentRecordDTO>  getTreatmentRecordsByExamId(Long examId);

    boolean deleteTreatmentRecord(Long examId, Long treatmentRecordId);

    ExamDTO updateExam(ExamDTO examDTO);
}
