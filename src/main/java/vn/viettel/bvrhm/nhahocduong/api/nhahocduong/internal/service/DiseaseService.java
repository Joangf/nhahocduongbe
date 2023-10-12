package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DiseaseDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Disease;

import java.util.List;

/**
 * @author: longlb1
 * @since: 29-Sep-23
 */
public interface DiseaseService {
  List<DiseaseDTO> getAllDiseases();
}
