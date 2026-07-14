package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.ResponseMessage;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.PatientSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.excel.PatientExcelData;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DiseaseDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.PatientDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Disease;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.helper.OrganizationHelper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.helper.PatientHelper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.PatientMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.DiseaseRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.OrganizationService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.PatientService;

@Service
public class PatientServiceImpl implements PatientService {
  @Autowired AreaService areaService;
  @Autowired PatientRepository patientRepository;
  @Autowired OrganizationService organizationService;
  @Autowired PatientMapper patientMapper;
  @Autowired DiseaseRepository diseaseRepository;
  @PersistenceContext EntityManager entityManager;

  @Autowired private AuthorizationService authorizationService;

  @Autowired private OrganizationHelper organizationHelper;

  @Autowired private PatientHelper patientHelper;
  @Autowired private ExamRepository examRepository;

  @Override
  public PatientDTO getPatientById(Long id) {
    Patient patient = patientRepository.findById(id).orElse(null);
    return patientMapper.toDto(patient);
  }

  @Override
  @Transactional
  @CacheEvict(value = {"patients", "dashboardStats"}, allEntries = true)
  public PatientDTO createPatient(PatientDTO patientDTO) {
    // Check organization class and patient class
    OrganizationDTO organizationDTO =
        organizationService.getOrganizationById(patientDTO.getOrganization().getId());
    List<String> schoolClassList = organizationHelper.getFlattenClassList(organizationDTO);
    if (isNull(schoolClassList)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          ResponseMessage.ORGANIZATION_CANT_FOUND_CLASS_OF_SCHOOL
              + patientDTO.getOrganization().getCode());
    }
    if (!schoolClassList.contains(patientDTO.getSchoolClass())) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          ResponseMessage.ORGANIZATION_CANT_FOUND_CLASS + patientDTO.getSchoolClass());
    }

    var entity = patientMapper.toEntity(patientDTO);
    entity.setCode(patientHelper.generateCode(patientDTO));

    patientRepository.saveAndFlush(entity);
    entityManager.refresh(entity);

    return patientMapper.toDto(entity);
  }

  @Override
  @Transactional
  @CacheEvict(value = {"patients", "dashboardStats"}, allEntries = true)
  public PatientDTO updatePatient(PatientDTO patientDTO, Long id) {
    var entity = patientMapper.toEntity(patientDTO);
    entity.setId(id);

    // TODO: optimize this
    List<Disease> chronicConditions = null;
    if (patientDTO.getChronicConditions() != null && !patientDTO.getChronicConditions().isEmpty()) {
      List<Long> updateChronicConditionIds =
          patientDTO.getChronicConditions().stream().map(DiseaseDTO::id).toList();
      chronicConditions = diseaseRepository.findAllById(updateChronicConditionIds);
    }
    entity.setChronicConditions(chronicConditions);

    patientRepository.save(entity);

    return patientMapper.toDto(entity);
  }

  @Override
  @Cacheable(
      value = "patients",
      key = "#searchCriteria.searchText + '|'"
          + " + #searchCriteria.organizationName + '|'"
          + " + #searchCriteria.schoolClass + '|'"
          + " + #searchCriteria.areaCode + '|'"
          + " + #pageable.pageNumber + '_'"
          + " + #pageable.pageSize + '_'"
          + " + #pageable.sort"
  )
  public Page<PatientDTO> getPatientsByCondition(
      PatientSearchCriteria searchCriteria, Pageable pageable) {
    AuthorizationData authData = authorizationService.authorize();
    if (nonNull(authData.getAreaCode())) {
      searchCriteria.setAreaCode(authData.getAreaCode());
    }

    if (searchCriteria.getAreaCode() != null) {
      if (areaService.getAreaByCode(searchCriteria.getAreaCode()) == null) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
      }
    }
    // Skip expensive recursive CTE when no area filter — pass empty list to bypass IN clause
    List<String> areaCodesInside = Collections.emptyList();
    if (searchCriteria.getAreaCode() != null) {
      List<String> children = areaService.getChildrenAreaCode(searchCriteria.getAreaCode());
      if (children != null) {
        areaCodesInside = children;
      }
    }

    Page<Patient> patients =
        patientRepository.findAllByCondition(
            searchCriteria.getSearchText(),
            searchCriteria.getOrganizationName(),
            authData.getOrganizationId(),
            areaCodesInside,
            searchCriteria.getSchoolClass(),
            searchCriteria.isStatus(),
            pageable);

    return patients.map(patientMapper::toDto);
  }

  @Override
  public Page<PatientDTO> getAllPatients(Pageable pageable) {
    Page<Patient> patients = patientRepository.findAll(pageable);
    return patients.map(patientMapper::toDto);
  }

  @Override
  @CacheEvict(value = {"patients", "dashboardStats"}, allEntries = true)
  public boolean deletePatientById(Long id) {
    Patient patient = patientRepository.findById(id).orElseThrow(NoSuchElementException::new);
    List<Exam> exams = examRepository.getExamsByPatientIdAndStatusOrderByIdDesc(id, true);

    if (nonNull(exams) && !exams.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, ResponseMessage.PATIENT_CANT_DELETE_HAS_EXAMS);
    }

    patient.setStatus(false);
    patientRepository.save(patient);
    return true;
  }

  @Override
  @Transactional
  public List<PatientDTO> importPatientsFromExcel(MultipartFile file) throws IOException {
    String fileName = file.getOriginalFilename();
    if (file.isEmpty() || fileName == null || !fileName.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Only non-empty Excel .xlsx files are supported");
    }

    final Sheet sheet;
    try {
      sheet = ExcelUtil.getSheetFromExcel(file.getInputStream(), 0);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "The uploaded file is not a valid Excel .xlsx file", e);
    }

    List<PatientExcelData> patientExcelData = patientHelper.extractPatientDataFromSheet(sheet);
    return patientExcelData.stream().map(patientMapper::toDto).map(this::createPatient).toList();
  }

  @Override
  public byte[] generateExcelTemplateFile(HttpServletResponse response) throws IOException {
    final String fileName = "Import_Hocsinh.xlsx";
    final String filePath = "template/excel/";
    ClassLoader cl = this.getClass().getClassLoader();
    try (InputStream is = cl.getResourceAsStream(filePath + fileName)) {
      XSSFWorkbook workbook = new XSSFWorkbook(is);
      patientHelper.populateOrganizationCategorySheet(workbook);

      // Setup response header and write file's data
      response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      workbook.close();
      return outputStream.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public byte[] exportPatients(HttpServletResponse response) throws IOException {
    final String fileName = "Export_Hocsinh.xlsx";
    final String filePath = "template/excel/";
    ClassLoader cl = this.getClass().getClassLoader();
    try (InputStream is = cl.getResourceAsStream(filePath + fileName)) {
      XSSFWorkbook workbook = new XSSFWorkbook(is);
      patientHelper.populateOrganizationCategorySheet(workbook);
      patientHelper.populatePatientsSheet(workbook);

      // Setup response header and write file's data
      response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      workbook.close();
      return outputStream.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
