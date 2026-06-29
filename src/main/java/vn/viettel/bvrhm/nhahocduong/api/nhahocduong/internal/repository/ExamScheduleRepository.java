package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamSchedule;

@Repository
public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {
  List<ExamSchedule> findByCampaignIdAndStatus(Long campaignId, Boolean status);
  Optional<ExamSchedule> findByCampaignIdAndOrganizationIdAndSchoolClassAndStatus(
      Long campaignId, Long organizationId, String schoolClass, Boolean status);
  Optional<ExamSchedule> findByIdAndCampaignIdAndStatus(Long id, Long campaignId, Boolean status);
}
