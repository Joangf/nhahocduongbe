package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DiseaseDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Disease;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.DiseaseMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.DiseaseRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.DiseaseService;

@Service
@Transactional(readOnly = true)
public class DiseaseServiceImpl implements DiseaseService {
  @Autowired DiseaseRepository diseaseRepository;

  @Autowired DiseaseMapper diseaseMapper;

  @Autowired
  public List<DiseaseDTO> getAllDiseases() {
    List<Disease> diseases = diseaseRepository.findAll();

    return diseaseMapper.toListDTO(diseases);
  }
}
