package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.PlaqueRecord;

//// NOT YET NEEDED
@RepositoryRestResource
public interface PlaqueRecordRepository extends JpaRepository<PlaqueRecord, Long> {
  //    List<PlagueRecord> findBy_17_16nAnd_11nAnd_26_27nAnd_47_46tAnd_31nAnd_36_37t(String _17_16n,
  //                                                                                 String _11n,
  //                                                                                 String _26_27n,
  //                                                                                 String _47_46t,
  //                                                                                 String _31n,
  //                                                                                 String
  // _36_37t);
}
