package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.DashboardService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothProblem;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ExamCampaignRepository campaignRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ExamRepository examRepository;

    // ── getCampaignStats: thống kê nhanh cho 4 card trên cùng ──────────────
    @Override
    @Cacheable(value = "dashboardCampaignStats")
    public Map<String, Object> getCampaignStats() {
        // FIX: dùng COUNT query thay vì load toàn bộ list
        long totalCampaigns  = campaignRepository.count();
        long activeCampaigns = campaignRepository.countByStatus(true);  // ← was: findAll().size()
        long totalStudents   = patientRepository.count();
        // FIX: dùng countTotalExamined() thay vì findAll().stream().filter()
        long totalExamined   = examRepository.countTotalExamined();     // ← was: findAll() full scan

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCampaigns",  totalCampaigns);
        stats.put("activeCampaigns", activeCampaigns);
        stats.put("totalStudents",   totalStudents);
        stats.put("totalExamined",   totalExamined);
        return stats;
    }

    // ── getStats: thống kê chi tiết (biểu đồ, heatmap, top trường) ─────────
    @Override
    @Cacheable(value = "dashboardStats", key = "#root.target.getCacheKey()")
    public Map<String, Object> getStats() {
        // FIX: dùng campaignStats đã cache thay vì tính lại
        Map<String, Object> campaignStats = getCampaignStats();

        // FIX: 1 query duy nhất với JOIN FETCH (tránh N+1 Organization + TeethRecord)
        // Trước đây: findAll() x2 → O(N) lazy load queries
        List<Exam> allExams = examRepository.findAllActiveWithDetails();

        // 1. Tỷ lệ sâu răng theo trường/lớp
        List<Map<String, Object>> cariesBySchoolClass = calculateCariesBySchoolClass(allExams);

        // 2. Biểu đồ theo năm học
        List<Map<String, Object>> statsByYear = calculateStatsByYear(allExams);

        // 3. Top trường có tỷ lệ bệnh răng miệng cao
        List<Map<String, Object>> topSchoolsCaries = calculateTopSchools(cariesBySchoolClass);

        // 4. Heatmap phân bố bệnh lý (số ca sâu răng theo từng vị trí răng 11-48)
        Map<String, Integer> pathologyHeatmap = calculatePathologyHeatmap(allExams);

        Map<String, Object> detailedStats = new HashMap<>(campaignStats);
        detailedStats.put("cariesBySchoolClass", cariesBySchoolClass);
        detailedStats.put("statsByYear",         statsByYear);
        detailedStats.put("topSchoolsCaries",    topSchoolsCaries);
        detailedStats.put("pathologyHeatmap",    pathologyHeatmap);

        return detailedStats;
    }

    // ── Evict tất cả cache dashboard khi data thay đổi ──────────────────────
    @CacheEvict(value = {"dashboardStats", "dashboardCampaignStats"}, allEntries = true)
    public void evictDashboardCache() {
        // Gọi method này sau khi tạo/sửa/xóa Exam hoặc Campaign
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private List<Map<String, Object>> calculateCariesBySchoolClass(List<Exam> exams) {
        // FIX: Organization đã được JOIN FETCH → không còn N+1
        Map<String, List<Exam>> grouped = exams.stream()
                .filter(e -> e.getOrganization() != null && e.getSchoolClass() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getOrganization().getId() + "_" + e.getSchoolClass()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Exam>> entry : grouped.entrySet()) {
            List<Exam> groupExams = entry.getValue();
            Exam first = groupExams.get(0);


            long totalExamined = groupExams.size();
            long cariesCount   = groupExams.stream().filter(this::hasCaries).count();
            double cariesRate  = totalExamined > 0 ? Math.round((double) cariesCount / totalExamined * 1000.0) / 10.0 : 0.0;

            Map<String, Object> map = new HashMap<>();
            map.put("schoolId",      first.getOrganization().getId());
            map.put("schoolName",    first.getOrganization().getName());
            map.put("schoolClass",   first.getSchoolClass());
            map.put("totalExamined", totalExamined);
            map.put("cariesCount",   cariesCount);
            map.put("cariesRate",    cariesRate);
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
            String year          = entry.getKey();
            List<Exam> yearExams = entry.getValue();
            long totalExamined   = yearExams.size();
            long cariesCount     = yearExams.stream().filter(this::hasCaries).count();
            double cariesRate    = totalExamined > 0 ? Math.round((double) cariesCount / totalExamined * 1000.0) / 10.0 : 0.0;

            Map<String, Object> map = new HashMap<>();
            map.put("year",          year);
            map.put("totalExamined", totalExamined);
            map.put("cariesCount",   cariesCount);
            map.put("cariesRate",    cariesRate);
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
            String schoolName              = entry.getKey();
            List<Map<String, Object>> list = entry.getValue();

            long   totalExamined = list.stream().mapToLong(m -> (long) m.get("totalExamined")).sum();
            long   cariesCount   = list.stream().mapToLong(m -> (long) m.get("cariesCount")).sum();
            double cariesRate    = totalExamined > 0 ? Math.round((double) cariesCount / totalExamined * 1000.0) / 10.0 : 0.0;

            Map<String, Object> map = new HashMap<>();
            map.put("schoolName",    schoolName);
            map.put("totalExamined", totalExamined);
            map.put("cariesCount",   cariesCount);
            map.put("cariesRate",    cariesRate);
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
            // FIX: TeethRecord đã JOIN FETCH → không còn N+1
            TeethRecord tr = exam.getTeethRecord();
            if (tr != null && tr.getRecord() != null) {
                for (Map.Entry<vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Tooth, ToothCondition> entry : tr.getRecord().entrySet()) {
                    ToothCondition cond = entry.getValue();
                    if (cond != null && cond.getProblem() == ToothProblem.CARIES) {
                        String numericCode = entry.getKey().name().replaceAll("\\D+", "");
                        if (!numericCode.isEmpty()) {
                            heatmap.merge(numericCode, 1, Integer::sum);
                        }
                    }
                }
            }
        }
        return heatmap;
    }

    private boolean hasCaries(Exam exam) {
        TeethRecord tr = exam.getTeethRecord();
        if (tr == null || tr.getRecord() == null) return false;
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
