package vn.viettel.bvrhm.nhahocduong.api.common.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.common.AreaDTO;
import vn.viettel.bvrhm.nhahocduong.api.common.AreaService;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.dto.AreaLookupDTO;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AreaController {

  @Autowired
  AreaService areaService;

  @GetMapping("areas/lookup")
  public List<AreaDTO> getProvinces(
      AreaLookupDTO lookupDTO
  ) {
    if (lookupDTO == null) return areaService.getAllProvinces();
    return areaService.getAllProvincesByCondition(lookupDTO);
  }

  @GetMapping("/areas/lookup/{code1}")
  public List<AreaDTO> getDistrictsByProvinceCode(@PathVariable("code1") String code1) {
    var areaList = areaService.getAllDistrictsOfProvince(code1);
    return areaList;
  }

  @GetMapping("/areas/lookup/{code1}/{code2}")
  public List<AreaDTO> getPrecinctsByProvinceCodeAndDistrictCode(
    @PathVariable("code1") String code1,
    @PathVariable("code2") String code2
  ) {
    var areaList = areaService.getAllPrecinctOfProvinceOfDistrict(code1, code2);
    return areaList;
  }

  @GetMapping("/areas/{code}")
  public AreaDTO getByCode(@PathVariable("code") String code) {
    var areaDTO = areaService.getAreaByCode(code);
    if (areaDTO == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return areaDTO;
  }
}
