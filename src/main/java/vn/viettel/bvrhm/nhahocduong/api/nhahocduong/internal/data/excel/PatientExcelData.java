package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.excel;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Ethnic;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;

/**
 * @author: longlb1
 * @since: 12-Oct-23
 */
@Builder(toBuilder = true)
public record PatientExcelData(
    String fullName,
    String code,
    String healthInsuranceNumber,
    Integer gender,
    LocalDate birthDate,
    Ethnic ethnic,
    String areaType,
    String phoneNumber,
    String addressLine,
    OrganizationDTO organization,
    String schoolClass,
    String nationalIdNum,
    String careTaker)
    implements Serializable {}
