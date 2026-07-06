package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.OrganizationSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;

@RepositoryRestResource
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
  Organization findByIdAndStatus(Long id, boolean status);

  List<Organization> findByName(String name);

  List<Organization> findByAddress(String address);

  List<Organization> findByAreaCode(String areaCode);

  List<Organization> findByNameAndAddress(String name, String address);

  List<Organization> findByNameAndAreaCode(String name, String areaCode);

  List<Organization> findByNameAndAddressAndAreaCode(String name, String address, String areaCode);

  @Query("SELECT o from Organization o where o.name is null or o.name like %:name%")
  List<Organization> findByNameIsLikeOrderByName(String name);

  List<Organization> findAllByOrderByName();

  List<Organization> findByAreaCodeIn(List<String> areaCodes);

  Organization findFirstByAreaCodeOrderByCodeDesc(String areaCode);

  @Query(
      """
    SELECT org
    FROM Organization org
    WHERE (:#{#areaCodes.size()} = 0 OR org.areaCode IN :areaCodes)
      AND (:#{#searchCriteria.getSearchText()} IS NULL OR (LOWER(org.code) LIKE LOWER(CONCAT('%', :#{#searchCriteria.getSearchText()}, '%'))
                                                          OR LOWER(org.name) LIKE LOWER(CONCAT('%', :#{#searchCriteria.getSearchText()}, '%'))))
      AND (:organizationId IS NULL OR org.id = :organizationId)
      AND (:organizationId IS NOT NULL OR ((:#{#searchCriteria.getType() eq NULL} = TRUE OR org.type = :#{#searchCriteria.getType()})))
      AND (:#{#searchCriteria.getStatus() eq NULL} = TRUE OR org.status = :#{#searchCriteria.getStatus()})
  """)
  Page<Organization> findByCriteria(
      List<String> areaCodes,
      OrganizationSearchCriteria searchCriteria,
      Long organizationId,
      Pageable pageable);

  Organization findByCode(String code);

  @Query(value = """
    SELECT o.id, o.name,
      COUNT(DISTINCT p.id) AS total_students,
      COUNT(DISTINCT e.id) AS examined,
      CASE WHEN COUNT(DISTINCT p.id) = 0 THEN 0
           ELSE ROUND(COUNT(DISTINCT e.id) * 1.0 / COUNT(DISTINCT p.id), 3) END AS rate
    FROM nhahocduong_organization o
    LEFT JOIN nhahocduong_patient p ON p.organization = o.id AND p.status = true
    LEFT JOIN nhahocduong_exam e ON e.patient_id = p.id AND e.status = true
    WHERE o.status = true
    GROUP BY o.id, o.name
    ORDER BY o.name
  """, nativeQuery = true)
  List<Object[]> findSchoolStatsRaw();
}
