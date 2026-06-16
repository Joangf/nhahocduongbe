package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.util.List;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DiseaseDTO;

/**
 * @author: longlb1
 * @since: 29-Sep-23
 */
public interface DiseaseService {
  List<DiseaseDTO> getAllDiseases();
}
