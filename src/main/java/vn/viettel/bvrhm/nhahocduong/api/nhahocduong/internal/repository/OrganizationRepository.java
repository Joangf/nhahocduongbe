package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.criteria.OrganizationSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;

import java.util.List;

@RepositoryRestResource
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
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

  @Query("""
    SELECT org
    FROM Organization org
    WHERE (:#{#areaCodes.size()} = 0 OR org.areaCode IN :areaCodes)
      AND (:#{#searchCriteria.getSearchText()} IS NULL OR (org.code LIKE %:#{#searchCriteria.getSearchText()}%
                                                          OR org.name LIKE %:#{#searchCriteria.getSearchText()}%))
      AND (:organizationId IS NULL OR org.id = :organizationId)
      AND (:organizationId IS NOT NULL OR ((:#{#searchCriteria.getType() eq NULL} = TRUE OR org.type = :#{#searchCriteria.getType()} )))
  """)
  Page<Organization> findByCriteria(List<String> areaCodes,
                                    OrganizationSearchCriteria searchCriteria,
                                    Long organizationId,
                                    Pageable pageable);

  Organization findFirstByAreaCodeOrderByCodeDesc(String areaCode);
}
