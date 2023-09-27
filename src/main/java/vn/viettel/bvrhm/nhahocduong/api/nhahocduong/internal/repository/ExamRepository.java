package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
  List<Exam> getExamsByPatientIdAndStatusOrderByIdDesc(Long patientId, boolean status);

  Exam findExamByIdAndPatientId(Long id, Long patientId);

  Exam findExamByIdAndStatus(Long id, boolean status);
}
