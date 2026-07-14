package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.AcademicYearStatus;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.AcademicYear;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {

  Optional<AcademicYear> findByStatus(AcademicYearStatus status);

  boolean existsByStatus(AcademicYearStatus status);

  @Query("SELECT a FROM AcademicYear a WHERE a.status = 'CURRENT'")
  Optional<AcademicYear> findCurrentYear();
}
