package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter;

import jakarta.persistence.AttributeConverter;
import java.util.stream.Stream;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.OrganizationType;

/**
 * @author: longlb1
 * @since: 14-Sep-23
 */
public class OrganizationTypeConverter implements AttributeConverter<OrganizationType, Integer> {
  @Override
  public Integer convertToDatabaseColumn(OrganizationType organizationType) {
    if (organizationType == null) {
      return null;
    }
    return organizationType.getCode();
  }

  @Override
  public OrganizationType convertToEntityAttribute(Integer code) {
    if (code == null) {
      return null;
    }
    return Stream.of(OrganizationType.values())
        .filter(type -> type.getCode().equals(code))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
