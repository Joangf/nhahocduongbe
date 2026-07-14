package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.AcademicYear;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class;

public interface ClassRepository extends JpaRepository<Class, Long> {

  List<Class> findByAcademicYearId(Long academicYearId);

  List<Class> findBySchoolIdAndAcademicYearId(Long schoolId, Long academicYearId);

  Optional<Class> findByNameAndSchoolIdAndAcademicYearId(String name, Long schoolId, Long academicYearId);

  boolean existsByAcademicYearId(Long academicYearId);
}
