package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.persistence.QueryHint;
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
      value = """
          SELECT DISTINCT p FROM Patient p
          LEFT JOIN FETCH p.organization o
          LEFT JOIN FETCH p.chronicConditions
          WHERE (:searchText IS NULL OR
                 LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchText, '%'))
              OR LOWER(p.healthInsuranceNumber) LIKE LOWER(CONCAT('%', :searchText, '%')))
            AND (:organizationId IS NULL OR o.id = :organizationId)
            AND (:organizationId IS NOT NULL OR :organizationName IS NULL
                 OR LOWER(o.name) LIKE LOWER(CONCAT('%', :organizationName, '%')))
            AND (:schoolClass IS NULL OR LOWER(p.schoolClass) LIKE LOWER(CONCAT('%', :schoolClass, '%')))
            AND (:#{#areaCodes == null || #areaCodes.isEmpty()} = TRUE OR o.areaCode IN :areaCodes)
            AND p.status = :status
          """,
      countQuery = """
          SELECT COUNT(DISTINCT p) FROM Patient p
          LEFT JOIN p.organization o
          WHERE (:searchText IS NULL OR
                 LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchText, '%'))
              OR LOWER(p.healthInsuranceNumber) LIKE LOWER(CONCAT('%', :searchText, '%')))
            AND (:organizationId IS NULL OR o.id = :organizationId)
            AND (:organizationId IS NOT NULL OR :organizationName IS NULL
                 OR LOWER(o.name) LIKE LOWER(CONCAT('%', :organizationName, '%')))
            AND (:schoolClass IS NULL OR LOWER(p.schoolClass) LIKE LOWER(CONCAT('%', :schoolClass, '%')))
            AND (:#{#areaCodes == null || #areaCodes.isEmpty()} = TRUE OR o.areaCode IN :areaCodes)
            AND p.status = :status
          """)
  @QueryHints(value = {
      @QueryHint(name = "org.hibernate.readOnly", value = "true"),
      @QueryHint(name = "jakarta.persistence.query.timeout", value = "10000")
  })
  Page<Patient> findAllByCondition(
      @RequestParam("searchText") String searchText,
      @RequestParam("organizationName") String organizationName,
      @RequestParam("organizationId") Long organizationId,
      @RequestParam("areaCodes") List<String> areaCodes,
      @RequestParam("schoolClass") String schoolClass,
      @RequestParam("status") Boolean status,
      Pageable pageable);

  List<Patient> findAllByStatus(boolean status);

  List<Patient> findAllByOrganization_Id(Long id);

  List<Patient> findAllByOrganization_IdAndSchoolClassIn(Long id, List<String> schoolClass);

  Patient findFirstByOrganizationCodeOrderByCodeDesc(String organizationCode);

  @Query(value = """
    SELECT DISTINCT p.id, p.full_name, p.code, p.school_class, p.phone_number,
      e.id AS exam_id, e.date AS exam_date, org.name AS exam_place,
      CASE WHEN e.id IS NOT NULL THEN 'EXAMINED' ELSE 'NOT_EXAMINED' END AS status
    FROM nhahocduong_patient p
    JOIN nhahocduong_exam_schedule s ON s.organization_id = p.organization AND s.school_class = p.school_class
    LEFT JOIN nhahocduong_exam e ON e.patient_id = p.id AND e.campaign_id = :campaignId
    LEFT JOIN nhahocduong_organization org ON e.organization_id = org.id
    WHERE s.campaign_id = :campaignId
      AND p.status = true
  """, nativeQuery = true)
  List<Object[]> findStudentExamStatusByCampaignId(@org.springframework.data.repository.query.Param("campaignId") Long campaignId);

  @Query(value = """
    SELECT p.id, p.full_name, p.code, p.school_class, p.phone_number,
      e.id AS exam_id, e.date AS exam_date, org.name AS exam_place,
      CASE WHEN e.id IS NOT NULL THEN 'EXAMINED' ELSE 'NOT_EXAMINED' END AS status
    FROM nhahocduong_patient p
    LEFT JOIN nhahocduong_exam e ON e.patient_id = p.id AND e.status = true
    LEFT JOIN nhahocduong_organization org ON e.organization_id = org.id
    WHERE p.organization = :schoolId AND p.status = true
    ORDER BY p.school_class, p.full_name
  """, nativeQuery = true)
  List<Object[]> findStudentsWithExamStatusBySchoolId(@org.springframework.data.repository.query.Param("schoolId") Long schoolId);
}
