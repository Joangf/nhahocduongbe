package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.mapper.ReferenceMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.excel.PatientExcelData;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PatientDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ReferenceMapper.class}
)
public interface PatientMapper {
  Patient toEntity(PatientDTO patientDTO);
  Patient toEntity(Long id);

  PatientDTO toDto(Patient patient);

  PatientDTO toDto(PatientExcelData patientExcelData);

  List<PatientDTO> toDtoList(List<Patient> patientList);

  List<Patient> toEntityList(List<PatientDTO> dtoList);

  //  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  //  Patient partialUpdate(
  //      PatientDTO patientDTO, @MappingTarget Patient patient);
}
