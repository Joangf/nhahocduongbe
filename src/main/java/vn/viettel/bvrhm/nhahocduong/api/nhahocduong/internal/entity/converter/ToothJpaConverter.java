package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Tooth;

@Converter
public class ToothJpaConverter implements AttributeConverter<Tooth, String> {
  @Override
  public String convertToDatabaseColumn(Tooth tooth) {
    if (tooth == null) {
      return null;
    }
    return tooth.getCode();
  }

  @Override
  public Tooth convertToEntityAttribute(String code) {
    if (code == null) {
      return null;
    }
    return Stream.of(Tooth.values())
        .filter(ts -> ts.getCode().equals(code))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
