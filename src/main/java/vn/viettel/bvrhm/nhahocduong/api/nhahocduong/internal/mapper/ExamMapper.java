package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import java.util.List;
import org.mapstruct.*;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.mapper.ReferenceMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {
      ReferenceMapper.class,
      OrganizationMapper.class,
      PatientMapper.class,
      DentistMapper.class,
      ExamCampaignMapper.class
    })
public interface ExamMapper {
  @Mappings({
    @Mapping(target = "organization", source = "organizationId"),
    @Mapping(target = "patient", source = "patientId"),
    @Mapping(target = "dentist", source = "dentistId"),
    @Mapping(target = "campaign", source = "campaignId")
  })
  Exam toEntity(ExamDTO examDTO);

  Exam toEntity(Long id);

  List<Exam> toEntityList(List<ExamDTO> examDTOList);

  @Mappings({
    @Mapping(target = "patientId", source = "patient.id"),
    @Mapping(target = "patientName", source = "patient.fullName"),
    @Mapping(target = "dentistId", source = "dentist.id"),
    @Mapping(target = "dentistName", source = "dentist.title"),
    @Mapping(target = "organizationId", source = "organization.id"),
    @Mapping(target = "organizationName", source = "organization.name"),
    @Mapping(target = "campaignId", source = "campaign.id")
  })
  ExamDTO toDto(Exam exam);

  List<ExamDTO> toDtoList(List<Exam> examList);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Exam partialUpdate(ExamDTO examDTO, @MappingTarget Exam exam);
}
