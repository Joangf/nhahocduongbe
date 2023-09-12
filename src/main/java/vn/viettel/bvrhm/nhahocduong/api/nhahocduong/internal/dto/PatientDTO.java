package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Ethnic;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record PatientDTO(
        Long id,
        String fullName,
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
        String careTaker
) implements Serializable {}