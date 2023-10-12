package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;

@RepositoryRestResource
public interface PatientRepository extends JpaRepository<Patient, Long> {
  List<Patient> findByFullNameAndHealthInsuranceNumberAndGenderAndBirthDateAndEthnicAndAreaType(
      String fullName,
      String healthInsuranceNumber,
      Integer gender,
      LocalDate birthDate,
      String ethnic,
      String areaType);

  @Query(
      "SELECT DISTINCT p FROM Patient p "
          + "WHERE (:searchText is null or (LOWER(p.fullName) like LOWER(CONCAT('%', :searchText, '%')) "
          + "or LOWER(p.healthInsuranceNumber) like LOWER(CONCAT('%', :searchText, '%')))) "
          + "and (:organizationName is null or LOWER(p.organization.name) like LOWER(CONCAT('%', :organizationName, '%'))) "
          + "and (:#{null eq #schoolClass} = true or p.schoolClass in :schoolClass)")
  List<Patient> findByCondition(
      @RequestParam("searchText") String searchText,
      @RequestParam("organizationName") String organizationName,
      @RequestParam("schoolClass") List<String> schoolClass);

  @Query(
      "SELECT DISTINCT p FROM Patient p LEFT JOIN p.organization "
          + "WHERE (:searchText is null or (LOWER(p.fullName) like LOWER(CONCAT('%', :searchText, '%')) "
          + "or LOWER(p.healthInsuranceNumber) like LOWER(CONCAT('%', :searchText, '%')))) "
          + "and (:organizationId is null or p.organization.id = :organizationId) "
          + "and (:organizationId is not null or (:organizationName is null or LOWER(p.organization.name) like LOWER(CONCAT('%', :organizationName, '%'))))"
          + "and (:#{null eq #schoolClass} = true or LOWER(p.schoolClass) LIKE LOWER(CONCAT('%', :schoolClass, '%'))) "
          + "AND (:#{#areaCodes.size()} = 0 OR  p.organization.areaCode IN :areaCodes)")
  Page<Patient> findAllByCondition(
      @RequestParam("searchText") String searchText,
      @RequestParam("organizationName") String organizationName,
      @RequestParam("organizationId") Long organizationId,
      @RequestParam("areaCodes") List<String> areaCodes,
      @RequestParam("schoolClass") String schoolClass,
      Pageable pageable);

  List<Patient> findAllByOrganization_Id(Long id);

  List<Patient> findAllByOrganization_IdAndSchoolClassIn(Long id, List<String> schoolClass);

  Patient findFirstByOrganizationCodeOrderByCodeDesc(String organizationCode);
}
