package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import java.util.List;
import org.mapstruct.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TreatmentRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TreatmentRecord;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ExamMapper.class})
public interface TreatmentRecordMapper {
  @Mapping(target = "exam", source = "examId")
  TreatmentRecord toEntity(TreatmentRecordDTO treatmentRecordDto);

  @Mapping(target = "examId", source = "exam.id")
  TreatmentRecordDTO toDto(TreatmentRecord treatmentRecord);

  List<TreatmentRecordDTO> toListDto(List<TreatmentRecord> treatmentRecords);

  List<TreatmentRecord> toListEntity(List<TreatmentRecordDTO> treatmentRecordDtos);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  TreatmentRecord partialUpdate(
      TreatmentRecordDTO treatmentRecordDto, @MappingTarget TreatmentRecord treatmentRecord);
}
