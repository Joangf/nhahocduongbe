package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.util.List;
import java.util.Map;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.StudentCountBySchoolDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.YearlyStudentCountDTO;

public interface DashboardService {
    Map<String, Object> getCampaignStats();
    Map<String, Object> getStats();
    List<StudentCountBySchoolDTO> getStudentCountByYear(Long academicYearId);
    List<YearlyStudentCountDTO> getStudentCountHistory();
}
