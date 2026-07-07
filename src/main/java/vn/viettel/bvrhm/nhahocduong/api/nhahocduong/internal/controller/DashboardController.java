package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.StudentCountBySchoolDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.YearlyStudentCountDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/campaign-stats")
    public Map<String, Object> getCampaignStats() {
        return dashboardService.getCampaignStats();
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return dashboardService.getStats();
    }

    @GetMapping("/student-count")
    public List<StudentCountBySchoolDTO> getStudentCountByYear(@RequestParam Long academicYearId) {
        return dashboardService.getStudentCountByYear(academicYearId);
    }

    @GetMapping("/student-count-history")
    public List<YearlyStudentCountDTO> getStudentCountHistory() {
        return dashboardService.getStudentCountHistory();
    }
}
