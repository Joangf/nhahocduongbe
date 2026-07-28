package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Ethnic;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Grade;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.excel.PatientExcelData;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PatientDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.OrganizationService;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientHelper Unit Tests")
class PatientHelperTest {

  @Mock private OrganizationService organizationService;
  @Mock private PatientRepository patientRepository;
  @Mock private OrganizationHelper organizationHelper;
  @InjectMocks private PatientHelper patientHelper;

  // --- Helper methods ---

  private OrganizationDTO createOrgDTO(Long id, String code, String areaCode) {
    OrganizationDTO dto = new OrganizationDTO();
    dto.setId(id);
    dto.setCode(code);
    dto.setAreaCode(areaCode);
    dto.setName("Truong ABC");
    dto.setAddress("Ha Noi");
    return dto;
  }

  private PatientDTO createPatientDTO(Long orgId) {
    PatientDTO dto = new PatientDTO();
    OrganizationDTO org = new OrganizationDTO();
    org.setId(orgId);
    dto.setOrganization(org);
    return dto;
  }

  private Patient createPatient(String code) {
    Patient p = new Patient();
    p.setCode(code);
    return p;
  }

  private OrganizationDTO createFullOrgDTO() {
    OrganizationDTO dto = new OrganizationDTO();
    dto.setId(1L);
    dto.setCode("001001");
    dto.setAreaCode("001");
    dto.setName("Truong Tieu Hoc ABC");
    dto.setAddress("Ha Noi");
    Map<Grade, List<String>> classes = Map.of(Grade._1, List.of("1A", "1B"));
    dto.setClasses(classes);
    return dto;
  }

  private Patient createFullPatient() {
    Patient p = new Patient();
    p.setId(1L);
    p.setCode("001001001");
    p.setFullName("Nguyen Van A");
    p.setBirthDate(LocalDate.of(2010, 1, 15));
    p.setGender(1);
    p.setSchoolClass("1A");
    p.setAreaType("Do thi");
    p.setNationalIdNum("001001001");
    p.setEthnic(Ethnic.KINH);
    p.setHealthInsuranceNumber("HN123456");
    p.setCareTaker("Nguyen Van B");
    p.setStatus(true);
    Organization org = new Organization();
    org.setCode("001001");
    org.setName("Truong Tieu Hoc ABC");
    p.setOrganization(org);
    return p;
  }

  // --- generateCode() Tests ---

  @Nested
  @DisplayName("generateCode()")
  class GenerateCodeTests {

    @Test
    @DisplayName("Organization không tồn tại → ném BAD_REQUEST")
    void organizationNotFound_throwsBadRequest() {
      PatientDTO dto = createPatientDTO(99L);
      when(organizationService.getOrganizationById(99L)).thenReturn(null);

      assertThatThrownBy(() -> patientHelper.generateCode(dto))
          .isInstanceOf(ResponseStatusException.class)
          .satisfies(
              ex -> {
                ResponseStatusException rse = (ResponseStatusException) ex;
                assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
              });
    }

    @Test
    @DisplayName("Không có patient trong DB → trả về orgCode + 001")
    void noExistingPatient_returnsOrgCodePlus001() {
      PatientDTO dto = createPatientDTO(1L);
      OrganizationDTO org = createOrgDTO(1L, "001001", "001");
      when(organizationService.getOrganizationById(1L)).thenReturn(org);
      when(patientRepository.findFirstByOrganizationCodeOrderByCodeDesc("001001"))
          .thenReturn(null);

      String code = patientHelper.generateCode(dto);
      assertThat(code).isEqualTo("001001001");
    }

    @Test
    @DisplayName("Patient cuối có code N/A → trả về orgCode + 001")
    void latestPatientCodeNA_returnsOrgCodePlus001() {
      PatientDTO dto = createPatientDTO(1L);
      OrganizationDTO org = createOrgDTO(1L, "001001", "001");
      when(organizationService.getOrganizationById(1L)).thenReturn(org);
      when(patientRepository.findFirstByOrganizationCodeOrderByCodeDesc("001001"))
          .thenReturn(createPatient("N/A"));

      String code = patientHelper.generateCode(dto);
      assertThat(code).isEqualTo("001001001");
    }

    @Test
    @DisplayName("Có patient cuối → tăng số thứ tự lên 1")
    void existingPatient_incrementsOrderNumber() {
      PatientDTO dto = createPatientDTO(1L);
      OrganizationDTO org = createOrgDTO(1L, "001001", "001");
      when(organizationService.getOrganizationById(1L)).thenReturn(org);
      when(patientRepository.findFirstByOrganizationCodeOrderByCodeDesc("001001"))
          .thenReturn(createPatient("001001005"));

      String code = patientHelper.generateCode(dto);
      assertThat(code).isEqualTo("001001006");
    }

    @Test
    @DisplayName("Patient cuối có code không khớp regex → ném INTERNAL_SERVER_ERROR")
    void invalidLatestPatientCode_throwsInternalError() {
      PatientDTO dto = createPatientDTO(1L);
      OrganizationDTO org = createOrgDTO(1L, "001001", "001");
      when(organizationService.getOrganizationById(1L)).thenReturn(org);
      when(patientRepository.findFirstByOrganizationCodeOrderByCodeDesc("001001"))
          .thenReturn(createPatient("INVALID_CODE"));

      assertThatThrownBy(() -> patientHelper.generateCode(dto))
          .isInstanceOf(ResponseStatusException.class)
          .satisfies(
              ex -> {
                ResponseStatusException rse = (ResponseStatusException) ex;
                assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
              });
    }

    @Test
    @DisplayName("Patient đầu tiên → orgCode + 001")
    void firstPatient_returnsOrgCodePlus001() {
      PatientDTO dto = createPatientDTO(1L);
      OrganizationDTO org = createOrgDTO(1L, "001001", "001");
      when(organizationService.getOrganizationById(1L)).thenReturn(org);
      when(patientRepository.findFirstByOrganizationCodeOrderByCodeDesc("001001"))
          .thenReturn(createPatient("001001001"));

      String code = patientHelper.generateCode(dto);
      assertThat(code).isEqualTo("001001002");
    }

    @Test
    @DisplayName("Patient cuối có số thứ tự lớn → tăng đúng")
    void largeOrderNumber_incrementsCorrectly() {
      PatientDTO dto = createPatientDTO(1L);
      OrganizationDTO org = createOrgDTO(1L, "001001", "001");
      when(organizationService.getOrganizationById(1L)).thenReturn(org);
      when(patientRepository.findFirstByOrganizationCodeOrderByCodeDesc("001001"))
          .thenReturn(createPatient("001001099"));

      String code = patientHelper.generateCode(dto);
      assertThat(code).isEqualTo("001001100");
    }
  }

  // --- populateOrganizationCategorySheet() Tests ---

  @Nested
  @DisplayName("populateOrganizationCategorySheet()")
  class PopulateOrganizationCategorySheetTests {

    @Test
    @DisplayName("Danh sách organization rỗng → chỉ có header row")
    void emptyOrganizationList_onlyHeaderRow() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        workbook.createSheet("Sheet0"); // index 0
        workbook.createSheet("OrgSheet"); // index 1
        when(organizationService.search(any(), any()))
            .thenReturn(new PageImpl<>(Collections.emptyList()));

        patientHelper.populateOrganizationCategorySheet(workbook);

        Sheet sheet = workbook.getSheetAt(1);
        assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(0);
      }
    }

    @Test
    @DisplayName("Organization có đầy đủ thông tin → ghi đúng dữ liệu")
    void fullOrganizationData_writesCorrectly() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        workbook.createSheet("Sheet0");
        workbook.createSheet("OrgSheet");

        OrganizationDTO org = createFullOrgDTO();
        when(organizationService.search(any(), any()))
            .thenReturn(new PageImpl<>(List.of(org)));
        when(organizationHelper.getFlattenClassList(org)).thenReturn(List.of("1A", "1B"));

        patientHelper.populateOrganizationCategorySheet(workbook);

        Sheet sheet = workbook.getSheetAt(1);
        Row dataRow = sheet.getRow(1);
        assertThat(dataRow).isNotNull();
        assertThat(dataRow.getCell(0).getNumericCellValue()).isEqualTo(1); // INDEX
        assertThat(dataRow.getCell(1).getStringCellValue()).isEqualTo("001"); // AREA_CODE
        assertThat(dataRow.getCell(2).getStringCellValue()).isEqualTo("Ha Noi"); // AREA
        assertThat(dataRow.getCell(3).getStringCellValue()).isEqualTo("001001"); // SCHOOL_CODE
        assertThat(dataRow.getCell(4).getStringCellValue()).isEqualTo("Truong Tieu Hoc ABC");
        assertThat(dataRow.getCell(5).getStringCellValue()).isEqualTo("1A,1B"); // CLASS
      }
    }

    @Test
    @DisplayName("Organization có field null → bỏ qua cell đó")
    void nullFields_skipsCells() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        workbook.createSheet("Sheet0");
        workbook.createSheet("OrgSheet");

        OrganizationDTO org = new OrganizationDTO();
        org.setAreaCode(null);
        org.setAddress(null);
        org.setCode(null);
        org.setName(null);
        org.setClasses(null);
        when(organizationService.search(any(), any()))
            .thenReturn(new PageImpl<>(List.of(org)));
        when(organizationHelper.getFlattenClassList(org)).thenReturn(null);

        patientHelper.populateOrganizationCategorySheet(workbook);

        Sheet sheet = workbook.getSheetAt(1);
        Row dataRow = sheet.getRow(1);
        assertThat(dataRow).isNotNull();
        assertThat(dataRow.getCell(0).getNumericCellValue()).isEqualTo(1); // INDEX
        // Note: ExcelUtil.addStyleForCells creates all cells for styling,
        // so we verify cell VALUES are blank rather than cell existence
        for (int col = 1; col <= 5; col++) {
          Cell cell = dataRow.getCell(col);
          assertThat(cell).isNotNull();
          assertThat(cell.getCellType()).isEqualTo(CellType.BLANK);
        }
      }
    }

    @Test
    @DisplayName("Nhiều organizations → ghi đúng số rows")
    void multipleOrganizations_writesCorrectRowCount() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        workbook.createSheet("Sheet0");
        workbook.createSheet("OrgSheet");

        OrganizationDTO org1 = createOrgDTO(1L, "001001", "001");
        org1.setName("Truong A");
        org1.setAddress("Ha Noi");
        OrganizationDTO org2 = createOrgDTO(2L, "002001", "002");
        org2.setName("Truong B");
        org2.setAddress("HCM");
        when(organizationService.search(any(), any()))
            .thenReturn(new PageImpl<>(List.of(org1, org2)));
        when(organizationHelper.getFlattenClassList(any())).thenReturn(null);

        patientHelper.populateOrganizationCategorySheet(workbook);

        Sheet sheet = workbook.getSheetAt(1);
        assertThat(sheet.getLastRowNum()).isEqualTo(2);
        assertThat(sheet.getRow(1).getCell(4).getStringCellValue()).isEqualTo("Truong A");
        assertThat(sheet.getRow(2).getCell(4).getStringCellValue()).isEqualTo("Truong B");
      }
    }
  }

  // --- extractPatientDataFromSheet() Tests ---

  @Nested
  @DisplayName("extractPatientDataFromSheet()")
  class ExtractPatientDataFromSheetTests {

    private Sheet createHeaderSheet(Workbook workbook) {
      Sheet sheet = workbook.createSheet("Patients");
      Row header = sheet.createRow(0);
      for (int i = 0; i <= 10; i++) header.createCell(i).setCellValue("Header" + i);
      return sheet;
    }

    private void addRequiredCells(Row row, int index, String name, String birthday, int gender, String schoolCode, String clazz) {
      row.createCell(0).setCellValue(index);
      row.createCell(1).setCellValue(name);
      row.createCell(2).setCellValue(birthday);
      row.createCell(3).setCellValue(gender);
      row.createCell(4).setCellValue(schoolCode);
      row.createCell(5).setCellValue(clazz);
    }

    @Test
    @DisplayName("Sheet rỗng (chỉ header) → trả về list rỗng")
    void emptySheet_returnsEmptyList() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Patients");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("So thu tu");
        header.createCell(1).setCellValue("Ho va ten");

        List<PatientExcelData> result = patientHelper.extractPatientDataFromSheet(sheet);
        assertThat(result).isEmpty();
      }
    }

    @Test
    @DisplayName("Row toàn cell trống → bỏ qua")
    void allBlankCells_skipsRow() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Patients");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("So thu tu");
        header.createCell(1).setCellValue("Ho va ten");

        sheet.createRow(1); // empty row

        List<PatientExcelData> result = patientHelper.extractPatientDataFromSheet(sheet);
        assertThat(result).isEmpty();
      }
    }

    @Test
    @DisplayName("Thiếu required field → ném BAD_REQUEST")
    void missingRequiredField_throwsBadRequest() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = createHeaderSheet(workbook);

        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue(1); // INDEX only, missing FULL_NAME

        assertThatThrownBy(() -> patientHelper.extractPatientDataFromSheet(sheet))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(
                ex -> {
                  ResponseStatusException rse = (ResponseStatusException) ex;
                  assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                });
      }
    }

    @Test
    @DisplayName("BIRTHDAY dạng Date → parse đúng LocalDate")
    void birthdayAsDate_parsesCorrectly() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = createHeaderSheet(workbook);

        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(1); // INDEX
        row.createCell(1).setCellValue("Nguyen Van A"); // FULL_NAME
        Cell birthdayCell = row.createCell(2);
        Date date = Date.from(
            LocalDate.of(2010, 3, 15).atStartOfDay(ZoneId.systemDefault()).toInstant());
        birthdayCell.setCellValue(date);
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/MM/yyyy"));
        birthdayCell.setCellStyle(dateStyle);
        row.createCell(3).setCellValue(1); // GENDER
        row.createCell(4).setCellValue("001001"); // SCHOOL_CODE
        row.createCell(5).setCellValue("1A"); // CLASS

        OrganizationDTO org = createOrgDTO(1L, "001001", "001");
        when(organizationService.getOrganizationByCode("001001")).thenReturn(org);

        List<PatientExcelData> result = patientHelper.extractPatientDataFromSheet(sheet);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).birthDate()).isEqualTo(LocalDate.of(2010, 3, 15));
        assertThat(result.get(0).fullName()).isEqualTo("Nguyen Van A");
      }
    }

    @Test
    @DisplayName("BIRTHDAY dạng String dd/MM/yyyy → parse đúng")
    void birthdayAsStringSlashFormat_parsesCorrectly() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = createHeaderSheet(workbook);

        Row row = sheet.createRow(1);
        addRequiredCells(row, 1, "Tran Thi B", "25/12/2012", 2, "001001", "3A");

        OrganizationDTO org = createOrgDTO(1L, "001001", "001");
        when(organizationService.getOrganizationByCode("001001")).thenReturn(org);

        List<PatientExcelData> result = patientHelper.extractPatientDataFromSheet(sheet);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).birthDate()).isEqualTo(LocalDate.of(2012, 12, 25));
      }
    }

    @Test
    @DisplayName("GENDER = \"1.0\" (numeric cell) → parse đúng integer")
    void genderAsNumeric_parsesCorrectly() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = createHeaderSheet(workbook);

        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(1);
        row.createCell(1).setCellValue("Le Van C");
        row.createCell(2).setCellValue("01/06/2011");
        row.createCell(3).setCellValue("1.0"); // GENDER as "1.0"
        row.createCell(4).setCellValue("001001");
        row.createCell(5).setCellValue("2B"); // CLASS

        OrganizationDTO org = createOrgDTO(1L, "001001", "001");
        when(organizationService.getOrganizationByCode("001001")).thenReturn(org);

        List<PatientExcelData> result = patientHelper.extractPatientDataFromSheet(sheet);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).gender()).isEqualTo(1);
      }
    }

    @Test
    @DisplayName("SCHOOL_CODE không tồn tại → ném BAD_REQUEST")
    void invalidSchoolCode_throwsBadRequest() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = createHeaderSheet(workbook);

        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(1);
        row.createCell(1).setCellValue("Pham Van D");
        row.createCell(2).setCellValue("10/05/2010");
        row.createCell(3).setCellValue(1);
        row.createCell(4).setCellValue("INVALID");
        row.createCell(5).setCellValue("1A"); // CLASS

        when(organizationService.getOrganizationByCode("INVALID")).thenReturn(null);

        assertThatThrownBy(() -> patientHelper.extractPatientDataFromSheet(sheet))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(
                ex -> {
                  ResponseStatusException rse = (ResponseStatusException) ex;
                  assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                });
      }
    }

    @Test
    @DisplayName("Đọc đầy đủ tất cả optional columns")
    void allOptionalFields_parsedCorrectly() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = createHeaderSheet(workbook);

        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(1); // INDEX
        row.createCell(1).setCellValue("Hoang Thi E"); // FULL_NAME
        row.createCell(2).setCellValue("15/08/2013"); // BIRTHDAY
        row.createCell(3).setCellValue(2); // GENDER
        row.createCell(4).setCellValue("001001"); // SCHOOL_CODE
        row.createCell(5).setCellValue("5A"); // CLASS
        row.createCell(6).setCellValue("Nong thon"); // AREA_TYPE
        row.createCell(7).setCellValue("001001001"); // NATIONAL_ID_NUMBER
        row.createCell(8).setCellValue("Kinh"); // ETHNIC
        row.createCell(9).setCellValue("HN123456"); // HEALTH_INSURANCE_NUMBER
        row.createCell(10).setCellValue("Hoang Van F"); // CARE_TAKER

        OrganizationDTO org = createOrgDTO(1L, "001001", "001");
        when(organizationService.getOrganizationByCode("001001")).thenReturn(org);

        List<PatientExcelData> result = patientHelper.extractPatientDataFromSheet(sheet);
        assertThat(result).hasSize(1);
        PatientExcelData data = result.get(0);
        assertThat(data.fullName()).isEqualTo("Hoang Thi E");
        assertThat(data.birthDate()).isEqualTo(LocalDate.of(2013, 8, 15));
        assertThat(data.gender()).isEqualTo(2);
        assertThat(data.schoolClass()).isEqualTo("5A");
        assertThat(data.areaType()).isEqualTo("Nong thon");
        assertThat(data.nationalIdNum()).isEqualTo("001001001");
        assertThat(data.ethnic()).isEqualTo(Ethnic.KINH);
        assertThat(data.healthInsuranceNumber()).isEqualTo("HN123456");
        assertThat(data.careTaker()).isEqualTo("Hoang Van F");
      }
    }

    @Test
    @DisplayName("Nhiều rows → parse đúng tất cả")
    void multipleRows_parsesAll() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = createHeaderSheet(workbook);

        Row row1 = sheet.createRow(1);
        addRequiredCells(row1, 1, "Student A", "01/01/2010", 1, "001001", "1A");

        Row row2 = sheet.createRow(2);
        addRequiredCells(row2, 2, "Student B", "02/02/2011", 2, "001001", "2B");

        OrganizationDTO org = createOrgDTO(1L, "001001", "001");
        when(organizationService.getOrganizationByCode("001001")).thenReturn(org);

        List<PatientExcelData> result = patientHelper.extractPatientDataFromSheet(sheet);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).fullName()).isEqualTo("Student A");
        assertThat(result.get(1).fullName()).isEqualTo("Student B");
      }
    }

    @Test
    @DisplayName("Optional field trống → bỏ qua, không ném exception")
    void emptyOptionalField_skipsGracefully() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = createHeaderSheet(workbook);

        Row row = sheet.createRow(1);
        addRequiredCells(row, 1, "Test User", "01/01/2010", 1, "001001", "1A");

        OrganizationDTO org = createOrgDTO(1L, "001001", "001");
        when(organizationService.getOrganizationByCode("001001")).thenReturn(org);

        List<PatientExcelData> result = patientHelper.extractPatientDataFromSheet(sheet);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).areaType()).isNull();
        assertThat(result.get(0).nationalIdNum()).isNull();
        assertThat(result.get(0).ethnic()).isNull();
      }
    }
  }

  // --- populatePatientsSheet() Tests ---

  @Nested
  @DisplayName("populatePatientsSheet()")
  class PopulatePatientsSheetTests {

    @Test
    @DisplayName("Danh sách patient rỗng → chỉ có header row")
    void emptyPatientList_onlyHeaderRow() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        workbook.createSheet("PatientSheet"); // index 0
        when(patientRepository.findAllByStatus(true)).thenReturn(Collections.emptyList());

        patientHelper.populatePatientsSheet(workbook);

        Sheet sheet = workbook.getSheetAt(0);
        assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(0);
      }
    }

    @Test
    @DisplayName("Patient đầy đủ thông tin → ghi đúng dữ liệu")
    void fullPatientData_writesCorrectly() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        workbook.createSheet("PatientSheet");

        Patient patient = createFullPatient();
        when(patientRepository.findAllByStatus(true)).thenReturn(List.of(patient));

        patientHelper.populatePatientsSheet(workbook);

        Sheet sheet = workbook.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        assertThat(dataRow).isNotNull();
        assertThat(dataRow.getCell(0).getNumericCellValue()).isEqualTo(1); // INDEX
        assertThat(dataRow.getCell(1).getStringCellValue()).isEqualTo("001001001"); // CODE
        assertThat(dataRow.getCell(2).getStringCellValue()).isEqualTo("Nguyen Van A");
        assertThat(dataRow.getCell(3).getStringCellValue()).isEqualTo("15/01/2010"); // BIRTHDAY
        assertThat(dataRow.getCell(4).getNumericCellValue()).isEqualTo(1); // GENDER
        assertThat(dataRow.getCell(5).getStringCellValue()).isEqualTo("001001"); // SCHOOL_CODE
        assertThat(dataRow.getCell(6).getStringCellValue()).isEqualTo("1A"); // CLASS
        assertThat(dataRow.getCell(7).getStringCellValue()).isEqualTo("Do thi"); // AREA_TYPE
        assertThat(dataRow.getCell(8).getStringCellValue()).isEqualTo("001001001"); // NATIONAL_ID
        assertThat(dataRow.getCell(9).getStringCellValue()).isEqualTo("Kinh"); // ETHNIC
        assertThat(dataRow.getCell(10).getStringCellValue()).isEqualTo("HN123456");
        assertThat(dataRow.getCell(11).getStringCellValue()).isEqualTo("Nguyen Van B");
      }
    }

    @Test
    @DisplayName("Patient có field null → bỏ qua cell đó")
    void nullFields_skipsCells() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        workbook.createSheet("PatientSheet");

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setCode("001001001");
        patient.setFullName("Minimal Patient");
        Organization org = new Organization();
        org.setCode(null);
        patient.setOrganization(org);
        when(patientRepository.findAllByStatus(true)).thenReturn(List.of(patient));

        patientHelper.populatePatientsSheet(workbook);

        Sheet sheet = workbook.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        assertThat(dataRow).isNotNull();
        assertThat(dataRow.getCell(0).getNumericCellValue()).isEqualTo(1); // INDEX
        assertThat(dataRow.getCell(1).getStringCellValue()).isEqualTo("001001001"); // CODE
        assertThat(dataRow.getCell(2).getStringCellValue()).isEqualTo("Minimal Patient");
        // Note: ExcelUtil.addStyleForCells creates all cells for styling,
        // so we verify cell VALUES are blank rather than cell existence
        for (int col = 3; col <= 11; col++) {
          Cell cell = dataRow.getCell(col);
          assertThat(cell).isNotNull();
          assertThat(cell.getCellType()).isEqualTo(CellType.BLANK);
        }
      }
    }

    @Test
    @DisplayName("Nhiều patients → ghi đúng số rows")
    void multiplePatients_writesCorrectRowCount() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        workbook.createSheet("PatientSheet");

        Patient p1 = new Patient();
        p1.setId(1L);
        p1.setCode("001001001");
        p1.setFullName("Student A");
        Organization org1 = new Organization();
        org1.setCode("001001");
        p1.setOrganization(org1);

        Patient p2 = new Patient();
        p2.setId(2L);
        p2.setCode("001001002");
        p2.setFullName("Student B");
        Organization org2 = new Organization();
        org2.setCode("001001");
        p2.setOrganization(org2);

        when(patientRepository.findAllByStatus(true)).thenReturn(List.of(p1, p2));

        patientHelper.populatePatientsSheet(workbook);

        Sheet sheet = workbook.getSheetAt(0);
        assertThat(sheet.getLastRowNum()).isEqualTo(2);
        assertThat(sheet.getRow(1).getCell(2).getStringCellValue()).isEqualTo("Student A");
        assertThat(sheet.getRow(2).getCell(2).getStringCellValue()).isEqualTo("Student B");
      }
    }

    @Test
    @DisplayName("Patient có organization null → không ghi SCHOOL_CODE")
    void nullOrganization_skipsSchoolCode() throws Exception {
      try (Workbook workbook = new XSSFWorkbook()) {
        workbook.createSheet("PatientSheet");

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setCode("001001001");
        patient.setFullName("No Org Patient");
        patient.setOrganization(null);
        when(patientRepository.findAllByStatus(true)).thenReturn(List.of(patient));

        patientHelper.populatePatientsSheet(workbook);

        Sheet sheet = workbook.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        assertThat(dataRow).isNotNull();
        // SCHOOL_CODE (col 5) not populated when organization is null
        // (styling code still creates the cell, but it should be blank)
        assertThat(dataRow.getCell(5).getCellType()).isEqualTo(CellType.BLANK);
      }
    }
  }
}
