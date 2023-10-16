package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.ExamSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
  List<Exam> getExamsByPatientIdAndStatusOrderByIdDesc(Long patientId, boolean status);

  Exam findExamByIdAndPatientId(Long id, Long patientId);

  Exam findExamByIdAndPatientIdAndStatus(Long id, Long patientId,boolean status);

  //  @Query("""
  //    SELECT e
  //    FROM Exam e
  //    WHERE
  //      (:id IS NULL OR e.id = :id)
  //      AND (:fromDate IS NULL OR e.date >= :fromDate)
  //      AND (:toDate IS NULL OR e.date <= :toDate)
  //      AND e.status = :status
  //  """)
  //  Page<Exam> search(Long id, LocalDate fromDate, LocalDate toDate, boolean status);

  @Query(
      """
    SELECT e
    FROM Exam e
    WHERE
      (:#{#searchCriteria.getId()} IS NULL OR e.id = :#{#searchCriteria.getId()} )
      AND (:#{#searchCriteria.patientId()} IS NULL OR e.patientId = :#{#searchCriteria.patientId()})
      AND (COALESCE(:#{#searchCriteria.getFromDate()}, NULL) IS NULL OR e.date >= :#{#searchCriteria.getFromDate()})
      AND (COALESCE(:#{#searchCriteria.getToDate()}, NULL) IS NULL OR e.date <= :#{#searchCriteria.getToDate()})
      AND e.status = :#{#searchCriteria.isStatus()}
  """)
  Page<Exam> search(ExamSearchCriteria searchCriteria, Pageable pageable);
}
