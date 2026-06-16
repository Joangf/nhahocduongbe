package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import org.mapstruct.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TartarRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TartarRecord;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface TartarRecordMapper {
  TartarRecord toEntity(TartarRecordDTO tartarRecordDTO);

  TartarRecordDTO toDto(TartarRecord tartarRecord);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  TartarRecord partialUpdate(
      TartarRecordDTO tartarRecordDTO, @MappingTarget TartarRecord tartarRecord);
}
