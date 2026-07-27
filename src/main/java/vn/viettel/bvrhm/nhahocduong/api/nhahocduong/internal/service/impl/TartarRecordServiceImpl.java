package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TartarRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TartarRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.TartarRecordMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.TartarRecordRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.TartarRecordService;

import static java.util.Objects.isNull;

@Service
@Transactional(readOnly = true)
public class TartarRecordServiceImpl implements TartarRecordService {
  @Autowired private TartarRecordRepository tartarRecordRepository;
  @Autowired private ExamRepository examRepository;
  @Autowired private TartarRecordMapper tartarRecordMapper;

  @Override
  public TartarRecordDTO getTartarRecordByPatientIdAndExamId(Long patientId, Long examId) {
    Exam exam =
        examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(patientId, true).stream()
            .filter(e -> e.getId().equals(examId))
            .findFirst()
            .orElse(null);

    if (exam == null) {
      return null;
    }

    TartarRecord tartarRecord = exam.getTartarRecord();
    if (isNull(tartarRecord)) {
      return null;
    }
    return tartarRecordMapper.toDto(tartarRecord);
  }

  @Override
  @Transactional
  public TartarRecordDTO upsertTartarRecord(TartarRecordDTO tartarRecordDTO) {
    var entity = tartarRecordMapper.toEntity(tartarRecordDTO);
    var savedEntity = tartarRecordRepository.save(entity);
    var savedDto = tartarRecordMapper.toDto(savedEntity);

    return savedDto;
  }
}
