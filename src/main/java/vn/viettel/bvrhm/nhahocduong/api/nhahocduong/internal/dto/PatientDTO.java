package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Ethnic;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
public record PatientDTO(
        Long id,
        String fullName,
        String code,
        String healthInsuranceNumber,
        Integer gender,
        LocalDate birthDate,
        Ethnic ethnic,
        String areaType,
        String phoneNumber,
        String addressLine,
        List<DiseaseDTO> chronicConditions,
        OrganizationDTO organization,
        String schoolClass,
        String nationalIdNum,
        String careTaker,
        @JsonIgnore
        Boolean status
) implements Serializable {}