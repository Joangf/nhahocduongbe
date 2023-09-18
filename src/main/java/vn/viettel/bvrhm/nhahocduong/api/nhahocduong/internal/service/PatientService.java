package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.bvrhm.nhahocduong.api.common.AreaService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PatientDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.search.PatientSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Disease;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.PatientMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.DiseaseRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class PatientService {

  @Autowired
  AreaService areaService;
  @Autowired PatientRepository patientRepository;
  @Autowired PatientMapper patientMapper;
  @Autowired DiseaseRepository diseaseRepository;
  @PersistenceContext EntityManager entityManager;

  public PatientDTO getPatientById(Long id) {
    Patient patient = patientRepository.findById(id).orElse(null);
    return patientMapper.toDto(patient);
  }

  @Transactional
  public PatientDTO createPatient(PatientDTO patientDTO) {
    var entity = patientMapper.toEntity(patientDTO);

    patientRepository.saveAndFlush(entity);
    entityManager.refresh(entity);

    return patientMapper.toDto(entity);
  }

  @Transactional
  public PatientDTO updatePatient(PatientDTO patientDTO, Long id) {
    var entity = patientMapper.toEntity(patientDTO);
    entity.setId(id);

    // TODO: optimize this
    List<Disease> chronicConditions = null;
    if (patientDTO.chronicConditions() != null && !patientDTO.chronicConditions().isEmpty()) {
      List<Long> updateChronicConditionIds =
          patientDTO.chronicConditions().stream().map(diseaseDTO -> diseaseDTO.id()).toList();
      chronicConditions = diseaseRepository.findAllById(updateChronicConditionIds);
    }
    entity.setChronicConditions(chronicConditions);

    patientRepository.save(entity);

    return patientMapper.toDto(entity);
  }

  public List<PatientDTO> getPatientByCondition(
      String searchText,
      String organizationName,
      List<String> schoolClass) {
    List<Patient> patients =
        patientRepository.findByCondition(searchText, organizationName, schoolClass);

    return patientMapper.toDtoList(patients);
  }

  public Page<PatientDTO> getPagePatientByCondition(
          PatientSearchCriteria searchCriteria, Pageable pageable) {

    if (searchCriteria.getAreaCode() != null) {
      if (areaService.getAreaByCode(searchCriteria.getAreaCode()) == null) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
      }
    }
    List<String> areaCodesInside = areaService.getChildrenAreaCode(searchCriteria.getAreaCode());
    
    Page<Patient> patients =
            patientRepository.findAllByCondition(searchCriteria.getSearchText(),
                                                 searchCriteria.getOrganizationName(),
                                                 areaCodesInside,
                                                 searchCriteria.getSchoolClass(),
                                                 pageable);

    return patients.map(patientMapper::toDto);
  }

  public Page<PatientDTO> getAllPatients(Pageable pageable) {
    Page<Patient> patients = patientRepository.findAll(pageable);

    return patients.map(patientMapper::toDto);
  }
}
