package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Medication;

@RepositoryRestResource
public interface MedicationRepository extends JpaRepository<Medication, Long> {
  Medication findByCode(String code);

  List<Medication> findByNameContaining(String name);

  List<Medication> findByUnit(String unit);

  List<Medication> findByNameContainingAndUnit(String name, String unit);
}
