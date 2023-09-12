package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.PlaqueCondition;

public record PlaqueRecordDTO(
  Long id,
  @JsonProperty("17-16n")
  PlaqueCondition _17_16n,
  @JsonProperty("11n")
  PlaqueCondition _11n,
  @JsonProperty("26-27n")
  PlaqueCondition _26_27n,
  @JsonProperty("47-46t")
  PlaqueCondition _47_46t,
  @JsonProperty("31n")
  PlaqueCondition _31n,
  @JsonProperty("36-37t")
  PlaqueCondition _36_37t
) {}
