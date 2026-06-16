package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.PatientSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PatientDTO;

/**
 * @author: longlb1
 * @since: 26-Sep-23
 */
public interface PatientService {
  PatientDTO getPatientById(Long id);

  PatientDTO createPatient(PatientDTO patientDTO);

  PatientDTO updatePatient(PatientDTO patientDTO, Long id);

  Page<PatientDTO> getPatientsByCondition(PatientSearchCriteria searchCriteria, Pageable pageable);

  Page<PatientDTO> getAllPatients(Pageable pageable);

  boolean deletePatientById(Long id);

  List<PatientDTO> importPatientsFromExcel(MultipartFile file) throws IOException;

  byte[] generateExcelTemplateFile(HttpServletResponse response) throws IOException;

  byte[] exportPatients(HttpServletResponse response) throws IOException;
}
