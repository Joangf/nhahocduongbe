package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.ExamSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;

/**
 * @author: longlb1
 * @since: 26-Sep-23
 */
public interface ExamService {
  List<ExamDTO> getExamsByPatientIdAndStatus(Long patientId, boolean status);

  ExamDTO getExamByIdAndPatientIdAndStatus(Long id, Long patientId, boolean status);

  ExamDTO createExam(ExamDTO newExamDTO);

  ExamDTO updateTeethRecordIdOfExam(Long examId, Long teethRecordId);

  ExamDTO updatePlaqueRecordIdOfExam(Long examId, Long plaqueRecordId);

  ExamDTO updateTartarRecordIdOfExam(Long examId, Long tartarRecordId);

  List<String> getChronicDiseasesCodesByExamId(Long examId);

  List<String> updateChronicDiseasesCodesByExamId(Long examId, List<String> diseaseCodeList);

  boolean delete(Long id);

  ExamDTO updateExam(ExamDTO examDTO);

  Page<ExamDTO> search(ExamSearchCriteria searchCriteria, Pageable pageable);
}
