package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthorizationService;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthorizationService.AuthorizationData;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.service.AreaService;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.utils.ExcelUtil;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ImportPatientExcelColumn;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PatientDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.criteria.OrganizationSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.criteria.PatientSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Disease;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Ethnic;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.PatientMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.DiseaseRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.OrganizationService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.PatientService;
import vn.viettel.bvrhm.nhahocduong.api.user.internal.repository.UserRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class PatientServiceImpl implements PatientService {
  @Autowired
  AreaService areaService;
  @Autowired PatientRepository patientRepository;
  @Autowired
  OrganizationService organizationService;
  @Autowired PatientMapper patientMapper;
  @Autowired DiseaseRepository diseaseRepository;
  @PersistenceContext EntityManager entityManager;
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AuthorizationService authorizationService;

  public PatientDTO getPatientById(Long id) {
    Patient patient = patientRepository.findById(id).orElse(null);
    return patientMapper.toDto(patient);
  }

  @Transactional
  public PatientDTO createPatient(PatientDTO patientDTO) {
    // Check organization class and patient class
    OrganizationDTO organizationDTO = organizationService.getOrganizationById(patientDTO.organization().getId());
    List<String> schoolClassList = organizationDTO.getFlattenClassList();
    if (isNull(schoolClassList)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found any class of school with Code " + patientDTO.organization().getCode());
    }
    if (!schoolClassList.contains(patientDTO.schoolClass())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found class " + patientDTO.schoolClass() + " in school with Code " + patientDTO.organization().getCode());
    }

    var entity = patientMapper.toEntity(patientDTO);
    entity.setCode(generateCode(patientDTO));

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
    AuthorizationData authData = authorizationService.authorize();
    if (authData.getAreaCode() != null) {
      searchCriteria.setAreaCode(authData.getAreaCode());
    }

    if (searchCriteria.getAreaCode() != null) {
      if (areaService.getAreaByCode(searchCriteria.getAreaCode()) == null) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
      }
    }
    List<String> areaCodesInside = areaService.getChildrenAreaCode(searchCriteria.getAreaCode());
    
    Page<Patient> patients =
            patientRepository.findAllByCondition(searchCriteria.getSearchText(),
                                                 searchCriteria.getOrganizationName(),
                                                 authData.getOrganizationId(),
                                                 areaCodesInside,
                                                 searchCriteria.getSchoolClass(),
                                                 pageable);

    return patients.map(patientMapper::toDto);
  }

  public Page<PatientDTO> getAllPatients(Pageable pageable) {
    Page<Patient> patients = patientRepository.findAll(pageable);

    return patients.map(patientMapper::toDto);
  }

  public boolean deletePatientById(Long id) {
    Patient patient = patientRepository.findById(id).orElseThrow(NoSuchElementException::new);

    patient.setStatus(false);
    patientRepository.save(patient);

    return true;
  }

  @Override
  @Transactional
  public List<PatientDTO> importPatientsFromExcel(MultipartFile file) throws IOException {
    Sheet sheet = ExcelUtil.getSheetFromExcel(file.getInputStream(), 0);

    List<PatientDTO> patientDTOList = extractPatientDataFromSheet(sheet);
    return patientDTOList.stream().map(this::createPatient).toList();
  }

  @Override
  public byte[] generateExcelTemplateFile(HttpServletResponse response) throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    //    FileInputStream inputStream = new FileInputStream(new ClassPathResource("template/excel/Import_Hocsinh.xlsx").getFile());
    try (InputStream is = cl.getResourceAsStream("template/excel/Import_Hocsinh.xlsx")){
      List<OrganizationDTO> organizationDTOList = organizationService.search(new OrganizationSearchCriteria(), null).toList();
      XSSFWorkbook workbook = new XSSFWorkbook(is);
      XSSFSheet sheet = workbook.getSheetAt(1);


      for(int i = 0; i < organizationDTOList.size(); i++) {
        OrganizationDTO organizationData = organizationDTOList.get(i);
        // Create row
        XSSFRow row = sheet.createRow(i+1);

        // Insert data
        row.createCell(0).setCellValue(i+1);
        if (nonNull(organizationData.getAreaCode())) {
          row.createCell(1).setCellValue(organizationData.getAreaCode());
        }
        if(nonNull(organizationData.getAddress())) {
          row.createCell(2).setCellValue(organizationData.getAddress());
        }
        if(nonNull(organizationData.getCode())) {
          row.createCell(3).setCellValue(organizationData.getCode());
        }
        if(nonNull(organizationData.getName())) {
          row.createCell(4).setCellValue(organizationData.getName());
        }
        if(nonNull(organizationData.getFlattenClassList())) {
          row.createCell(5).setCellValue(String.join(",", organizationData.getFlattenClassList()));
        }
      }
      // Style sheet
      // Style normal rows
      XSSFCellStyle normalCellStyle = workbook.createCellStyle();
      normalCellStyle.setBorderTop(BorderStyle.THIN);
      normalCellStyle.setBorderBottom(BorderStyle.THIN);
      normalCellStyle.setBorderLeft(BorderStyle.THIN);
      normalCellStyle.setBorderRight(BorderStyle.THIN);
      normalCellStyle.setAlignment(HorizontalAlignment.CENTER);
      normalCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
      ExcelUtil.addStyleForCells(sheet, normalCellStyle, 0, 5, 1, organizationDTOList.size());
      ExcelUtil.autoSizeColumns(sheet, 6);

      response.setContentType(MediaType.APPLICATION_XML_VALUE);
      response.setHeader("Content-Disposition", "attachment; filename=Import_Hocsinh.xlsx");
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      workbook.close();
      return outputStream.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private List<PatientDTO> extractPatientDataFromSheet(Sheet sheet) {
    List<PatientDTO> patientDTOList = new ArrayList<>();
    for (Row row : sheet) {
      if (row.getRowNum() == 0) {
        // Ignore header
        continue;
      }

      Iterator<Cell> cellIterator = row.cellIterator();

      // Read cells and set value for object
      PatientDTO.PatientDTOBuilder patientDTOBuilder = PatientDTO.builder();
      while (cellIterator.hasNext()) {
        //Read cell
        Cell cell = cellIterator.next();
        String cellValue = null;
        if (nonNull(ExcelUtil.getCellValue(cell))) {
          cellValue = String.valueOf(ExcelUtil.getCellValue(cell));
        }

        // Indicate Column
        int columnIndex = cell.getColumnIndex();
        ImportPatientExcelColumn column = ImportPatientExcelColumn.getByIndex(columnIndex);
        if (Objects.isNull(column)) {
          continue;
        }

        // Check cell
        if (cellValue == null || cellValue.isEmpty()) {
          if (column.isRequired()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required field '" + column.getHeader() + "' of row " + row.getRowNum() + 1 + " is missing");
          }
          continue;
        }

        // Set value for object
        switch (column) {
          case FULL_NAME -> patientDTOBuilder.fullName(cellValue);
          case BIRTHDAY -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss z yyyy");
            LocalDate date = LocalDate.parse(cellValue, formatter);

//            LocalDate date = new Date(cellValue).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            patientDTOBuilder.birthDate(date);
          }
          case GENDER -> patientDTOBuilder.gender(Integer.parseInt(cellValue.replace(".0", "")));
          case CLASS -> patientDTOBuilder.schoolClass(cellValue);
          case SCHOOL_CODE -> {
            OrganizationDTO organizationDTO = organizationService.getOrganizationByCode(cellValue);
            if (isNull(organizationDTO)) {
              throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[Row " + row.getRowNum() + "]: Can't found organization with code " + cellValue);
            }

            patientDTOBuilder.organization(organizationDTO);
          }
          case AREA_TYPE -> patientDTOBuilder.areaType(cellValue);
          case NATIONAL_ID_NUMBER -> patientDTOBuilder.nationalIdNum(cellValue);
          case ETHNIC -> patientDTOBuilder.ethnic(Ethnic.getByDescription(cellValue));
          case HEALTH_INSURANCE_NUMBER -> patientDTOBuilder.healthInsuranceNumber(cellValue);
          case CARE_TAKER -> patientDTOBuilder.careTaker(cellValue);
        }
      }

      patientDTOList.add(patientDTOBuilder.build());
    }

    return patientDTOList;
  }

  private String generateCode(PatientDTO patientDTO) {
    // Get org code
    OrganizationDTO organization = organizationService.getOrganizationById(patientDTO.organization().getId());
    if (organization == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organization not found!");

    StringBuilder codeBuilder = new StringBuilder();
    codeBuilder.append(organization.getCode());

    // Get the latest patient code and increase 1, if not exist start with xxxyyy001
    Patient latestPatient = patientRepository.findFirstByOrganizationCodeOrderByCodeDesc(organization.getCode());
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
