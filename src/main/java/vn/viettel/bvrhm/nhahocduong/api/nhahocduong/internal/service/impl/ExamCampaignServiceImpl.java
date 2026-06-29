package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamCampaignDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamCampaign;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.ExamCampaignMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamCampaignRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamCampaignService;

@Service
public class ExamCampaignServiceImpl implements ExamCampaignService {

  @Autowired private ExamCampaignRepository examCampaignRepository;
  @Autowired private ExamCampaignMapper examCampaignMapper;

  @Override
  public List<ExamCampaignDTO> getAllActiveCampaigns() {
    List<ExamCampaign> campaigns = examCampaignRepository.findAllByStatusOrderByIdDesc(true);
    return examCampaignMapper.toDtoList(campaigns);
  }

  @Override
  public ExamCampaignDTO getCampaignById(Long id) {
    ExamCampaign campaign = examCampaignRepository.findByIdAndStatus(id, true)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found with id: " + id));
    return examCampaignMapper.toDto(campaign);
  }

  @Override
  @Transactional
  public ExamCampaignDTO createCampaign(ExamCampaignDTO dto) {
    ExamCampaign campaign = examCampaignMapper.toEntity(dto);
    campaign.setId(null);
    campaign.setStatus(true);
    ExamCampaign saved = examCampaignRepository.save(campaign);
    return examCampaignMapper.toDto(saved);
  }

  @Override
  @Transactional
  public ExamCampaignDTO updateCampaign(Long id, ExamCampaignDTO dto) {
    ExamCampaign campaign = examCampaignRepository.findByIdAndStatus(id, true)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found with id: " + id));
    examCampaignMapper.partialUpdate(dto, campaign);
    ExamCampaign saved = examCampaignRepository.save(campaign);
    return examCampaignMapper.toDto(saved);
  }

  @Override
  @Transactional
  public boolean deleteCampaign(Long id) {
    ExamCampaign campaign = examCampaignRepository.findByIdAndStatus(id, true).orElse(null);
    if (campaign != null) {
      campaign.setStatus(false);
      examCampaignRepository.save(campaign);
      return true;
    }
    return false;
  }
}
