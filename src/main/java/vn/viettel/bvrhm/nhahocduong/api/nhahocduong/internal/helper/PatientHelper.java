package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.helper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.utils.ExcelUtil;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.ResponseMessage;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Ethnic;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.excel.ExportPatientExcelColumn;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.excel.ImportPatientExcelColumn;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.excel.OrganizationCategoryExcelColumn;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.OrganizationSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.excel.PatientExcelData;
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

  @Autowired OrganizationHelper organizationHelper;

  public String generateCode(PatientDTO patientDTO) {
    // Get org code
    OrganizationDTO organization =
        organizationService.getOrganizationById(patientDTO.getOrganization().getId());
    if (organization == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organization not found!");

    StringBuilder codeBuilder = new StringBuilder();
    codeBuilder.append(organization.getCode());

    // Get the latest patient code and increase 1, if not exist start with xxxyyy001
    Patient latestPatient =
        patientRepository.findFirstByOrganizationCodeOrderByCodeDesc(organization.getCode());
    int patientOrderNumber;
    if (nonNull(latestPatient) && !"N/A".equals(latestPatient.getCode())) {
      Pattern codePattern =
          Pattern.compile("^" + Pattern.quote(organization.getCode()) + "(\\d+)");
      Matcher matcher = codePattern.matcher(latestPatient.getCode());
      if (!matcher.find()) {
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Invalid latest patient code: " + latestPatient.getCode());
      }
      patientOrderNumber = Integer.parseInt(matcher.group(1));
    } else {
      patientOrderNumber = 0;
    }
    codeBuilder.append(String.format("%03d", patientOrderNumber + 1));

    return codeBuilder.toString();
  }

  public void populateOrganizationCategorySheet(Workbook workbook) {
    Sheet schoolSheet = workbook.getSheetAt(1);
    List<OrganizationDTO> organizationDTOList =
        organizationService.search(new OrganizationSearchCriteria(), null).toList();

    for (int i = 0; i < organizationDTOList.size(); i++) {
      OrganizationDTO organizationData = organizationDTOList.get(i);
      // Create row
      Row row = schoolSheet.createRow(i + 1);

      // Insert data
      row.createCell(OrganizationCategoryExcelColumn.INDEX.getIndex()).setCellValue(i + 1);
      if (nonNull(organizationData.getAreaCode())) {
        row.createCell(OrganizationCategoryExcelColumn.AREA_CODE.getIndex())
            .setCellValue(organizationData.getAreaCode());
      }
      if (nonNull(organizationData.getAddress())) {
        row.createCell(OrganizationCategoryExcelColumn.AREA.getIndex())
            .setCellValue(organizationData.getAddress());
      }
      if (nonNull(organizationData.getCode())) {
        row.createCell(OrganizationCategoryExcelColumn.SCHOOL_CODE.getIndex())
            .setCellValue(organizationData.getCode());
      }
      if (nonNull(organizationData.getName())) {
        row.createCell(OrganizationCategoryExcelColumn.SCHOOL_NAME.getIndex())
            .setCellValue(organizationData.getName());
      }
      if (nonNull(organizationHelper.getFlattenClassList(organizationData))) {
        row.createCell(OrganizationCategoryExcelColumn.CLASS.getIndex())
            .setCellValue(
                String.join(",", organizationHelper.getFlattenClassList(organizationData)));
      }
    }
    // Style sheet
    // Style normal rows
    CellStyle normalCellStyle = workbook.createCellStyle();
    normalCellStyle.setBorderTop(BorderStyle.THIN);
    normalCellStyle.setBorderBottom(BorderStyle.THIN);
    normalCellStyle.setBorderLeft(BorderStyle.THIN);
    normalCellStyle.setBorderRight(BorderStyle.THIN);
    normalCellStyle.setAlignment(HorizontalAlignment.CENTER);
    normalCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    ExcelUtil.addStyleForCells(
        schoolSheet,
        normalCellStyle,
        0,
        OrganizationCategoryExcelColumn.getTotalColumn() - 1,
        1,
        organizationDTOList.size());
    ExcelUtil.autoSizeColumns(schoolSheet, OrganizationCategoryExcelColumn.getTotalColumn());
  }

  public List<PatientExcelData> extractPatientDataFromSheet(Sheet sheet) {
    List<PatientExcelData> patientsData = new ArrayList<>();
    for (Row row : sheet) {
      if (row.getRowNum() == 0) {
        // Ignore header
        continue;
      }

      boolean rowHasData = false;
      for (ImportPatientExcelColumn column : ImportPatientExcelColumn.values()) {
        Cell cell = row.getCell(column.getIndex(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        Object value = nonNull(cell) ? ExcelUtil.getCellValue(cell) : null;
        if (nonNull(value) && !String.valueOf(value).isBlank()) {
          rowHasData = true;
          break;
        }
      }
      if (!rowHasData) {
        continue;
      }

      // Read cells and set value for object
      PatientExcelData.PatientExcelDataBuilder patientExcelDataBuilder = PatientExcelData.builder();
      for (ImportPatientExcelColumn column : ImportPatientExcelColumn.values()) {
        Cell cell = row.getCell(column.getIndex(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        Object rawCellValue = nonNull(cell) ? ExcelUtil.getCellValue(cell) : null;
        String cellValue = null;
        if (nonNull(rawCellValue)) {
          cellValue = String.valueOf(rawCellValue);
        }

        // Check cell
        if (cellValue == null || cellValue.isBlank()) {
          if (column.isRequired()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Required field '"
                    + column.getHeader()
                    + "' of row "
                    + (row.getRowNum() + 1)
                    + " is missing");
          }
          continue;
        }

        // Set value for object based on column
        switch (column) {
          case INDEX -> {
            // Cột số thứ tự, bỏ qua không cần xử lý
          }
          case FULL_NAME -> patientExcelDataBuilder.fullName(cellValue);
          case BIRTHDAY -> {
            LocalDate date;
            if (rawCellValue instanceof Date javaDate) {
              // Cell dạng NUMERIC (Date) — Apache POI trả về java.util.Date
              date = javaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else {
              // Cell dạng STRING — parse theo nhiều định dạng phổ biến
              DateTimeFormatter formatter;
              if (cellValue.contains("/")) {
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
              } else {
                formatter = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
              }
              date = LocalDate.parse(cellValue, formatter);
            }
            patientExcelDataBuilder.birthDate(date);
          }
          case GENDER -> patientExcelDataBuilder.gender(
              Integer.parseInt(cellValue.split("\\.")[0]));
          case CLASS -> patientExcelDataBuilder.schoolClass(cellValue);
          case SCHOOL_CODE -> {
            OrganizationDTO organizationDTO = organizationService.getOrganizationByCode(cellValue);
            if (isNull(organizationDTO)) {
              throw new ResponseStatusException(
                  HttpStatus.BAD_REQUEST,
                  "[Row "
                      + row.getRowNum()
                      + "]: "
                      + ResponseMessage.ORGANIZATION_NOT_FOUND_WITH_CODE
                      + cellValue);
            }

            patientExcelDataBuilder.organization(organizationDTO);
          }
          case AREA_TYPE -> patientExcelDataBuilder.areaType(cellValue);
          case NATIONAL_ID_NUMBER -> patientExcelDataBuilder.nationalIdNum(cellValue);
          case ETHNIC -> patientExcelDataBuilder.ethnic(Ethnic.getByDescription(cellValue));
          case HEALTH_INSURANCE_NUMBER -> patientExcelDataBuilder.healthInsuranceNumber(cellValue);
          case CARE_TAKER -> patientExcelDataBuilder.careTaker(cellValue);
        }
      }

      patientsData.add(patientExcelDataBuilder.build());
    }

    return patientsData;
  }

  public void populatePatientsSheet(Workbook workbook) {
    Sheet patientSheet = workbook.getSheetAt(0);
    List<Patient> patients = patientRepository.findAllByStatus(true);

    for (int i = 0; i < patients.size(); i++) {
      Patient patient = patients.get(i);
      // Create row
      Row row = patientSheet.createRow(i + 1);

      // Insert data
      row.createCell(ExportPatientExcelColumn.INDEX.getIndex()).setCellValue(i + 1);
      if (nonNull(patient.getCode())) {
        row.createCell(ExportPatientExcelColumn.CODE.getIndex()).setCellValue(patient.getCode());
      }
      if (nonNull(patient.getFullName())) {
        row.createCell(ExportPatientExcelColumn.FULL_NAME.getIndex())
            .setCellValue(patient.getFullName());
      }
      if (nonNull(patient.getBirthDate())) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        row.createCell(ExportPatientExcelColumn.BIRTHDAY.getIndex())
            .setCellValue(patient.getBirthDate().format(formatter));
      }
      if (nonNull(patient.getGender())) {
        row.createCell(ExportPatientExcelColumn.GENDER.getIndex())
            .setCellValue(patient.getGender());
      }
      if (nonNull(patient.getOrganization()) && nonNull(patient.getOrganization().getCode())) {
        row.createCell(ExportPatientExcelColumn.SCHOOL_CODE.getIndex())
            .setCellValue(patient.getOrganization().getCode());
      }
      if (nonNull(patient.getSchoolClass())) {
        row.createCell(ExportPatientExcelColumn.CLASS.getIndex())
            .setCellValue(patient.getSchoolClass());
      }
      if (nonNull(patient.getAreaType())) {
        row.createCell(ExportPatientExcelColumn.AREA_TYPE.getIndex())
            .setCellValue(patient.getAreaType());
      }
      if (nonNull(patient.getNationalIdNum())) {
        row.createCell(ExportPatientExcelColumn.NATIONAL_ID_NUMBER.getIndex())
            .setCellValue(patient.getNationalIdNum());
      }
      if (nonNull(patient.getEthnic())) {
        row.createCell(ExportPatientExcelColumn.ETHNIC.getIndex())
            .setCellValue(patient.getEthnic().getDescription());
      }
      if (nonNull(patient.getHealthInsuranceNumber())) {
        row.createCell(ExportPatientExcelColumn.HEALTH_INSURANCE_NUMBER.getIndex())
            .setCellValue(patient.getHealthInsuranceNumber());
      }
      if (nonNull(patient.getCareTaker())) {
        row.createCell(ExportPatientExcelColumn.CARE_TAKER.getIndex())
            .setCellValue(patient.getCareTaker());
      }
    }
    // Style sheet
    // Style normal rows
    CellStyle normalCellStyle = workbook.createCellStyle();
    normalCellStyle.setBorderTop(BorderStyle.THIN);
    normalCellStyle.setBorderBottom(BorderStyle.THIN);
    normalCellStyle.setBorderLeft(BorderStyle.THIN);
    normalCellStyle.setBorderRight(BorderStyle.THIN);
    normalCellStyle.setAlignment(HorizontalAlignment.CENTER);
    normalCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    ExcelUtil.addStyleForCells(
        patientSheet,
        normalCellStyle,
        0,
        ExportPatientExcelColumn.getTotalColumn() - 1,
        1,
        patients.size());
    ExcelUtil.autoSizeColumns(patientSheet, ExportPatientExcelColumn.getTotalColumn());
  }
}
