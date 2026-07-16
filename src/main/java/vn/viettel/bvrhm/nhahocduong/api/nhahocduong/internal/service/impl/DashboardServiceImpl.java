package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.cache.annotation.Cacheable;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthorizationService;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthorizationService.AuthorizationData;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.StudentCountBySchoolDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.YearlyStudentCountDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.DashboardService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Tooth;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothProblem;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ExamCampaignRepository campaignRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private StudentClassAffiliationRepository affiliationRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public Map<String, Object> getCampaignStats() {
        AuthorizationData authData = authorizationService.authorize();
        Long orgId = authData.getOrganizationId();

        long totalCampaigns = campaignRepository.count();
        long activeCampaigns = campaignRepository.countByStatus(true);
        long totalStudents = (orgId != null)
            ? patientRepository.countByOrganization_IdAndStatus(orgId, true)
            : patientRepository.count();
        long totalExamined = (orgId != null)
            ? examRepository.countTotalExaminedByOrganization(orgId)
            : examRepository.countTotalExamined();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCampaigns", totalCampaigns);
        stats.put("activeCampaigns", activeCampaigns);
        stats.put("totalStudents", totalStudents);
        stats.put("totalExamined", totalExamined);
        return stats;
    }

    @Override
    @Cacheable(value = "dashboardStats", key = "#root.target.getCacheKey()")
    public Map<String, Object> getStats() {
        AuthorizationData authData = authorizationService.authorize();
        Long orgId = authData.getOrganizationId();

        Map<String, Object> campaignStats = getCampaignStats();

        List<Exam> allExams = (orgId != null)
            ? examRepository.findAllActiveByOrganization(orgId)
            : examRepository.findAllActiveWithAssociations();

        // 1. Thống kê tỷ lệ sâu răng theo trường/lớp
        List<Map<String, Object>> cariesBySchoolClass = calculateCariesBySchoolClass(allExams);

        // 2. Biểu đồ theo năm học
        List<Map<String, Object>> statsByYear = calculateStatsByYear(allExams);

        // 3. Top trường có tỷ lệ bệnh răng miệng cao
        List<Map<String, Object>> topSchoolsCaries = calculateTopSchools(cariesBySchoolClass);

        // 4. Heatmap phân bố bệnh lý răng miệng (số ca sâu răng theo từng vị trí răng 11-48)
        Map<String, Integer> pathologyHeatmap = calculatePathologyHeatmap(allExams);

        Map<String, Object> detailedStats = new HashMap<>(campaignStats);
        detailedStats.put("cariesBySchoolClass", cariesBySchoolClass);
        detailedStats.put("statsByYear", statsByYear);
        detailedStats.put("topSchoolsCaries", topSchoolsCaries);
        detailedStats.put("pathologyHeatmap", pathologyHeatmap);

        return detailedStats;
    }

    /**
     * Returns a cache key that includes the organization ID so SCHOOL users
     * don't see cached data from other organizations.
     */
    public String getCacheKey() {
        AuthorizationData authData = authorizationService.authorize();
        Long orgId = authData.getOrganizationId();
        return orgId != null ? "org-" + orgId : "all";
    }

    private List<Map<String, Object>> calculateCariesBySchoolClass(List<Exam> exams) {
        Map<String, List<Exam>> grouped = exams.stream()
                .filter(e -> e.getOrganization() != null && e.getSchoolClass() != null)
                .collect(Collectors.groupingBy(e -> e.getOrganization().getId() + "_" + e.getSchoolClass()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Exam>> entry : grouped.entrySet()) {
            List<Exam> groupExams = entry.getValue();
            Exam first = groupExams.get(0);

            long totalExamined = groupExams.size();
            long cariesCount = groupExams.stream()
                    .filter(this::hasCaries)
                    .count();

            double cariesRate = totalExamined > 0 ? (double) cariesCount / totalExamined * 100 : 0.0;
            cariesRate = Math.round(cariesRate * 10.0) / 10.0;

            Map<String, Object> map = new HashMap<>();
            map.put("schoolId", first.getOrganization().getId());
            map.put("schoolName", first.getOrganization().getName());
            map.put("schoolClass", first.getSchoolClass());
            map.put("totalExamined", totalExamined);
            map.put("cariesCount", cariesCount);
            map.put("cariesRate", cariesRate);
            result.add(map);
        }
        return result;
    }

    private List<Map<String, Object>> calculateStatsByYear(List<Exam> exams) {
        Map<String, List<Exam>> grouped = exams.stream()
                .filter(e -> e.getYear() != null)
                .collect(Collectors.groupingBy(Exam::getYear));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Exam>> entry : grouped.entrySet()) {
            String year = entry.getKey();
            List<Exam> yearExams = entry.getValue();
            long totalExamined = yearExams.size();
            long cariesCount = yearExams.stream()
                    .filter(this::hasCaries)
                    .count();

            double cariesRate = totalExamined > 0 ? (double) cariesCount / totalExamined * 100 : 0.0;
            cariesRate = Math.round(cariesRate * 10.0) / 10.0;

            Map<String, Object> map = new HashMap<>();
            map.put("year", year);
            map.put("totalExamined", totalExamined);
            map.put("cariesCount", cariesCount);
            map.put("cariesRate", cariesRate);
            result.add(map);
        }
        result.sort(Comparator.comparing(m -> (String) m.get("year")));
        return result;
    }

    private List<Map<String, Object>> calculateTopSchools(List<Map<String, Object>> schoolClassStats) {
        Map<String, List<Map<String, Object>>> groupedBySchool = schoolClassStats.stream()
                .collect(Collectors.groupingBy(m -> (String) m.get("schoolName")));

        List<Map<String, Object>> schoolStats = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : groupedBySchool.entrySet()) {
            String schoolName = entry.getKey();
            List<Map<String, Object>> list = entry.getValue();

            long totalExamined = list.stream().mapToLong(m -> (long) m.get("totalExamined")).sum();
            long cariesCount = list.stream().mapToLong(m -> (long) m.get("cariesCount")).sum();

            double cariesRate = totalExamined > 0 ? (double) cariesCount / totalExamined * 100 : 0.0;
            cariesRate = Math.round(cariesRate * 10.0) / 10.0;

            Map<String, Object> map = new HashMap<>();
            map.put("schoolName", schoolName);
            map.put("totalExamined", totalExamined);
            map.put("cariesCount", cariesCount);
            map.put("cariesRate", cariesRate);
            schoolStats.add(map);
        }

        return schoolStats.stream()
                .sorted((m1, m2) -> Double.compare((double) m2.get("cariesRate"), (double) m1.get("cariesRate")))
                .limit(5)
                .collect(Collectors.toList());
    }

    private Map<String, Integer> calculatePathologyHeatmap(List<Exam> exams) {
        Map<String, Integer> heatmap = new HashMap<>();

        int[] regions = {1, 2, 3, 4};
        for (int r : regions) {
            for (int i = 1; i <= 8; i++) {
                heatmap.put("" + r + i, 0);
            }
        }

        for (Exam exam : exams) {
            TeethRecord tr = exam.getTeethRecord();
            if (tr != null && tr.getRecord() != null) {
                for (Map.Entry<Tooth, ToothCondition> entry : tr.getRecord().entrySet()) {
                    Tooth tooth = entry.getKey();
                    ToothCondition cond = entry.getValue();
                    if (cond != null && cond.getProblem() == ToothProblem.CARIES) {
                        String toothName = tooth.name();
                        String numericCode = toothName.replaceAll("\\D+", "");
                        if (heatmap.containsKey(numericCode)) {
                            heatmap.put(numericCode, heatmap.get(numericCode) + 1);
                        } else if (!numericCode.isEmpty()) {
                            heatmap.put(numericCode, 1);
                        }
                    }
                }
            }
        }
        return heatmap;
    }

    private boolean hasCaries(Exam exam) {
        TeethRecord tr = exam.getTeethRecord();
        if (tr == null || tr.getRecord() == null) {
            return false;
        }
        return tr.getRecord().values().stream()
                .anyMatch(cond -> cond != null && cond.getProblem() == ToothProblem.CARIES);
    }

    // ── Thống kê sĩ số theo năm học ──

    @Override
    public List<StudentCountBySchoolDTO> getStudentCountByYear(Long academicYearId) {
        AuthorizationData authData = authorizationService.authorize();
        Long orgId = authData.getOrganizationId();

        List<Object[]> rows = (orgId != null)
            ? affiliationRepository.countStudentsBySchoolAndGradeFiltered(academicYearId, orgId)
            : affiliationRepository.countStudentsBySchoolAndGrade(academicYearId);

        return rows.stream()
            .map(r -> new StudentCountBySchoolDTO(
                (String) r[0],
                (String) r[1],
                ((Number) r[2]).longValue()))
            .collect(Collectors.toList());
    }

    @Override
    public List<YearlyStudentCountDTO> getStudentCountHistory() {
        AuthorizationData authData = authorizationService.authorize();
        Long orgId = authData.getOrganizationId();

        List<Object[]> rows = (orgId != null)
            ? affiliationRepository.countStudentsByYearFiltered(orgId)
            : affiliationRepository.countStudentsByYear();

        return rows.stream()
            .map(r -> new YearlyStudentCountDTO(
                (String) r[0],
                ((Number) r[1]).longValue()))
            .collect(Collectors.toList());
    }
}
