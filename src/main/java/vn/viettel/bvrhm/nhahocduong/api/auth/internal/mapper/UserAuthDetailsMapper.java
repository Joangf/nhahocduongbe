package vn.viettel.bvrhm.nhahocduong.api.auth.internal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.object.UserAuthDetails;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.dto.UserDTO;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserAuthDetailsMapper {

  @Mapping(source = "id", target = "userId")
  UserAuthDetails userAuthDetailsFromUserDTO(UserDTO userDTO);
}
