package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamScheduleDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Dentist;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamCampaign;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamSchedule;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.ExamScheduleMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.DentistRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamCampaignRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamScheduleRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.OrganizationRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamScheduleService;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.User;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.UserRepository;

@Service
public class ExamScheduleServiceImpl implements ExamScheduleService {

  @Autowired private ExamScheduleRepository examScheduleRepository;
  @Autowired private ExamCampaignRepository examCampaignRepository;
  @Autowired private OrganizationRepository organizationRepository;
  @Autowired private DentistRepository dentistRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private ExamScheduleMapper examScheduleMapper;

  @Override
  @Transactional(readOnly = true)
  public List<ExamScheduleDTO> getSchedulesByCampaignId(Long campaignId) {
    examCampaignRepository.findByIdAndStatus(campaignId, true)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found with id: " + campaignId));

    List<ExamSchedule> schedules = examScheduleRepository.findByCampaignIdAndStatus(campaignId, true);
    return examScheduleMapper.toDtoList(schedules);
  }

  @Override
  @Transactional
  public ExamScheduleDTO addOrUpdateSchedule(Long campaignId, ExamScheduleDTO dto) {
    ExamCampaign campaign = examCampaignRepository.findByIdAndStatus(campaignId, true)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found with id: " + campaignId));

    Organization organization = organizationRepository.findById(dto.getOrganizationId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found with id: " + dto.getOrganizationId()));

    if (dto.getSchoolClass() == null || dto.getSchoolClass().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "School class cannot be empty");
    }

    if (dto.getExamDate() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exam date cannot be empty");
    }

    Optional<ExamSchedule> existingScheduleOpt = examScheduleRepository
        .findByCampaignIdAndOrganizationIdAndSchoolClassAndStatus(
            campaignId, dto.getOrganizationId(), dto.getSchoolClass(), true);

    ExamSchedule schedule;
    if (existingScheduleOpt.isPresent()) {
      schedule = existingScheduleOpt.get();
      schedule.setExamDate(dto.getExamDate());
    } else {
      schedule = new ExamSchedule();
      schedule.setCampaign(campaign);
      schedule.setOrganization(organization);
      schedule.setSchoolClass(dto.getSchoolClass());
      schedule.setExamDate(dto.getExamDate());
      schedule.setStatus(true);
    }

    // Xử lý danh sách bác sĩ - lọc bỏ các bác sĩ có tài khoản bị khóa
    if (dto.getDentistIds() != null && !dto.getDentistIds().isEmpty()) {
      List<Dentist> dentists = dentistRepository.findAllById(dto.getDentistIds());
      // Lọc bỏ các bác sĩ có tài khoản đã bị khóa hoặc chưa được duyệt
      Set<Dentist> activeDentists = dentists.stream()
          .filter(dentist -> {
            Optional<User> userOpt = userRepository.findById(dentist.getUserId());
            return userOpt.isPresent()
                && userOpt.get().getStatus() != null
                && userOpt.get().getStatus()
                && userOpt.get().getRegisterStatus() != null
                && userOpt.get().getRegisterStatus();
          })
          .collect(Collectors.toSet());
      schedule.setDentists(new HashSet<>(activeDentists));
    } else {
      schedule.setDentists(new HashSet<>());
    }

    ExamSchedule saved = examScheduleRepository.save(schedule);
    return examScheduleMapper.toDto(saved);
  }

  @Override
  @Transactional
  public boolean deleteSchedule(Long campaignId, Long scheduleId) {
    examCampaignRepository.findByIdAndStatus(campaignId, true)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found with id: " + campaignId));

    ExamSchedule schedule = examScheduleRepository.findByIdAndCampaignIdAndStatus(scheduleId, campaignId, true).orElse(null);
    if (schedule != null) {
      schedule.setStatus(false);
      examScheduleRepository.save(schedule);
      return true;
    }
    return false;
  }
}
