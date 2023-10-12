package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Ethnic;

@Converter
public class EthnicJpaConverter implements AttributeConverter<Ethnic, String> {
  @Override
  public String convertToDatabaseColumn(Ethnic ethnic) {
    if (ethnic == null) {
      return null;
    }
    return ethnic.getDescription();
  }

  @Override
  public Ethnic convertToEntityAttribute(String description) {
    if (description == null) {
      return null;
    }
    return Stream.of(Ethnic.values())
        .filter(eth -> eth.getDescription().equals(description))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
