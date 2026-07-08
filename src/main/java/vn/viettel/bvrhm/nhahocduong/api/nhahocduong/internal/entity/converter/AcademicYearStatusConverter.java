package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.AcademicYearStatus;

@Converter(autoApply = true)
public class AcademicYearStatusConverter implements AttributeConverter<AcademicYearStatus, String> {

  @Override
  public String convertToDatabaseColumn(AcademicYearStatus status) {
    if (status == null) return null;
    return status.name();
  }

  @Override
  public AcademicYearStatus convertToEntityAttribute(String dbValue) {
    if (dbValue == null) return null;
    return Stream.of(AcademicYearStatus.values())
        .filter(s -> s.name().equalsIgnoreCase(dbValue))
        .findFirst()
        .orElse(null);
  }
}
