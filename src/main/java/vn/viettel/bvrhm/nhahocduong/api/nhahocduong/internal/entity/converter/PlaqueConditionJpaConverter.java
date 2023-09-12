package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.PlaqueCondition;

import java.util.stream.Stream;

@Converter
public class PlaqueConditionJpaConverter implements AttributeConverter<PlaqueCondition, String> {
  @Override
  public String convertToDatabaseColumn(PlaqueCondition plaqueCondition) {
    if (plaqueCondition == null) {
      return null;
    }
    return plaqueCondition.getCode();
  }

  @Override
  public PlaqueCondition convertToEntityAttribute(String code) {
    if (code == null) {
      return null;
    }
    return Stream.of(PlaqueCondition.values())
        .filter(pc -> pc.getCode().equals(code))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
