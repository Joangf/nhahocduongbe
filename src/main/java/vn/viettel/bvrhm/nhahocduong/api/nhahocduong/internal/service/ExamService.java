package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.ExamSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.AssessmentUpdateDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ImageUpdateDTO;
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

  List<ExamDTO> getReExams();

  vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DashboardStatsDTO getDashboardStats();

  /** PATCH: Cập nhật đánh giá bệnh lý & ghi chú điều trị (mục 4 & 5) */
  ExamDTO updateAssessment(Long examId, AssessmentUpdateDTO dto);

  /** PATCH: Cập nhật hoặc xóa ảnh trước/sau điều trị (mục 6) */
  ExamDTO updateImages(Long examId, ImageUpdateDTO dto);
}
