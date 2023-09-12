package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamPlace;

import java.util.stream.Stream;

@Converter
public class ExamPlaceJpaConverter implements AttributeConverter<ExamPlace, String> {
  @Override
  public String convertToDatabaseColumn(ExamPlace examPlace) {
    if (examPlace == null) {
      return null;
    }
    return examPlace.getCode();
  }

  @Override
  public ExamPlace convertToEntityAttribute(String code) {
    if (code == null) {
      return null;
    }
    return Stream.of(ExamPlace.values())
        .filter(pc -> pc.getCode().equals(code))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
