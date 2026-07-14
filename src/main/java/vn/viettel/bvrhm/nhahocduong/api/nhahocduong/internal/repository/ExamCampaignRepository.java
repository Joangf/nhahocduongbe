package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamCampaign;

@Repository
public interface ExamCampaignRepository extends JpaRepository<ExamCampaign, Long> {
  List<ExamCampaign> findAllByStatusOrderByIdDesc(Boolean status);
  Optional<ExamCampaign> findByIdAndStatus(Long id, Boolean status);
  long countByStatus(Boolean status);
}
