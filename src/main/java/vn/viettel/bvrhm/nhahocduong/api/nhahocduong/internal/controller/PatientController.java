package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.PatientSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PatientDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.PatientMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.PatientService;

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

  @GetMapping("/patient/search")
  public Page<PatientDTO> getPatientsByCondition(
      PatientSearchCriteria patientSearchCriteria, Pageable pageable) {
    return patientService.getPatientsByCondition(patientSearchCriteria, pageable);
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

  @DeleteMapping("/patient/{id}")
  public boolean deletePatientById(@PathVariable Long id) {
    return patientService.deletePatientById(id);
  }

  @GetMapping("/patient")
  public Page<PatientDTO> getPatientsAll(Pageable pageable) {
    return patientService.getAllPatients(pageable);
  }

  @PostMapping("/patient/excel")
  public List<PatientDTO> importPatientsFromExcel(@RequestParam MultipartFile file)
      throws IOException {
    return patientService.importPatientsFromExcel(file);
  }

  @GetMapping("/patient/excel")
  public byte[] exportPatients(HttpServletResponse response) throws IOException {
    return patientService.exportPatients(response);
  }

  @GetMapping("/patient/excel/template")
  public byte[] getTemplateFile(HttpServletResponse response) throws IOException {
    return patientService.generateExcelTemplateFile(response);
  }
}
