package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import org.mapstruct.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DiseaseDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Disease;

import java.util.List;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface DiseaseMapper {
  Disease toEntity(DiseaseDTO diseaseDTO);

  DiseaseDTO toDTO(Disease disease);

  List<DiseaseDTO> toListDTO(List<Disease> diseases);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Disease partialUpdate(DiseaseDTO diseaseDTO, @MappingTarget Disease disease);
}
