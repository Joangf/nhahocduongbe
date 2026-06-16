package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import org.mapstruct.*;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.mapper.ReferenceMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DentistDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Dentist;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ReferenceMapper.class}
)
public interface DentistMapper {
  Dentist toEntity(DentistDTO dentistDTO);
  Dentist toEntity(Long id);

  DentistDTO toDto(Dentist dentist);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Dentist partialUpdate(DentistDTO dentistDTO, @MappingTarget Dentist dentist);
}
