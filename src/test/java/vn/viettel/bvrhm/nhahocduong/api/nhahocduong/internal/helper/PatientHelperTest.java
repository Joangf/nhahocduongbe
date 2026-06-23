package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.excel.PatientExcelData;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PatientDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.OrganizationService;

class PatientHelperTest {

  @Test
  void generateCodeUsesOrganizationCodeLengthAndIgnoresLegacySuffix() {
    PatientHelper helper = new PatientHelper();
    helper.organizationService = mock(OrganizationService.class);
    helper.patientRepository = mock(PatientRepository.class);

    OrganizationDTO organization = new OrganizationDTO();
    organization.setId(10000L);
    organization.setCode("VL001");
    PatientDTO patient = new PatientDTO();
    patient.setOrganization(organization);

    Patient latestPatient = new Patient();
    latestPatient.setCode("VL001001AA");
    when(helper.organizationService.getOrganizationById(10000L)).thenReturn(organization);
    when(helper.patientRepository.findFirstByOrganizationCodeOrderByCodeDesc("VL001"))
        .thenReturn(latestPatient);

    assertThat(helper.generateCode(patient)).isEqualTo("VL001002");
  }

  @Test
  void extractPatientDataSkipsRowsThatOnlyContainFormattedBlankCells() throws Exception {
    PatientHelper helper = new PatientHelper();
    helper.organizationService = mock(OrganizationService.class);

    OrganizationDTO organization = new OrganizationDTO();
    organization.setCode("VL001");
    when(helper.organizationService.getOrganizationByCode("VL001")).thenReturn(organization);

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet();
      sheet.createRow(0);
      Row dataRow = sheet.createRow(1);
      dataRow.createCell(0).setCellValue(1);
      dataRow.createCell(1).setCellValue("Nguyen Van Mock");

      CellStyle dateStyle = workbook.createCellStyle();
      CreationHelper creationHelper = workbook.getCreationHelper();
      dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/MM/yyyy"));
      Date birthDate =
          Date.from(
              LocalDate.of(2015, 6, 18)
                  .atStartOfDay(ZoneId.systemDefault())
                  .toInstant());
      dataRow.createCell(2).setCellValue(birthDate);
      dataRow.getCell(2).setCellStyle(dateStyle);

      dataRow.createCell(3).setCellValue(1);
      dataRow.createCell(4).setCellValue("VL001");
      dataRow.createCell(5).setCellValue("1A");

      Row formattedBlankRow = sheet.createRow(2);
      formattedBlankRow.createCell(7);
      formattedBlankRow.createCell(9);

      List<PatientExcelData> result = helper.extractPatientDataFromSheet(sheet);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).fullName()).isEqualTo("Nguyen Van Mock");
      assertThat(result.get(0).birthDate()).isEqualTo(LocalDate.of(2015, 6, 18));
    }
  }
}
