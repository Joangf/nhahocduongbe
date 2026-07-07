package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.AffiliationStatus;

@Converter(autoApply = true)
public class AffiliationStatusConverter implements AttributeConverter<AffiliationStatus, String> {

  @Override
  public String convertToDatabaseColumn(AffiliationStatus status) {
    if (status == null) return null;
    return status.name();
  }

  @Override
  public AffiliationStatus convertToEntityAttribute(String dbValue) {
    if (dbValue == null) return null;
    return Stream.of(AffiliationStatus.values())
        .filter(s -> s.name().equalsIgnoreCase(dbValue))
        .findFirst()
        .orElse(null);
  }
}
