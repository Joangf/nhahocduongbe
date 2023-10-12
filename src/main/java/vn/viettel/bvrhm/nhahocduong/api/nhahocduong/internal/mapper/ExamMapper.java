package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import java.util.List;
import org.mapstruct.*;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.mapper.ReferenceMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ReferenceMapper.class})
public interface ExamMapper {
  Exam toEntity(ExamDTO examDTO);

  Exam toEntity(Long id);

  public List<Exam> toEntityList(List<ExamDTO> examDTOList);

  ExamDTO toDto(Exam exam);

  public List<ExamDTO> toDtoList(List<Exam> examList);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Exam partialUpdate(ExamDTO examDTO, @MappingTarget Exam exam);
}
