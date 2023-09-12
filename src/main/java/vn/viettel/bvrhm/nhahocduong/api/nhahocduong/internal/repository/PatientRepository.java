package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;

import java.time.LocalDate;
import java.util.List;

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
          + "WHERE (:searchText is null or (p.fullName like %:searchText% " +
              "or p.healthInsuranceNumber like %:searchText%)) " +
              "and (:organizationName is null or p.organization.name like %:organizationName%) " +
              "and (:#{null eq #schoolClass} = true or p.schoolClass in :schoolClass)"
  )
  List<Patient> findByCondition(
      @RequestParam("searchText") String searchText,
      @RequestParam("organizationName") String organizationName,
      @RequestParam("schoolClass") List<String> schoolClass);

  @Query(
          "SELECT DISTINCT p FROM Patient p "
                  + "WHERE (:searchText is null or (p.fullName like %:searchText% " +
                  "or p.healthInsuranceNumber like %:searchText%)) " +
                  "and (:organizationName is null or p.organization.name like %:organizationName%) " +
                  "and (:#{null eq #schoolClass} = true or p.schoolClass in :schoolClass)"
  )
  Page<Patient> findAllByCondition(
          @RequestParam("searchText") String searchText,
          @RequestParam("organizationName") String organizationName,
          @RequestParam("schoolClass") List<String> schoolClass,
          Pageable pageable);

  List<Patient> findAllByOrganization_Id(Long id);
}
