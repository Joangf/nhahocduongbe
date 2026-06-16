package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.TartarCondition;

@Converter
public class TartarConditionJpaConverter implements AttributeConverter<TartarCondition, String> {

  @Override
  public String convertToDatabaseColumn(TartarCondition tartarCondition) {
    if (tartarCondition == null) {
      return null;
    }
    return tartarCondition.getCode();
  }

  @Override
  public TartarCondition convertToEntityAttribute(String code) {
    if (code == null) {
      return null;
    }
    return Stream.of(TartarCondition.values())
        .filter(tc -> tc.getCode().equals(code))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
