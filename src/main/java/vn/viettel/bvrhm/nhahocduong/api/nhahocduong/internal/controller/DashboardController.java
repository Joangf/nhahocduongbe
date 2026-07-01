package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.DashboardService;

import java.util.Map;

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
}
