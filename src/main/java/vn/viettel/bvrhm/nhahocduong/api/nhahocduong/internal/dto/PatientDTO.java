package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Ethnic;

@Data
public class PatientDTO implements Serializable {
  private Long id;
  private String fullName;
  private String code;
  private String healthInsuranceNumber;
  private Integer gender;
  private LocalDate birthDate;
  private Ethnic ethnic;
  private String areaType;
  private String phoneNumber;
  private String addressLine;
  private List<DiseaseDTO> chronicConditions;
  private OrganizationDTO organization;
  private String schoolClass;
  private String nationalIdNum;
  private String careTaker;
  @JsonIgnore private Boolean status = true;
}
