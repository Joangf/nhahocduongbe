package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import org.mapstruct.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Grade;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;

import java.util.List;
import java.util.Map;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrganizationMapper {
  Organization toEntity(OrganizationDTO organizationDTO);

  OrganizationDTO toDto(Organization organization);

  List<OrganizationDTO> toDtoList(List<Organization> patientList);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "classes", expression = "java(OrganizationMapper.overwriteClasses(organizationDTO, organization))")
  Organization partialUpdate(
      OrganizationDTO organizationDTO, @MappingTarget Organization organization);

  /*
    Overwrite default behavior of Mapper partialUpdate when perform JSONB property 'classes' of organization
    Default behavior: clear current map and put data of DTO map inside
    (won't change reference -> JPA won't detect updated entity is a dirty record -> not perform update)
   */
  static Map<Grade, List<String>> overwriteClasses(OrganizationDTO organizationDTO, Organization organization) {
      Map<Grade, List<String>> map = organizationDTO.getClasses();
      if ( map != null ) {
        return map;
      }
      return organization.getClasses();
  }
}
