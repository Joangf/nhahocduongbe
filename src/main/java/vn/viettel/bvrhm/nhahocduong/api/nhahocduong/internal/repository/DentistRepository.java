package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Dentist;

@Repository
public interface DentistRepository extends JpaRepository<Dentist, Long> {
  List<Dentist> findByUserIdAndTitle(Long userId, String title);

  List<Dentist> findByUserId(Long userId);

  List<Dentist> findByTitle(String title);
}
