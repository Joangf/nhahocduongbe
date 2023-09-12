package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.bvrhm.nhahocduong.api.common.AreaService;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.TeethRecordDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.TeethRecordMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.TeethRecordRepository;

import java.util.List;
import java.util.Map;

@RestController
public class TestController {

  @Autowired TeethRecordRepository teethRecordRepository;
  @Autowired
  AreaService areaService;
  @Autowired TeethRecordMapper teethRecordMapper;

  @GetMapping("/test")
  public TeethRecord test() {
    ToothCondition tc1 = new ToothCondition();
    tc1.setProblem(ToothProblem.CARIES);
    tc1.setLocations(List.of(ToothSide.FAR, ToothSide.CHEW));
    tc1.setTreatment(ToothTreatment.ONE_SIDE_FILLING);

    ToothCondition tc2 = new ToothCondition();
    tc2.setProblem(ToothProblem.CARIES_FILLING);
    tc2.setLocations(List.of(ToothSide.INSIDE, ToothSide.CHEW));
    tc2.setTreatment(ToothTreatment.REMOVE);
    //    return Map.of(Tooth._61, tc1, Tooth._72, tc2);
    return new TeethRecord(9999L, Map.of(Tooth._61, tc1, Tooth._72, tc2));
  }

  @PostMapping("api/test")
  public void testPost(@RequestBody TeethRecordDTO teethRecordDTO) {
    System.out.println(
        ReflectionToStringBuilder.toString(teethRecordDTO, new RecursiveToStringStyle()));
    var entity = teethRecordMapper.toEntity(teethRecordDTO);
    teethRecordRepository.save(entity);
  }

  @GetMapping("/test/{id}")
  public TeethRecordDTO testGet(@PathVariable Long id) {
    //    var ret = teethRecordRepository.getReferenceById(id);
    var ret = teethRecordRepository.findById(id).orElse(null);
    var dto = teethRecordMapper.toDto(ret);
    return dto;
  }

  @GetMapping("/areatest/{code}")
  public List<String> getarealist(@PathVariable("code")String code) {
    return areaService.getChildrenAreaCode(code);
  }
}
