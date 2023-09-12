package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;

import java.util.List;

// @RepositoryRestResource
public interface ExamRepository extends JpaRepository<Exam, Long> {
  @Query("SELECT e FROM Exam e where e.patientId = :patientId " +
          "ORDER BY e.id DESC")
  List<Exam> getExamsByPatientId(@Param("patientId") Long patientId);
}
