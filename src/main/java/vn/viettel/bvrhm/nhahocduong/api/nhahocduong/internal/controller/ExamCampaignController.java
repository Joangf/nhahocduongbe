package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamCampaignDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamScheduleDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamCampaignService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamScheduleService;

@RestController
@RequestMapping("/api/exam-campaigns")
public class ExamCampaignController {

  @Autowired private ExamCampaignService examCampaignService;
  @Autowired private ExamScheduleService examScheduleService;

  @GetMapping("")
  public List<ExamCampaignDTO> getAllCampaigns() {
    return examCampaignService.getAllActiveCampaigns();
  }

  @GetMapping("/{id}")
  public ExamCampaignDTO getCampaignById(@PathVariable Long id) {
    return examCampaignService.getCampaignById(id);
  }

  @PostMapping("")
  public ExamCampaignDTO createCampaign(@RequestBody ExamCampaignDTO dto) {
    return examCampaignService.createCampaign(dto);
  }

  @PutMapping("/{id}")
  public ExamCampaignDTO updateCampaign(@PathVariable Long id, @RequestBody ExamCampaignDTO dto) {
    return examCampaignService.updateCampaign(id, dto);
  }

  @DeleteMapping("/{id}")
  public boolean deleteCampaign(@PathVariable Long id) {
    return examCampaignService.deleteCampaign(id);
  }

  @GetMapping("/{campaignId}/schedules")
  public List<ExamScheduleDTO> getSchedulesByCampaignId(@PathVariable Long campaignId) {
    return examScheduleService.getSchedulesByCampaignId(campaignId);
  }

  @PostMapping("/{campaignId}/schedules")
  public ExamScheduleDTO addOrUpdateSchedule(
      @PathVariable Long campaignId, @RequestBody ExamScheduleDTO dto) {
    return examScheduleService.addOrUpdateSchedule(campaignId, dto);
  }

  @DeleteMapping("/{campaignId}/schedules/{scheduleId}")
  public boolean deleteSchedule(
      @PathVariable Long campaignId, @PathVariable Long scheduleId) {
    return examScheduleService.deleteSchedule(campaignId, scheduleId);
  }

  @GetMapping("/{campaignId}/students")
  public List<vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.StudentExamStatusDTO> getStudentsByCampaignId(@PathVariable Long campaignId) {
    return examCampaignService.getStudentsByCampaignId(campaignId);
  }

  @PostMapping("/{campaignId}/notify")
  public void notifyDentists(@PathVariable Long campaignId) {
    examCampaignService.notifyDentists(campaignId);
  }
}
