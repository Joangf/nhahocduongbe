package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PatientDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.PatientMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.PatientService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PatientController {
  @Autowired PatientService patientService;
  @Autowired PatientRepository patientRepository;
  @Autowired PatientMapper patientMapper;

  @PostMapping("/patient")
  public PatientDTO createPatient(@RequestBody PatientDTO patientDTO) {
    PatientDTO createdDto = patientService.createPatient(patientDTO);
    return createdDto;
  }
  @GetMapping("/patient/all")
  public Page<PatientDTO> getPatientsByCondition(
          @RequestParam(required = false) String searchText,
          @RequestParam(required = false) String organizationName,
          @RequestParam(required = false) List<String> schoolClass, Pageable pageable) {
    return patientService.getPagePatientByCondition(searchText, organizationName, schoolClass, pageable);
  }

  @PutMapping("/patient/{id}")
  public PatientDTO updatePatient(@RequestBody PatientDTO patientDTO, @PathVariable Long id) {
    return patientService.updatePatient(patientDTO, id);
  }

  @GetMapping("/patient/{id}")
  public PatientDTO getPatientById(@PathVariable Long id) {
    PatientDTO dto = patientService.getPatientById(id);
    return dto;
  }

  @GetMapping("/patients/search/getByCondition")
  public List<PatientDTO> getPatientsByCondition(
      @RequestParam(name = "searchText",required = false) String searchText,
      @RequestParam(name = "organizationName",required = false) String organizationName,
      @RequestParam(name = "schoolClass",required = false) List<String> schoolClass) {
    return patientService.getPatientByCondition(searchText, organizationName, schoolClass);
  }

  @GetMapping("/patients")
  public Page<PatientDTO> getPatientsAll(Pageable pageable) {
    return patientService.getAllPatients(pageable);
  }
}
