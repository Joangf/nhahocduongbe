package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import org.mapstruct.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;

import java.util.List;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExamMapper {
  Exam toEntity(ExamDTO examDTO);

  public List<Exam> toEntityList(List<ExamDTO> examDTOList);

  ExamDTO toDto(Exam exam);

  public List<ExamDTO> toDtoList(List<Exam> examList);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Exam partialUpdate(ExamDTO examDTO, @MappingTarget Exam exam);
}
