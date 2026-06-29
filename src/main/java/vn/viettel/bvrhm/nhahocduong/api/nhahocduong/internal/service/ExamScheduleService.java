package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.util.List;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamScheduleDTO;

public interface ExamScheduleService {
  List<ExamScheduleDTO> getSchedulesByCampaignId(Long campaignId);
  ExamScheduleDTO addOrUpdateSchedule(Long campaignId, ExamScheduleDTO dto);
  boolean deleteSchedule(Long campaignId, Long scheduleId);
}
