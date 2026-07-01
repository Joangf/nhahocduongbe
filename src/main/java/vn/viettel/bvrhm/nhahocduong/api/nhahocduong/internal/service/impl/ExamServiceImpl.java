package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.ResponseMessage;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.ExamSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.AssessmentUpdateDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ImageUpdateDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.ExamMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.TreatmentRecordMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamService;

@Service
public class ExamServiceImpl implements ExamService {

  @Autowired private ExamRepository examRepository;
  @Autowired private DiseaseRepository diseaseRepository;
  @Autowired private ExamMapper examMapper;
  @Autowired private TreatmentRecordMapper treatmentRecordMapper;
  @Autowired private PatientRepository patientRepository;
  @Autowired private DentistRepository dentistRepository;
  @Autowired private OrganizationRepository organizationRepository;
  @Autowired private TartarRecordRepository tartarRecordRepository;
  @Autowired private TeethRecordRepository teethRecordRepository;
  @Autowired private PlaqueRecordRepository plaqueRecordRepository;

  @Override
  public List<ExamDTO> getExamsByPatientIdAndStatus(Long patientId, boolean status) {
    List<Exam> examList =
        examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(patientId, status);
    return examMapper.toDtoList(examList);
  }

  @Override
  public ExamDTO getExamByIdAndPatientIdAndStatus(Long id, Long patientId, boolean status) {
    Exam exam = examRepository.findExamByIdAndPatientIdAndStatus(id, patientId, status);
    if (exam == null) {
      return null;
    }
    return examMapper.toDto(exam);
  }

  @Override
  public ExamDTO createExam(ExamDTO newExamDTO) {
    Exam newExam = examMapper.toEntity(newExamDTO);
    newExam.setId(null);
    Exam createdExam = examRepository.save(newExam);
    return examMapper.toDto(createdExam);
  }

  @Override
  @Retryable(
      retryFor = CannotAcquireLockException.class,
      maxAttempts = 5,
      backoff = @Backoff(delay = 300))
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public ExamDTO updateTeethRecordIdOfExam(Long examId, Long teethRecordId) {
    Exam exam = examRepository.getReferenceById(examId);
    exam.setTeethRecord(teethRecordRepository.getReferenceById(teethRecordId));
    var updated = examRepository.save(exam);
    return examMapper.toDto(updated);
  }

  @Override
  @Retryable(
      retryFor = CannotAcquireLockException.class,
      maxAttempts = 5,
      backoff = @Backoff(delay = 300))
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public ExamDTO updatePlaqueRecordIdOfExam(Long examId, Long plaqueRecordId) {
    Exam exam = examRepository.findById(examId).orElseThrow(NoSuchElementException::new);
    exam.setPlaqueRecord(plaqueRecordRepository.getReferenceById(plaqueRecordId));
    var updated = examRepository.save(exam);
    return examMapper.toDto(updated);
  }

  @Override
  @Retryable(
      retryFor = CannotAcquireLockException.class,
      maxAttempts = 5,
      backoff = @Backoff(delay = 300))
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public ExamDTO updateTartarRecordIdOfExam(Long examId, Long tartarRecordId) {
    Exam exam = examRepository.findById(examId).orElseThrow(NoSuchElementException::new);
    exam.setTartarRecord(tartarRecordRepository.getReferenceById(tartarRecordId));
    var updated = examRepository.save(exam);
    return examMapper.toDto(updated);
  }

  //  public List<PrescriptionItem> getPrescriptionByExamId(Long examId) {
  //    Exam exam = examRepository.getReferenceById(examId);
  //    if (exam.getPrescription() == null) {
  //      return new ArrayList<PrescriptionItem>();
  //    }
  //    return exam.getPrescription();
  //  }
  //
  //  public List<PrescriptionItem> updatePrescriptionByExamId(
  //      Long examId, List<PrescriptionItem> prescriptionItemList) {
  //    Exam exam = examRepository.getReferenceById(examId);
  //
  //    exam.setPrescription(prescriptionItemList);
  //    Exam saved = examRepository.save(exam);
  //    return saved.getPrescription();
  //  }

  @Override
  public List<String> getChronicDiseasesCodesByExamId(Long examId) {
    Exam exam = examRepository.getReferenceById(examId);

    List<Disease> chronicConditions = exam.getChronicConditions();
    return chronicConditions.stream().map(Disease::getCode).toList();
  }

  /** Bệnh mãn tính */
  @Override
  public List<String> updateChronicDiseasesCodesByExamId(
      Long examId, List<String> diseaseCodeList) {
    Exam exam = examRepository.findById(examId).orElseThrow(NoSuchElementException::new);

    List<Disease> newDiseaseList =
        diseaseCodeList.stream()
            .map(code -> diseaseRepository.getByCode(code))
            .collect(Collectors.toList());
    exam.setChronicConditions(newDiseaseList);
    Exam updateExam = examRepository.save(exam);
    List<Disease> chronicConditions = updateExam.getChronicConditions();
    return chronicConditions.stream().map(Disease::getCode).toList();
  }

//  @Override
//  public ExamDTO injectChildObject(Exam entity) {
//    Patient patient = patientRepository.findById(entity.getPatientId()).orElse(null);
//    Dentist dentist = dentistRepository.findById(entity.getDentistId()).orElse(null);
//    Organization organization =
//        organizationRepository.findById(entity.getOrganizationId()).orElse(null);
//
//    return new ExamDTO(
//        entity.getId(),
//        entity.getPatientId(),
//        patient.getFullName(),
//        entity.getDentistId(),
//        dentist.getTitle(),
//        entity.getOrganizationId(),
//        organization.getName(),
//        entity.getSchoolClass(),
//        entity.getYear(),
//        entity.getProfileNumber(),
//        entity.getDate(),
//        entity.getTeethRecordId(),
//        entity.getPlaqueRecordId(),
//        entity.getTartarRecordId(),
//        treatmentRecordMapper.toListDto(entity.getTreatmentRecords()),
//        entity.getStatus());
//  }

  @Override
  public boolean delete(Long id) {
    Exam exam =
        examRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ResponseMessage.EXAM_NOT_FOUND_WITH_ID + id));
    exam.setStatus(false);
    examRepository.save(exam);
    return true;
  }

  @Override
  public ExamDTO updateExam(ExamDTO examDTO) {
    Exam exam = examRepository.findExamByIdAndPatientId(examDTO.getId(), examDTO.getPatientId());
    if (isNull(exam)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          ResponseMessage.EXAM_NOT_FOUND_WITH_PATIENT_ID + examDTO.getPatientId());
    }

    examMapper.partialUpdate(examDTO, exam);
    return examMapper.toDto(examRepository.save(exam));
  }

  @Override
  public Page<ExamDTO> search(ExamSearchCriteria searchCriteria, Pageable pageable) {
    Page<Exam> exams = examRepository.search(searchCriteria, pageable);
    List<ExamDTO> examDTOList = exams.getContent().stream().map(examMapper::toDto).toList();
    return new PageImpl<>(examDTOList, pageable, exams.getTotalElements());
  }

  @Autowired private vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamCampaignRepository examCampaignRepository;

  @Override
  public List<ExamDTO> getReExams() {
    // Tìm tất cả exam đang active, kiểm tra nếu có răng sâu (CARIES) thì cần tái khám
    List<Exam> allExams = examRepository.findAll().stream()
        .filter(e -> e.getStatus() == null || e.getStatus())
        .collect(Collectors.toList());

    List<ExamDTO> reExams = new ArrayList<>();
    for (Exam exam : allExams) {
      TeethRecord tr = exam.getTeethRecord();
      if (tr != null && tr.getRecord() != null) {
        boolean hasCaries = tr.getRecord().values().stream()
            .anyMatch(cond -> cond != null
                && cond.getProblem() == vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothProblem.CARIES);
        if (hasCaries) {
          ExamDTO dto = examMapper.toDto(exam);
          // Tính ngày tái khám dự kiến = ngày khám + 6 tháng
          if (exam.getDate() != null) {
            dto.setReExamDate(exam.getDate().plusMonths(6));
          } else {
            dto.setReExamDate(java.time.LocalDate.now().plusMonths(6));
          }
          dto.setReExamNote("Cần tái khám điều trị sâu răng");
          reExams.add(dto);
        }
      }
    }
    return reExams;
  }

  @Override
  public vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DashboardStatsDTO getDashboardStats() {
    vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DashboardStatsDTO stats = new vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DashboardStatsDTO();
    
    long totalCampaigns = examCampaignRepository.count();
    long activeCampaigns = examCampaignRepository.findAllByStatusOrderByIdDesc(true).size();
    long totalStudents = patientRepository.count();
    long totalExamined = examRepository.countTotalExamined();
    
    stats.setTotalCampaigns(totalCampaigns);
    stats.setActiveCampaigns(activeCampaigns);
    stats.setTotalStudents(totalStudents);
    stats.setTotalExamined(totalExamined);
    
    return stats;
  }

  @Override
  @Transactional
  public ExamDTO updateAssessment(Long examId, AssessmentUpdateDTO dto) {
    Exam exam = examRepository
        .findById(examId)
        .orElseThrow(() ->
            new ResponseStatusException(
                HttpStatus.NOT_FOUND, ResponseMessage.EXAM_NOT_FOUND_WITH_ID + examId));

    if (dto.getPathologyAssessment() != null) {
      exam.setPathologyAssessment(dto.getPathologyAssessment());
    }
    if (dto.getTreatmentNote() != null) {
      exam.setTreatmentNote(dto.getTreatmentNote());
    }

    Exam saved = examRepository.save(exam);
    return examMapper.toDto(saved);
  }

  @Override
  @Transactional
  public ExamDTO updateImages(Long examId, ImageUpdateDTO dto) {
    Exam exam = examRepository
        .findById(examId)
        .orElseThrow(() ->
            new ResponseStatusException(
                HttpStatus.NOT_FOUND, ResponseMessage.EXAM_NOT_FOUND_WITH_ID + examId));

    // Before image: null url = xóa ảnh
    exam.setImageBeforeUrl(dto.getImageBeforeUrl());
    exam.setImageBeforeTime(dto.getImageBeforeTime());

    // After image: null url = xóa ảnh
    exam.setImageAfterUrl(dto.getImageAfterUrl());
    exam.setImageAfterTime(dto.getImageAfterTime());

    Exam saved = examRepository.save(exam);
    return examMapper.toDto(saved);
  }
}
