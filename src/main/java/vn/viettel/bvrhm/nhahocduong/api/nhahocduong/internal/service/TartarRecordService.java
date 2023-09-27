package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PlaqueRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TartarRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TartarRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.TartarRecordMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.TartarRecordRepository;

@Service
public class TartarRecordService {
  @Autowired private TartarRecordRepository tartarRecordRepository;
  @Autowired private ExamRepository examRepository;
  @Autowired private TartarRecordMapper tartarRecordMapper;

  public TartarRecordDTO getTartarRecordByPatientIdAndExamId(Long patientId, Long examId) {
    Exam exam =
        examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(patientId, true).stream()
            .filter(e -> e.getId().equals(examId))
            .findFirst()
            .orElse(null);

    if (exam == null) {
      return null;
    }
    Long tartarRecordId = exam.getTartarRecordId();
    if (tartarRecordId == null) {
      return null;
    }
    TartarRecord tartarRecord = tartarRecordRepository.getReferenceById(tartarRecordId);
    TartarRecordDTO dto = tartarRecordMapper.toDto(tartarRecord);
    return dto;
  }

  public TartarRecordDTO upsertTartarRecord(TartarRecordDTO tartarRecordDTO) {
    var entity = tartarRecordMapper.toEntity(tartarRecordDTO);
    var savedEntity = tartarRecordRepository.save(entity);
    var savedDto = tartarRecordMapper.toDto(savedEntity);

    return savedDto;
  }
}
