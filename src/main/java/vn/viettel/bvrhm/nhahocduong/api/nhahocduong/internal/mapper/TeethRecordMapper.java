package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TeethRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TeethRecord;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeethRecordMapper {
  TeethRecordDTO toDto(TeethRecord entity);

  TeethRecord toEntity(TeethRecordDTO dto);
}
