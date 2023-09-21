package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.ExamMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.*;

@Service
public class ExamService {

  @Autowired private ExamRepository examRepository;
  @Autowired private DiseaseRepository diseaseRepository;
  @Autowired private ExamMapper examMapper;
  @Autowired private PatientRepository patientRepository;
  @Autowired private DentistRepository dentistRepository;
  @Autowired private OrganizationRepository organizationRepository;

  public List<ExamDTO> getExamsByPatientId(Long patientId) {
    List<Exam> examList = examRepository.getExamsByPatientId(patientId);

    List<ExamDTO> examDTOList = new ArrayList<ExamDTO>();
    for (Exam exam : examList) {
      ExamDTO dto = injectChildObject(exam);
      examDTOList.add(dto);
    }

    return examDTOList;
  }

  public ExamDTO getExamById(Long id) {
    Exam exam = examRepository.getReferenceById(id);
    if (exam == null) {
      return null;
    }
    //    ExamDTO dto = examMapper.toDto(exam);
    return injectChildObject(exam);
  }

  public ExamDTO createExam(ExamDTO newExamDTO) {
    Exam newExam = examMapper.toEntity(newExamDTO);
    newExam.setId(null);
    Exam createdExam = examRepository.save(newExam);
    var createdExamDTO = examMapper.toDto(createdExam);
    return createdExamDTO;
  }


  @Retryable(retryFor = CannotAcquireLockException.class, maxAttempts = 3, backoff = @Backoff(delay = 300))
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public ExamDTO updateTeethRecordIdOfExamId(
    Long examId, Long teethRecordId) {
    Exam exam = examRepository.getReferenceById(examId);
    exam.setTeethRecordId(teethRecordId);
    var updated = examRepository.save(exam);
    return examMapper.toDto(updated);
  }

  @Retryable(retryFor = CannotAcquireLockException.class, maxAttempts = 3, backoff = @Backoff(delay = 300))
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public ExamDTO updatePlaqueRecordIdOfExam(Long examId, Long plaqueRecordId) {
    Exam exam = examRepository.findById(examId).orElseThrow(NoSuchElementException::new);
    exam.setPlaqueRecordId(plaqueRecordId);
    var updated = examRepository.save(exam);
    return examMapper.toDto(updated);
  }

  @Retryable(retryFor = CannotAcquireLockException.class, maxAttempts = 3, backoff = @Backoff(delay = 300))
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public ExamDTO updateTartarRecordIdOfExam(Long examId, Long tartarRecordId) {
    Exam exam = examRepository.findById(examId).orElseThrow(NoSuchElementException::new);
    exam.setTartarRecordId(tartarRecordId);
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

  public List<String> getChronicDiseasesCodesByExamId(Long examId) {
    Exam exam = examRepository.getReferenceById(examId);

    List<Disease> chronicConditions = exam.getChronicConditions();
    List<String> codeList = chronicConditions.stream().map(disease -> disease.getCode()).toList();
    return codeList;
  }

  /** Bệnh mãn tính */
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
    List<String> codeList = chronicConditions.stream().map(disease -> disease.getCode()).toList();
    return codeList;
  }

  public ExamDTO injectChildObject(Exam entity) {
    Patient patient = patientRepository.findById(entity.getPatientId()).orElse(null);
    Dentist dentist = dentistRepository.findById(entity.getDentistId()).orElse(null);
    Organization organization =
        organizationRepository.findById(entity.getOrganizationId()).orElse(null);
    ExamDTO dto =
        new ExamDTO(
            entity.getId(),
            entity.getPatientId(),
            patient.getFullName(),
            entity.getDentistId(),
            dentist.getTitle(),
            entity.getOrganizationId(),
            organization.getName(),
            entity.getSchoolClass(),
            entity.getYear(),
            entity.getProfileNumber(),
            entity.getDate(),
            entity.getTeethRecordId(),
            entity.getPlaqueRecordId(),
            entity.getTartarRecordId(),
            entity.getTreatmentRecord());

    return dto;
  }

  public void delete(Long id) {
    examRepository.deleteById(id);
  }


  @Retryable(retryFor = CannotAcquireLockException.class, maxAttempts = 3, backoff = @Backoff(delay = 300))
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public TreatmentRecord updateTreatmentRecordByExamId(
      Long examId, TreatmentRecord treatmentRecord) {
    Exam exam = examRepository.findById(examId).orElseThrow(NoSuchElementException::new);

    exam.setTreatmentRecord(treatmentRecord);
    Exam saved = examRepository.save(exam);
    return saved.getTreatmentRecord();
  }

  public TreatmentRecord getTreatmentRecordByExamId(Long examId) {
    Exam exam = examRepository.getReferenceById(examId);
    return exam.getTreatmentRecord();
  }
}
