package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TreatmentRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.ExamMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.TreatmentRecordMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private DiseaseRepository diseaseRepository;
    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private TreatmentRecordMapper treatmentRecordMapper;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DentistRepository dentistRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private TreatmentRecordRepository treatmentRecordRepository;

    @Override
    public List<ExamDTO> getExamsByPatientId(Long patientId) {
        List<Exam> examList = examRepository.getExamsByPatientId(patientId);

        List<ExamDTO> examDTOList = new ArrayList<ExamDTO>();
        for (Exam exam : examList) {
            ExamDTO dto = injectChildObject(exam);
            examDTOList.add(dto);
        }

        return examDTOList;
    }

    @Override
    public ExamDTO getExamById(Long id) {
        Exam exam = examRepository.getReferenceById(id);
        if (exam == null) {
            return null;
        }
        //    ExamDTO dto = examMapper.toDto(exam);
        return injectChildObject(exam);
    }

    @Override
    public ExamDTO createExam(ExamDTO newExamDTO) {
        Exam newExam = examMapper.toEntity(newExamDTO);
        newExam.setId(null);
        Exam createdExam = examRepository.save(newExam);
        var createdExamDTO = examMapper.toDto(createdExam);
        return createdExamDTO;
    }


    @Override
    @Retryable(retryFor = CannotAcquireLockException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ExamDTO updateTeethRecordIdOfExamId(
            Long examId, Long teethRecordId) {
        Exam exam = examRepository.getReferenceById(examId);
        exam.setTeethRecordId(teethRecordId);
        var updated = examRepository.save(exam);
        return examMapper.toDto(updated);
    }

    @Override
    @Retryable(retryFor = CannotAcquireLockException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ExamDTO updatePlaqueRecordIdOfExam(Long examId, Long plaqueRecordId) {
        Exam exam = examRepository.findById(examId).orElseThrow(NoSuchElementException::new);
        exam.setPlaqueRecordId(plaqueRecordId);
        var updated = examRepository.save(exam);
        return examMapper.toDto(updated);
    }

    @Override
    @Retryable(retryFor = CannotAcquireLockException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
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

    @Override
    public List<String> getChronicDiseasesCodesByExamId(Long examId) {
        Exam exam = examRepository.getReferenceById(examId);

        List<Disease> chronicConditions = exam.getChronicConditions();
        List<String> codeList = chronicConditions.stream().map(disease -> disease.getCode()).toList();
        return codeList;
    }

    /**
     * Bệnh mãn tính
     */
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
        List<String> codeList = chronicConditions.stream().map(disease -> disease.getCode()).toList();
        return codeList;
    }

    @Override
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
                        treatmentRecordMapper.toListDto(entity.getTreatmentRecords())
                );

        return dto;
    }

    @Override
    public void delete(Long id) {
        examRepository.deleteById(id);
    }


    @Override
    @Retryable(retryFor = CannotAcquireLockException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<TreatmentRecordDTO> upsertTreatmentRecordsByExamId(
            Long examId, List<TreatmentRecordDTO> treatmentRecordDTOS) {
        Exam exam = examRepository.findById(examId).orElseThrow(NoSuchElementException::new);

        List<TreatmentRecord> treatmentRecords = treatmentRecordMapper.toListEntity(treatmentRecordDTOS);
        treatmentRecords.forEach(treatmentRecord -> treatmentRecord.setExam(exam));
        List<TreatmentRecord> savedTreatmentRecord = treatmentRecordRepository.saveAll(treatmentRecords);

        return treatmentRecordMapper.toListDto(savedTreatmentRecord);
    }

    @Override
    public List<TreatmentRecordDTO> getTreatmentRecordsByExamId(Long examId) {
        return treatmentRecordMapper.toListDto(treatmentRecordRepository.findByExamIdAndStatus(examId, true));
    }

    @Override
    public ExamDTO updateExam(ExamDTO examDTO) {
        Exam exam = examRepository.findExamByIdAndPatientId(examDTO.id(), examDTO.patientId());
        if (isNull(exam)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found exam for patient with ID " + examDTO.patientId());
        }

        examMapper.partialUpdate(examDTO, exam);
        return examMapper.toDto(examRepository.save(exam));
    }

    @Override
    public boolean deleteTreatmentRecord(Long examId, Long treatmentRecordId) {
        Exam exam = examRepository.findById(examId).orElse(null);
        if (isNull(exam)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found exam with ID " + examId);
        }

        TreatmentRecord treatmentRecord = exam.getTreatmentRecords().stream().filter(record -> record.getId().equals(treatmentRecordId)).findFirst().orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found treatment record ID " + treatmentRecordId + " in the exam with ID " + examId);
        });

        treatmentRecord.setStatus(false);
        treatmentRecordRepository.save(treatmentRecord);

        return true;
    }
}
