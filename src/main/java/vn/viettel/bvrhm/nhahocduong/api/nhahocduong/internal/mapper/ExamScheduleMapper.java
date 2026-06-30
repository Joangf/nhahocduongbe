package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.mapper.ReferenceMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamScheduleDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Dentist;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamSchedule;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ReferenceMapper.class}
)
public interface ExamScheduleMapper {
  @Mapping(source = "campaignId", target = "campaign.id")
  @Mapping(source = "organizationId", target = "organization.id")
  @Mapping(target = "dentists", ignore = true)
  ExamSchedule toEntity(ExamScheduleDTO dto);

  @Mapping(source = "campaign.id", target = "campaignId")
  @Mapping(source = "organization.id", target = "organizationId")
  @Mapping(source = "organization.name", target = "organizationName")
  @Mapping(source = "dentists", target = "dentistIds", qualifiedByName = "dentistsToIds")
  @Mapping(source = "dentists", target = "dentistNames", qualifiedByName = "dentistsToNames")
  ExamScheduleDTO toDto(ExamSchedule entity);

  List<ExamScheduleDTO> toDtoList(List<ExamSchedule> entityList);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "campaignId", target = "campaign.id")
  @Mapping(source = "organizationId", target = "organization.id")
  @Mapping(target = "dentists", ignore = true)
  void partialUpdate(ExamScheduleDTO dto, @MappingTarget ExamSchedule entity);

  @Named("dentistsToIds")
  default List<Long> dentistsToIds(Set<Dentist> dentists) {
    if (dentists == null) return List.of();
    return dentists.stream().map(Dentist::getId).collect(Collectors.toList());
  }

  @Named("dentistsToNames")
  default List<String> dentistsToNames(Set<Dentist> dentists) {
    if (dentists == null) return List.of();
    return dentists.stream().map(Dentist::getTitle).collect(Collectors.toList());
  }
}
