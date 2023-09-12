package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TreatmentStatus;

@Converter
public class TreatmentStatusJpaConverter implements AttributeConverter<TreatmentStatus, String> {
  @Override
  public String convertToDatabaseColumn(TreatmentStatus treatmentSolution) {
    if (treatmentSolution == null) {
      return null;
    }
    return treatmentSolution.getDescription();
  }

  @Override
  public TreatmentStatus convertToEntityAttribute(String description) {
    if (description == null) {
      return null;
    }
    return Stream.of(TreatmentStatus.values())
        .filter(ts -> ts.getDescription().equals(description))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
