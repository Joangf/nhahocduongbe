package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TeethRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TeethRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.TeethRecordMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.TeethRecordRepository;

@Service
public class TeethRecordService {

  @Autowired TeethRecordRepository teethRecordRepository;
  @Autowired ExamRepository examRepository;
  @Autowired TeethRecordMapper teethRecordMapper;

  public TeethRecordDTO getTeethRecordById(Long id) {
    TeethRecord teethRecord = teethRecordRepository.findById(id).orElse(null);
    return teethRecordMapper.toDto(teethRecord);
  }

  public TeethRecordDTO getTeethRecordByPatientIdAndExamId(Long patientId, Long examId) {
    Exam exam =
        examRepository.getExamsByPatientId(patientId).stream()
            .filter(e -> e.getId().equals(examId))
            .findFirst()
            .orElse(null);

    if (exam == null) {
      return null;
    }
    Long teethRecordId = exam.getTeethRecordId();
    if (teethRecordId == null) {
      return null;
    }
    TeethRecord teethRecord = teethRecordRepository.getReferenceById(teethRecordId);
    TeethRecordDTO dto = teethRecordMapper.toDto(teethRecord);
    return dto;
  }

  public TeethRecordDTO upsertTeethRecord(TeethRecordDTO teethRecordDTO) {
//    System.out.println(
//        ReflectionToStringBuilder.toString(teethRecordDTO, new RecursiveToStringStyle()));
    var entity = teethRecordMapper.toEntity(teethRecordDTO);
    teethRecordRepository.save(entity);

    return teethRecordMapper.toDto(entity);
  }
}
