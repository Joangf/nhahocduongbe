package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.util.List;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamCampaignDTO;

public interface ExamCampaignService {
  List<ExamCampaignDTO> getAllActiveCampaigns();
  ExamCampaignDTO getCampaignById(Long id);
  ExamCampaignDTO createCampaign(ExamCampaignDTO dto);
  ExamCampaignDTO updateCampaign(Long id, ExamCampaignDTO dto);
  boolean deleteCampaign(Long id);
}
