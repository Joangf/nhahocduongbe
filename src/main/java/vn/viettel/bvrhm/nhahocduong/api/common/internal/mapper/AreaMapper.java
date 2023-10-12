package vn.viettel.bvrhm.nhahocduong.api.common.internal.mapper;

import java.util.List;
import org.mapstruct.*;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.dto.AreaDTO;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.Area;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface AreaMapper {
  Area toEntity(AreaDTO areaDTO);

  List<Area> toEntity(List<AreaDTO> list);

  AreaDTO toDto(Area area);

  List<AreaDTO> toDto(List<Area> list);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Area partialUpdate(AreaDTO areaDTO, @MappingTarget Area area);
}
