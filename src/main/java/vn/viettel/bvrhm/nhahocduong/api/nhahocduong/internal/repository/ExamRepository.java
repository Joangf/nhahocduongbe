package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.ExamSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
  List<Exam> getExamsByPatientIdAndStatusOrderByIdDesc(Long patientId, boolean status);

  Exam findExamByIdAndPatientId(Long id, Long patientId);

  Exam findExamByIdAndPatientIdAndStatus(Long id, Long patientId, boolean status);

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
      AND (:#{#searchCriteria.getPatientId()} IS NULL OR e.patient.id = :#{#searchCriteria.getPatientId()})
      AND (COALESCE(:#{#searchCriteria.getFromDate()}, NULL) IS NULL OR e.date >= :#{#searchCriteria.getFromDate()})
      AND (COALESCE(:#{#searchCriteria.getToDate()}, NULL) IS NULL OR e.date <= :#{#searchCriteria.getToDate()})
      AND e.status = :#{#searchCriteria.isStatus()}
  """)
  Page<Exam> search(ExamSearchCriteria searchCriteria, Pageable pageable);

  @Query("""
    SELECT e FROM Exam e
    LEFT JOIN FETCH e.patient
    LEFT JOIN FETCH e.dentist
    LEFT JOIN FETCH e.organization
    LEFT JOIN FETCH e.teethRecord
    LEFT JOIN FETCH e.campaign
    WHERE e.reExamDate IS NOT NULL AND e.reExamDate >= CURRENT_DATE
    ORDER BY e.reExamDate ASC
  """)
  List<Exam> findUpcomingReExams();

  @Query("SELECT COUNT(e) FROM Exam e WHERE e.status = true OR e.status IS NULL")
  Long countTotalExamined();

  /**
   * Dashboard query: fetch active exams với JOIN FETCH để tránh N+1 lazy loading.
   * Load Organization và TeethRecord trong cùng 1 SQL query.
   */
  @Query("SELECT DISTINCT e FROM Exam e LEFT JOIN FETCH e.organization LEFT JOIN FETCH e.teethRecord WHERE e.status = true")
  List<Exam> findAllActiveWithDetails();

  Exam findTopByPatientIdAndStatusOrderByIdDesc(Long patientId, boolean status);

  @Query("""
    SELECT e FROM Exam e
    LEFT JOIN FETCH e.patient
    LEFT JOIN FETCH e.dentist
    LEFT JOIN FETCH e.organization
    LEFT JOIN FETCH e.teethRecord
    LEFT JOIN FETCH e.campaign
    WHERE e.status = true OR e.status IS NULL
  """)
  List<Exam> findAllActiveWithAssociations();

  @Query("""
    SELECT e FROM Exam e
    LEFT JOIN FETCH e.patient
    LEFT JOIN FETCH e.dentist
    LEFT JOIN FETCH e.organization
    LEFT JOIN FETCH e.teethRecord
    LEFT JOIN FETCH e.campaign
    WHERE (e.status = true OR e.status IS NULL)
      AND e.organization.id = :orgId
  """)
  List<Exam> findAllActiveByOrganization(@Param("orgId") Long orgId);
}
