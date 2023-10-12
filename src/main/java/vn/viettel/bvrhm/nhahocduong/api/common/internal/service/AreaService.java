package vn.viettel.bvrhm.nhahocduong.api.common.internal.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.dto.AreaDTO;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.dto.AreaLookupDTO;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.Area;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.AreaType;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.mapper.AreaMapper;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.repository.AreaRepository;

@Service
public class AreaService {

  @Autowired AreaRepository areaRepository;
  @Autowired AreaMapper areaMapper;

  public List<AreaDTO> getAllProvinces() {
    List<Area> provinceEntities = areaRepository.findAllByType(AreaType.PROVINCE.getCode());
    var dtoList = areaMapper.toDto(provinceEntities);
    return dtoList;
  }

  public List<AreaDTO> getAllProvincesByCondition(AreaLookupDTO lookupCondition) {
    if (lookupCondition.region() == null) return getAllProvinces();

    final int DANANG_CODE = 48;
    List<Area> provinces =
        areaRepository.searchAllByCondition(DANANG_CODE, lookupCondition.region().getCode());
    return areaMapper.toDto(provinces);
  }

  public List<AreaDTO> getAllDistrictsOfProvince(String provinceCode) {
    return areaMapper.toDto(areaRepository.listDistrictsByProvinceCode(provinceCode));
  }

  public List<AreaDTO> getAllPrecinctOfProvinceOfDistrict(
      String provinceCode, String districtCode) {
    return areaMapper.toDto(
        areaRepository.listPrecinctByProvinceCodeAndDistrictCode(provinceCode, districtCode));
  }

  public AreaDTO getAreaByCode(String code) {
    return areaMapper.toDto(areaRepository.getByCode(code));
  }

  public List<String> getChildrenAreaCode(String parentCode) {

    return areaRepository.getChildrenAreaCodeByParentCode(parentCode);
  }
}
