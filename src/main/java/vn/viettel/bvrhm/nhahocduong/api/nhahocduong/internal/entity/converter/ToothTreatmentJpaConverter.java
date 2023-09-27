package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ToothTreatment;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TreatmentStatus;

import java.util.stream.Stream;

@Converter
public class ToothTreatmentJpaConverter implements AttributeConverter<ToothTreatment, String> {
  @Override
  public String convertToDatabaseColumn(ToothTreatment toothTreatment) {
    if (toothTreatment == null) {
      return null;
    }
    return toothTreatment.getCode();
  }

  @Override
  public ToothTreatment convertToEntityAttribute(String code) {
    if (code == null) {
      return null;
    }
    return Stream.of(ToothTreatment.values())
        .filter(ts -> ts.getCode().equals(code))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
