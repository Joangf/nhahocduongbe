package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PlaqueRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.PlaqueRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.PlaqueRecordMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PlaqueRecordRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.PlaqueRecordService;

@Service
public class PlaqueRecordServiceImpl implements PlaqueRecordService {
  @Autowired private PlaqueRecordRepository plagueRecordRepository;
  @Autowired private ExamRepository examRepository;
  @Autowired private PlaqueRecordMapper plaqueRecordMapper;

  @Override
  public PlaqueRecordDTO getPlaqueRecordByPatientIdAndExamId(Long patientId, Long examId) {
    Exam exam =
        examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(patientId, true).stream()
            .filter(e -> e.getId().equals(examId))
            .findFirst()
            .orElse(null);

    if (exam == null) {
      return null;
    }
    Long plaqueRecordId = exam.getPlaqueRecordId();
    if (plaqueRecordId == null) {
      return null;
    }
    PlaqueRecord plaqueRecord = plagueRecordRepository.getReferenceById(plaqueRecordId);
    PlaqueRecordDTO dto = plaqueRecordMapper.toDto(plaqueRecord);
    return dto;
  }

  @Override
  public PlaqueRecordDTO upsertPlaqueRecord(PlaqueRecordDTO plaqueRecordDTO) {
    var entity = plaqueRecordMapper.toEntity(plaqueRecordDTO);
    var savedEntity = plagueRecordRepository.save(entity);

    return plaqueRecordMapper.toDto(savedEntity);
  }
}
