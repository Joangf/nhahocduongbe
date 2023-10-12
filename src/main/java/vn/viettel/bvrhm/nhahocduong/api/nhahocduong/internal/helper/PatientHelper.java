package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.helper;

import static java.util.Objects.nonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PatientDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.OrganizationService;

/**
 * @author: longlb1
 * @since: 29-Sep-23
 */
@Component
public class PatientHelper {
  @Autowired OrganizationService organizationService;

  @Autowired PatientRepository patientRepository;

  public String generateCode(PatientDTO patientDTO) {
    // Get org code
    OrganizationDTO organization =
        organizationService.getOrganizationById(patientDTO.organization().getId());
    if (organization == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organization not found!");

    StringBuilder codeBuilder = new StringBuilder();
    codeBuilder.append(organization.getCode());

    // Get the latest patient code and increase 1, if not exist start with xxxyyy001
    Patient latestPatient =
        patientRepository.findFirstByOrganizationCodeOrderByCodeDesc(organization.getCode());
    int patientOrderNumber;
    if (nonNull(latestPatient) && !"N/A".equals(latestPatient.getCode())) {
      patientOrderNumber = Integer.parseInt(latestPatient.getCode().substring(6, 9));
    } else {
      patientOrderNumber = 0;
    }
    codeBuilder.append(String.format("%03d", patientOrderNumber + 1));

    return codeBuilder.toString();
  }
}
