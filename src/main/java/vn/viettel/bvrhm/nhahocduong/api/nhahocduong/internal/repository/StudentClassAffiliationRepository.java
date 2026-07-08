package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.StudentClassAffiliation;

public interface StudentClassAffiliationRepository extends JpaRepository<StudentClassAffiliation, Long> {

  List<StudentClassAffiliation> findByAcademicYearId(Long academicYearId);

  List<StudentClassAffiliation> findByAcademicYearIdAndStatus(Long academicYearId, String status);

  Optional<StudentClassAffiliation> findByStudentIdAndAcademicYearId(Long studentId, Long academicYearId);

  long countByAcademicYearId(Long academicYearId);

  @Query(value = """
      SELECT o.name as schoolName, c.grade, COUNT(a.id) as studentCount
      FROM student_class_affiliation a
      JOIN class c ON c.id = a.class_id
      JOIN nhahocduong_organization o ON o.id = c.school_id
      WHERE a.academic_year_id = :yearId AND a.status = 'STUDYING'
      GROUP BY o.name, c.grade
      ORDER BY o.name, c.grade
      """, nativeQuery = true)
  List<Object[]> countStudentsBySchoolAndGrade(@Param("yearId") Long academicYearId);

  @Query(value = """
      SELECT ay.name as yearName, COUNT(a.id) as studentCount
      FROM student_class_affiliation a
      JOIN academic_year ay ON ay.id = a.academic_year_id
      WHERE a.status = 'STUDYING'
      GROUP BY ay.name, ay.start_date
      ORDER BY ay.start_date
      """, nativeQuery = true)
  List<Object[]> countStudentsByYear();

  void deleteByAcademicYearId(Long academicYearId);
}
