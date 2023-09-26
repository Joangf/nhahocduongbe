package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TreatmentRecord;

import java.util.List;

/**
 * @author: longlb1
 * @since: 26-Sep-23
 */
public interface ExamService {
    List<ExamDTO> getExamsByPatientId(Long patientId);

    ExamDTO getExamById(Long id);

    ExamDTO createExam(ExamDTO newExamDTO);
    
    ExamDTO updateTeethRecordIdOfExamId(Long examId, Long teethRecordId);
    
    ExamDTO updatePlaqueRecordIdOfExam(Long examId, Long plaqueRecordId);
    
    ExamDTO updateTartarRecordIdOfExam(Long examId, Long tartarRecordId);

    List<String> getChronicDiseasesCodesByExamId(Long examId);
    List<String> updateChronicDiseasesCodesByExamId(Long examId, List<String> diseaseCodeList);

    ExamDTO injectChildObject(Exam entity);

    void delete(Long id);
    
    TreatmentRecord upsertTreatmentRecordByExamId(Long examId, TreatmentRecord treatmentRecord);

    TreatmentRecord getTreatmentRecordByExamId(Long examId);

    ExamDTO updateExam(ExamDTO examDTO);
}
