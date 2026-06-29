package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.CampaignStatus;

@Converter(autoApply = true)
public class CampaignStatusConverter implements AttributeConverter<CampaignStatus, String> {
  @Override
  public String convertToDatabaseColumn(CampaignStatus status) {
    if (status == null) {
      return null;
    }
    return status.getDescription();
  }

  @Override
  public CampaignStatus convertToEntityAttribute(String description) {
    if (description == null) {
      return null;
    }
    return Stream.of(CampaignStatus.values())
        .filter(cs -> cs.getDescription().equalsIgnoreCase(description) || cs.name().equalsIgnoreCase(description))
        .findFirst()
        .orElse(null);
  }
}
