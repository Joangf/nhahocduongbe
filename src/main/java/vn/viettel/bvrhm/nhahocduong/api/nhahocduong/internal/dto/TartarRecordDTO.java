package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TartarCondition;

public record TartarRecordDTO(
  Long id,
  @JsonProperty("17-16n")
  TartarCondition _17_16n,
  @JsonProperty("11n")
  TartarCondition _11n,
  @JsonProperty("26-27n")
  TartarCondition _26_27n,
  @JsonProperty("47-46t")
  TartarCondition _47_46t,
  @JsonProperty("31n")
  TartarCondition _31n,
  @JsonProperty("36-37t")
  TartarCondition _36_37t
) {}