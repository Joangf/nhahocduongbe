package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TeethRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TeethRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.TeethRecordMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.TeethRecordRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.TeethRecordService;

import static java.util.Objects.isNull;

@Service
public class TeethRecordServiceImpl implements TeethRecordService {

  @Autowired TeethRecordRepository teethRecordRepository;
  @Autowired ExamRepository examRepository;
  @Autowired TeethRecordMapper teethRecordMapper;

  @Override
  public TeethRecordDTO getTeethRecordById(Long id) {
    TeethRecord teethRecord = teethRecordRepository.findById(id).orElse(null);
    return teethRecordMapper.toDto(teethRecord);
  }

  @Override
  public TeethRecordDTO getTeethRecordByPatientIdAndExamId(Long patientId, Long examId) {
    Exam exam =
        examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(patientId, true).stream()
            .filter(e -> e.getId().equals(examId))
            .findFirst()
            .orElse(null);

    if (exam == null) {
      return null;
    }
    TeethRecord teethRecord  = exam.getTeethRecord();
    if (isNull(teethRecord)) {
      return null;
    }
    return teethRecordMapper.toDto(teethRecord);
  }

  @Override
  public TeethRecordDTO upsertTeethRecord(TeethRecordDTO teethRecordDTO) {
    //    System.out.println(
    //        ReflectionToStringBuilder.toString(teethRecordDTO, new RecursiveToStringStyle()));
    var entity = teethRecordMapper.toEntity(teethRecordDTO);
    teethRecordRepository.save(entity);

    return teethRecordMapper.toDto(entity);
  }
}
