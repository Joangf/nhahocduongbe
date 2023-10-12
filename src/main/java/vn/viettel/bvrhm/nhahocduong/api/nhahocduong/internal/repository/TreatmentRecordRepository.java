package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TreatmentRecord;

@Repository
public interface TreatmentRecordRepository extends JpaRepository<TreatmentRecord, Long> {
  List<TreatmentRecord> findByExamIdAndStatus(Long id, boolean status);

  List<TreatmentRecord> findByIdIsIn(List<Long> ids);
}
