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

  @Autowired private vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository patientRepository;
  @Autowired private vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamScheduleRepository examScheduleRepository;
  @Autowired private vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.NotificationService notificationService;

  @Override
  public List<vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.StudentExamStatusDTO> getStudentsByCampaignId(Long campaignId) {
    return patientRepository.findStudentExamStatusByCampaignId(campaignId);
  }

  @Override
  @Transactional
  public int notifyDentists(Long campaignId) {
    ExamCampaign campaign = examCampaignRepository.findByIdAndStatus(campaignId, true)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Campaign not found with id: " + campaignId));

    List<vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamSchedule> schedules =
        examScheduleRepository.findByCampaignIdAndStatus(campaignId, true);

    if (schedules.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Không có lịch khám nào trong đợt này");
    }

    // Gom nhóm lịch khám theo bác sĩ (theo userId)
    java.util.Map<Long, java.util.List<String>> dentistScheduleMap = new java.util.LinkedHashMap<>();

    for (vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamSchedule schedule : schedules) {
      if (schedule.getDentists() == null || schedule.getDentists().isEmpty()) {
        continue;
      }
      String schoolName = schedule.getOrganization() != null
          ? schedule.getOrganization().getName()
          : "N/A";
      String detail = schoolName + " - Lớp " + schedule.getSchoolClass()
          + " - Ngày " + schedule.getExamDate();

      for (vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Dentist dentist : schedule.getDentists()) {
        Long userId = dentist.getUserId();
        if (userId != null) {
          dentistScheduleMap.computeIfAbsent(userId, k -> new java.util.ArrayList<>()).add(detail);
        }
      }
    }

    if (dentistScheduleMap.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Không có bác sĩ nào được phân công trong đợt này");
    }

    // Tạo thông báo cho từng bác sĩ
    int notifiedCount = 0;
    for (java.util.Map.Entry<Long, java.util.List<String>> entry : dentistScheduleMap.entrySet()) {
      Long userId = entry.getKey();
      java.util.List<String> scheduleDetails = entry.getValue();
      notificationService.createNotificationForDentist(
          userId, campaignId, campaign.getName(), scheduleDetails);
      notifiedCount++;
    }

    return notifiedCount;
  }
}
