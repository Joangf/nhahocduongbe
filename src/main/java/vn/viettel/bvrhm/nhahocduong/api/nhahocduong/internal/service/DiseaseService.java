package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DiseaseDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Disease;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.DiseaseMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.DiseaseRepository;

import java.util.List;

@Service
public class DiseaseService {
  @Autowired DiseaseRepository diseaseRepository;

  @Autowired DiseaseMapper diseaseMapper;

  public List<DiseaseDTO> getAllDiseases() {
    List<Disease> diseases = diseaseRepository.findAll();

    return diseaseMapper.toListDTO(diseases);
  }
}
