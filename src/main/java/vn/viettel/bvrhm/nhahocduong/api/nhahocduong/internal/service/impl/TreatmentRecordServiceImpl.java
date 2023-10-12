package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import static java.util.Objects.isNull;

import java.util.List;
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
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TreatmentRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.TreatmentRecordMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.TreatmentRecordRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.ExamService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.TreatmentRecordService;

/**
 * @author: longlb1
 * @since: 29-Sep-23
 */
@Service
public class TreatmentRecordServiceImpl implements TreatmentRecordService {
  @Autowired ExamService examService;

  @Autowired TreatmentRecordRepository treatmentRecordRepository;

  @Autowired TreatmentRecordMapper treatmentRecordMapper;

  @Override
  @Retryable(
      retryFor = CannotAcquireLockException.class,
      maxAttempts = 5,
      backoff = @Backoff(delay = 300))
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public List<TreatmentRecordDTO> upsertTreatmentRecordsByExamId(
      Long examId, List<TreatmentRecordDTO> treatmentRecordDTOS) {
    ExamDTO examDTO = examService.getExamByIdAndStatus(examId, true);
    treatmentRecordDTOS.forEach(treatmentRecordDTO -> treatmentRecordDTO.setExamId(examId));
    List<TreatmentRecord> treatmentRecords =
        treatmentRecordMapper.toListEntity(treatmentRecordDTOS);

    // Remove treatment record that not in upsert list
    List<Long> idOfRecordsNotIncluded =
        examDTO.getTreatmentRecords().stream()
            .map(TreatmentRecordDTO::getId)
            .filter(
                id ->
                    treatmentRecords.stream()
                        .anyMatch(upsertRecord -> !id.equals(upsertRecord.getId())))
            .toList();
    List<TreatmentRecord> treatmentRecordsNotIncluded =
        treatmentRecordRepository.findByIdIsIn(idOfRecordsNotIncluded);
    treatmentRecordsNotIncluded.forEach(record -> record.setStatus(false));
    treatmentRecordRepository.saveAll(treatmentRecordsNotIncluded);

    // Upsert records
    List<TreatmentRecord> savedTreatmentRecord =
        treatmentRecordRepository.saveAll(treatmentRecords);

    return treatmentRecordMapper.toListDto(savedTreatmentRecord);
  }

  @Override
  public List<TreatmentRecordDTO> getTreatmentRecordsByExamId(Long examId) {
    return treatmentRecordMapper.toListDto(
        treatmentRecordRepository.findByExamIdAndStatus(examId, true));
  }

  @Override
  public boolean deleteTreatmentRecord(Long examId, Long treatmentRecordId) {
    ExamDTO examDTO = examService.getExamByIdAndStatus(examId, true);
    if (isNull(examDTO)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found exam with ID " + examId);
    }

    examDTO.getTreatmentRecords().stream()
        .filter(record -> record.getId().equals(treatmentRecordId))
        .findFirst()
        .orElseThrow(
            () -> {
              throw new ResponseStatusException(
                  HttpStatus.NOT_FOUND,
                  "Not found treatment record ID "
                      + treatmentRecordId
                      + " in the exam with ID "
                      + examId);
            });

    TreatmentRecord treatmentRecord = treatmentRecordRepository.getReferenceById(treatmentRecordId);
    treatmentRecord.setStatus(false);
    treatmentRecordRepository.save(treatmentRecord);

    return true;
  }
}
