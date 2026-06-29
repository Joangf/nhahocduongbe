package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import java.util.List;
import org.mapstruct.*;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.mapper.ReferenceMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamScheduleDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamSchedule;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ReferenceMapper.class}
)
public interface ExamScheduleMapper {
  @Mapping(source = "campaignId", target = "campaign.id")
  @Mapping(source = "organizationId", target = "organization.id")
  ExamSchedule toEntity(ExamScheduleDTO dto);

  @Mapping(source = "campaign.id", target = "campaignId")
  @Mapping(source = "organization.id", target = "organizationId")
  @Mapping(source = "organization.name", target = "organizationName")
  ExamScheduleDTO toDto(ExamSchedule entity);

  List<ExamScheduleDTO> toDtoList(List<ExamSchedule> entityList);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "campaignId", target = "campaign.id")
  @Mapping(source = "organizationId", target = "organization.id")
  void partialUpdate(ExamScheduleDTO dto, @MappingTarget ExamSchedule entity);
}
