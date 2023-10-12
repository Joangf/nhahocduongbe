package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Grade;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.OrganizationType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO implements Serializable {
  private Long id;
  private String name;
  private String address;
  private String code;
  private String areaCode;
  private Map<Grade, List<String>> classes;
  private OrganizationType type = OrganizationType.SCHOOL;
  @JsonIgnore private Boolean status = true;
}
