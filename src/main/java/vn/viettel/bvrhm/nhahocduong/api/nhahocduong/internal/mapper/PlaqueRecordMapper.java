package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import org.mapstruct.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PlaqueRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.PlaqueRecord;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlaqueRecordMapper {
  PlaqueRecord toEntity(PlaqueRecordDTO plaqueRecordDTO);

  PlaqueRecordDTO toDto(PlaqueRecord plaqueRecord);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  PlaqueRecord partialUpdate(
      PlaqueRecordDTO plaqueRecordDTO, @MappingTarget PlaqueRecord plaqueRecord);
}
