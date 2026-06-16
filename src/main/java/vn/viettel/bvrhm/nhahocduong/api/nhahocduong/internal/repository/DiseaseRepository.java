package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Disease;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Long> {
  Disease getByCode(String code);

  List<Disease> findByNameLike(String name);
}
