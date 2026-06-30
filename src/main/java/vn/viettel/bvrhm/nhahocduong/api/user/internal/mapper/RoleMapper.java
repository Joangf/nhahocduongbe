package vn.viettel.bvrhm.nhahocduong.api.user.internal.mapper;

import java.util.List;
import org.mapstruct.*;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.RoleDTO;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.entity.Role;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
  Role toEntity(RoleDTO roleDTO);

  RoleDTO toDto(Role role);

  List<RoleDTO> toDtoList(List<Role> roleList);

  List<RoleDTO> toListDto(List<Role> roles);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Role partialUpdate(RoleDTO roleDTO, @MappingTarget Role role);
}
