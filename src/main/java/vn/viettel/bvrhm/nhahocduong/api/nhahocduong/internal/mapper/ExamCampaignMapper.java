package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import java.util.List;
import org.mapstruct.*;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.mapper.ReferenceMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamCampaignDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamCampaign;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ReferenceMapper.class}
)
public interface ExamCampaignMapper {
  ExamCampaign toEntity(ExamCampaignDTO dto);
  ExamCampaignDTO toDto(ExamCampaign entity);
  List<ExamCampaignDTO> toDtoList(List<ExamCampaign> entityList);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void partialUpdate(ExamCampaignDTO dto, @MappingTarget ExamCampaign entity);
}
