package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.AcademicYearStatus;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.AffiliationStatus;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.CampaignStatus;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.AcademicYearDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TransitionResultDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.YearTransitionRequest;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.AcademicYear;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Class;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.StudentClassAffiliation;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.SystemLog;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.AcademicYearRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ClassRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamCampaignRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.StudentClassAffiliationRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.SystemLogRepository;

@Service
public class AcademicYearServiceImpl implements vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.AcademicYearService {

  @Autowired private AcademicYearRepository academicYearRepository;
  @Autowired private ClassRepository classRepository;
  @Autowired private StudentClassAffiliationRepository affiliationRepository;
  @Autowired private ExamCampaignRepository campaignRepository;
  @Autowired private SystemLogRepository systemLogRepository;

  private final ObjectMapper objectMapper = new ObjectMapper();

  // ═══════════════════ CRUD ═══════════════════

  @Override
  public List<AcademicYearDTO> getAll() {
    return academicYearRepository.findAll().stream()
        .map(this::toDTO)
        .toList();
  }

  @Override
  public AcademicYearDTO getById(Long id) {
    return academicYearRepository.findById(id)
        .map(this::toDTO)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy năm học với id=" + id));
  }

  @Override
  public AcademicYearDTO getCurrentYear() {
    return academicYearRepository.findByStatus(AcademicYearStatus.CURRENT)
        .map(this::toDTO)
        .orElseThrow(() -> new RuntimeException("Không có năm học nào đang diễn ra"));
  }

  @Override
  public AcademicYearDTO create(AcademicYearDTO dto) {
    if (dto.getStatus() != null && "CURRENT".equals(dto.getStatus()) || dto.getStatus() == null) {
      // Nếu tạo với status CURRENT, kiểm tra xem đã có năm CURRENT chưa
      if (academicYearRepository.existsByStatus(AcademicYearStatus.CURRENT)) {
        throw new RuntimeException("Đã tồn tại năm học Đang diễn ra. Chỉ được có 1 năm CURRENT.");
      }
    }

    AcademicYear entity = new AcademicYear();
    entity.setName(dto.getName());
    entity.setStartDate(dto.getStartDate());
    entity.setEndDate(dto.getEndDate());
    entity.setStatus(dto.getStatus() != null
        ? AcademicYearStatus.valueOf(dto.getStatus())
        : AcademicYearStatus.UPCOMING);
    entity.setCreatedDate(LocalDateTime.now());
    entity.setUpdatedDate(LocalDateTime.now());
    entity.setCreatedBy("system");
    entity.setUpdatedBy("system");

    return toDTO(academicYearRepository.save(entity));
  }

  @Override
  public AcademicYearDTO update(Long id, AcademicYearDTO dto) {
    AcademicYear entity = academicYearRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy năm học với id=" + id));

    // Nếu set status thành CURRENT, kiểm tra unique constraint
    if ("CURRENT".equals(dto.getStatus())) {
      academicYearRepository.findByStatus(AcademicYearStatus.CURRENT)
          .ifPresent(current -> {
            if (!current.getId().equals(id)) {
              throw new RuntimeException("Đã tồn tại năm học Đang diễn ra (id=" + current.getId() + "). Chỉ được có 1 năm CURRENT.");
            }
          });
    }

    entity.setName(dto.getName());
    entity.setStartDate(dto.getStartDate());
    entity.setEndDate(dto.getEndDate());
    if (dto.getStatus() != null) {
      entity.setStatus(AcademicYearStatus.valueOf(dto.getStatus()));
    }
    entity.setUpdatedDate(LocalDateTime.now());
    entity.setUpdatedBy("system");

    return toDTO(academicYearRepository.save(entity));
  }

  @Override
  public void delete(Long id) {
    AcademicYear entity = academicYearRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy năm học với id=" + id));

    if (entity.getStatus() == AcademicYearStatus.CURRENT) {
      throw new RuntimeException("Không thể xóa năm học đang diễn ra. Hãy chuyển năm học trước.");
    }

    academicYearRepository.deleteById(id);
  }

  // ═══════════════════ VALIDATION ═══════════════════

  @Override
  public List<String> validateBeforeTransition(Long currentYearId) {
    List<String> warnings = new ArrayList<>();
    AcademicYear currentYear = academicYearRepository.findById(currentYearId)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy năm học với id=" + currentYearId));

    // 1. Kiểm tra campaign chưa hoàn thành
    campaignRepository.findAll().stream()
        .filter(c -> c.getCampaignStatus() == CampaignStatus.IN_PROGRESS)
        .forEach(c -> warnings.add(
            "Đợt khám '" + c.getName() + "' đang ở trạng thái 'Đang diễn ra'. Nên đóng lại trước khi chuyển năm."));

    // 2. Kiểm tra đã có class cho năm mới chưa (nếu có request cụ thể thì mới check)
    // Validation này sẽ chạy trong transition khi đã biết newYearId

    return warnings;
  }

  // ═══════════════════ TRANSITION WORKFLOW ═══════════════════

  @Override
  @Transactional
  public TransitionResultDTO transitionToNewYear(YearTransitionRequest request) {
    String sessionId = UUID.randomUUID().toString();
    TransitionResultDTO result = new TransitionResultDTO();
    result.setSessionId(sessionId);

    // ── Bước 0: Tìm năm học hiện tại ──
    AcademicYear oldYear = academicYearRepository.findByStatus(AcademicYearStatus.CURRENT)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy năm học Đang diễn ra để chuyển đổi"));
    result.setOldYearId(oldYear.getId());

    try {
      // ── Bước 1: Kiểm tra validation ──
      List<String> warnings = validateBeforeTransition(oldYear.getId());
      if (!warnings.isEmpty()) {
        result.setWarnings(warnings);
      }

      // ── Bước 2: Đóng năm cũ ──
      String oldYearJson = objectMapper.writeValueAsString(
          java.util.Map.of("name", oldYear.getName(), "status", oldYear.getStatus().name()));
      oldYear.setStatus(AcademicYearStatus.COMPLETED);
      oldYear.setUpdatedDate(LocalDateTime.now());
      academicYearRepository.save(oldYear);

      systemLogRepository.save(buildLog(sessionId, "YEAR_TRANSITION", "ACADEMIC_YEAR",
          oldYear.getId(), oldYearJson,
          "{\"status\":\"COMPLETED\"}"));

      // ── Bước 3: Tạo năm học mới ──
      AcademicYear newYear = new AcademicYear();
      newYear.setName(request.getNewYearName());
      newYear.setStartDate(request.getStartDate());
      newYear.setEndDate(request.getEndDate());
      newYear.setStatus(AcademicYearStatus.CURRENT);
      newYear.setCreatedDate(LocalDateTime.now());
      newYear.setUpdatedDate(LocalDateTime.now());
      newYear.setCreatedBy("system");
      newYear = academicYearRepository.save(newYear);
      result.setNewYearId(newYear.getId());
      result.setNewYearName(newYear.getName());

      systemLogRepository.save(buildLog(sessionId, "YEAR_TRANSITION", "ACADEMIC_YEAR",
          newYear.getId(), null,
          "{\"name\":\"" + newYear.getName() + "\",\"status\":\"CURRENT\"}"));

      // ── Bước 4: Tự động lên lớp ──
      autoPromoteStudents(oldYear, newYear, sessionId, result);

      result.setSuccess(true);
      result.setMessage("Chuyển năm học thành công! " + result.getPromotedCount()
          + " học sinh được lên lớp, " + result.getGraduatedCount() + " học sinh tốt nghiệp.");

    } catch (Exception e) {
      result.setSuccess(false);
      result.setMessage("Chuyển năm học thất bại: " + e.getMessage());
      // Transaction sẽ tự rollback
      throw new RuntimeException("Chuyển năm học thất bại, toàn bộ thay đổi đã được rollback: " + e.getMessage(), e);
    }

    return result;
  }

  // ═══════════════════ AUTO-PROMOTE LOGIC ═══════════════════

  private void autoPromoteStudents(AcademicYear oldYear, AcademicYear newYear,
      String sessionId, TransitionResultDTO result) {
    int promotedCount = 0;
    int graduatedCount = 0;

    // Lấy tất cả học sinh đang học ở năm cũ
    List<StudentClassAffiliation> currentAffiliations =
        affiliationRepository.findByAcademicYearIdAndStatus(oldYear.getId(), "STUDYING");

    for (StudentClassAffiliation aff : currentAffiliations) {
      Class oldClass = aff.getStudentClass();
      int oldGrade = Integer.parseInt(oldClass.getGrade());

      if (oldGrade >= 12) {
        // ── Edge case: Lớp 12 → tốt nghiệp ──
        aff.setStatus(AffiliationStatus.GRADUATED);
        aff.setUpdatedDate(LocalDateTime.now());
        affiliationRepository.save(aff);

        systemLogRepository.save(buildLog(sessionId, "YEAR_TRANSITION", "STUDENT_AFFILIATION",
            aff.getId(),
            "{\"status\":\"STUDYING\"}",
            "{\"status\":\"GRADUATED\"}"));
        graduatedCount++;
      } else {
        // ── Lên lớp: grade+1, giữ room và school ──
        int newGrade = oldGrade + 1;
        String newClassName = newGrade + oldClass.getRoom();

        // Tìm class mới ở năm học mới (phải được tạo sẵn)
        Class newClass = classRepository.findByNameAndSchoolIdAndAcademicYearId(
            newClassName, oldClass.getSchool().getId(), newYear.getId())
            .orElse(null);

        if (newClass == null) {
          // Nếu chưa có class, tạo mới
          newClass = new Class();
          newClass.setName(newClassName);
          newClass.setGrade(String.valueOf(newGrade));
          newClass.setRoom(oldClass.getRoom());
          newClass.setSchool(oldClass.getSchool());
          newClass.setAcademicYear(newYear);
          newClass.setStatus(true);
          newClass.setCreatedDate(LocalDateTime.now());
          newClass.setUpdatedDate(LocalDateTime.now());
          newClass = classRepository.save(newClass);

          systemLogRepository.save(buildLog(sessionId, "YEAR_TRANSITION", "CLASS",
              newClass.getId(), null,
              "{\"name\":\"" + newClassName + "\",\"grade\":\"" + newGrade
                  + "\",\"school_id\":" + oldClass.getSchool().getId() + "}"));
        }

        // Tạo affiliation mới cho năm học mới
        StudentClassAffiliation newAff = new StudentClassAffiliation();
        newAff.setStudent(aff.getStudent());
        newAff.setStudentClass(newClass);
        newAff.setAcademicYear(newYear);
        newAff.setStatus(AffiliationStatus.STUDYING);
        newAff.setCreatedDate(LocalDateTime.now());
        newAff.setUpdatedDate(LocalDateTime.now());
        newAff.setCreatedBy("system");
        newAff = affiliationRepository.save(newAff);

        systemLogRepository.save(buildLog(sessionId, "YEAR_TRANSITION", "STUDENT_AFFILIATION",
            newAff.getId(), null,
            "{\"student_id\":" + aff.getStudent().getId()
                + ",\"class_id\":" + newClass.getId()
                + ",\"status\":\"STUDYING\"}"));
        promotedCount++;
      }
    }

    result.setPromotedCount(promotedCount);
    result.setGraduatedCount(graduatedCount);
  }

  // ═══════════════════ ROLLBACK ═══════════════════

  @Override
  @Transactional
  public TransitionResultDTO rollbackTransition(String sessionId) {
    List<SystemLog> logs = systemLogRepository.findBySessionIdOrderByCreatedDateDesc(sessionId);

    if (logs.isEmpty()) {
      throw new RuntimeException("Không tìm thấy phiên chuyển năm với sessionId=" + sessionId);
    }

    TransitionResultDTO result = new TransitionResultDTO();
    result.setSessionId(sessionId);

    // Tìm newYearId và oldYearId từ logs
    Long newYearId = null;
    Long oldYearId = null;
    for (SystemLog log : logs) {
      if ("ACADEMIC_YEAR".equals(log.getEntityType())) {
        if (log.getOldValue() != null) {
          // oldValue chứa status cũ (COMPLETED → oldYear)
          oldYearId = log.getEntityId();
        } else {
          // newValue chứa status CURRENT (newYear)
          newYearId = log.getEntityId();
        }
      }
    }

    // 1. Xóa affiliations của năm mới
    if (newYearId != null) {
      affiliationRepository.deleteByAcademicYearId(newYearId);
    }

    // 2. Xóa classes của năm mới
    if (newYearId != null) {
      classRepository.findByAcademicYearId(newYearId)
          .forEach(c -> classRepository.delete(c));
    }

    // 3. Khôi phục trạng thái affiliations cũ
    if (oldYearId != null) {
      affiliationRepository.findByAcademicYearId(oldYearId)
          .forEach(aff -> {
            if (aff.getStatus() == AffiliationStatus.GRADUATED) {
              aff.setStatus(AffiliationStatus.STUDYING);
              affiliationRepository.save(aff);
            }
          });
    }

    // 4. Trả năm cũ về CURRENT
    if (oldYearId != null) {
      academicYearRepository.findById(oldYearId).ifPresent(oldYear -> {
        oldYear.setStatus(AcademicYearStatus.CURRENT);
        academicYearRepository.save(oldYear);
      });
    }

    // 5. Xóa năm mới
    if (newYearId != null) {
      academicYearRepository.deleteById(newYearId);
    }

    // 6. Xóa system_log của phiên này
    systemLogRepository.deleteBySessionId(sessionId);

    result.setSuccess(true);
    result.setMessage("Khôi phục thành công! Năm học cũ đã được trả về trạng thái Đang diễn ra.");
    return result;
  }

  // ═══════════════════ HELPERS ═══════════════════

  private AcademicYearDTO toDTO(AcademicYear entity) {
    AcademicYearDTO dto = new AcademicYearDTO();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setStartDate(entity.getStartDate());
    dto.setEndDate(entity.getEndDate());
    dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
    return dto;
  }

  private SystemLog buildLog(String sessionId, String action, String entityType,
      Long entityId, String oldValue, String newValue) {
    SystemLog log = new SystemLog();
    log.setSessionId(sessionId);
    log.setAction(action);
    log.setEntityType(entityType);
    log.setEntityId(entityId);
    log.setOldValue(oldValue);
    log.setNewValue(newValue);
    log.setCreatedDate(LocalDateTime.now());
    log.setCreatedBy("system");
    return log;
  }
}
