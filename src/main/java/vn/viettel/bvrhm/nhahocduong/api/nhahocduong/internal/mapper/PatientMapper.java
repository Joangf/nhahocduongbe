package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PatientDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface PatientMapper {
  Patient toEntity(PatientDTO patientDTO);

  PatientDTO toDto(Patient patient);

  List<PatientDTO> toDtoList(List<Patient> patientList);

  List<Patient> toEntityList(List<PatientDTO> dtoList);

  //  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  //  Patient partialUpdate(
  //      PatientDTO patientDTO, @MappingTarget Patient patient);
}
