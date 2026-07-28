package vn.viettel.bvrhm.nhahocduong.api.common.internal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.common.internal.dto.AreaDTO;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.dto.AreaLookupDTO;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.Area;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.AreaType;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.Region;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.mapper.AreaMapper;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.repository.AreaRepository;

@DisplayName("AreaService — Unit Tests")
@ExtendWith(MockitoExtension.class)
class AreaServiceTest {

  @Mock AreaRepository areaRepository;
  @Mock AreaMapper areaMapper;
  @InjectMocks AreaService areaService;

  private Area createArea(String code, String name, int type) {
    Area area = new Area();
    area.setCode(code);
    area.setName(name);
    area.setType(type);
    return area;
  }

  private AreaDTO createAreaDto(String code, String name) {
    return new AreaDTO(null, code, name, null, null);
  }

  @Nested
  @DisplayName("getAllProvinces()")
  class GetAllProvinces {

    @Test
    @DisplayName("Trả về tất cả tỉnh/thành")
    void shouldReturnAllProvinces() {
      Area area1 = createArea("01", "Hà Nội", AreaType.PROVINCE.getCode());
      Area area2 = createArea("79", "TP.HCM", AreaType.PROVINCE.getCode());
      AreaDTO dto1 = createAreaDto("01", "Hà Nội");
      AreaDTO dto2 = createAreaDto("79", "TP.HCM");

      when(areaRepository.findAllByType(AreaType.PROVINCE.getCode())).thenReturn(List.of(area1, area2));
      when(areaMapper.toDto(List.of(area1, area2))).thenReturn(List.of(dto1, dto2));

      List<AreaDTO> result = areaService.getAllProvinces();

      assertThat(result).hasSize(2);
      assertThat(result.get(0).code()).isEqualTo("01");
      assertThat(result.get(1).code()).isEqualTo("79");
    }

    @Test
    @DisplayName("Trả về danh sách rỗng khi không có tỉnh nào")
    void shouldReturnEmptyWhenNoProvinces() {
      when(areaRepository.findAllByType(AreaType.PROVINCE.getCode())).thenReturn(List.of());
      when(areaMapper.toDto(List.of())).thenReturn(List.of());

      assertThat(areaService.getAllProvinces()).isEmpty();
    }
  }

  @Nested
  @DisplayName("getAllProvincesByCondition()")
  class GetAllProvincesByCondition {

    @Test
    @DisplayName("Khi region null — fallback về getAllProvinces()")
    void shouldFallbackWhenRegionNull() {
      AreaLookupDTO lookup = new AreaLookupDTO(null);
      Area area = createArea("01", "Hà Nội", AreaType.PROVINCE.getCode());
      AreaDTO dto = createAreaDto("01", "Hà Nội");

      when(areaRepository.findAllByType(AreaType.PROVINCE.getCode())).thenReturn(List.of(area));
      when(areaMapper.toDto(List.of(area))).thenReturn(List.of(dto));

      List<AreaDTO> result = areaService.getAllProvincesByCondition(lookup);

      assertThat(result).hasSize(1);
      verify(areaRepository).findAllByType(AreaType.PROVINCE.getCode());
      verify(areaRepository, never()).searchAllByCondition(anyInt(), anyString());
    }

    @Test
    @DisplayName("Khi có region — tìm kiếm theo điều kiện")
    void shouldSearchByCondition() {
      AreaLookupDTO lookup = new AreaLookupDTO(Region.NORTH);
      Area area = createArea("02", "Lào Cai", AreaType.PROVINCE.getCode());
      AreaDTO dto = createAreaDto("02", "Lào Cai");

      when(areaRepository.searchAllByCondition(48, "north")).thenReturn(List.of(area));
      when(areaMapper.toDto(List.of(area))).thenReturn(List.of(dto));

      List<AreaDTO> result = areaService.getAllProvincesByCondition(lookup);

      assertThat(result).hasSize(1);
      verify(areaRepository).searchAllByCondition(48, "north");
    }

    @Test
    @DisplayName("Khi có region nhưng không có kết quả")
    void shouldReturnEmptyWhenNoMatch() {
      AreaLookupDTO lookup = new AreaLookupDTO(Region.SOUTH);

      when(areaRepository.searchAllByCondition(48, "south")).thenReturn(List.of());
      when(areaMapper.toDto(List.of())).thenReturn(List.of());

      List<AreaDTO> result = areaService.getAllProvincesByCondition(lookup);

      assertThat(result).isEmpty();
    }
  }

  @Nested
  @DisplayName("getAllDistrictsOfProvince()")
  class GetAllDistricts {

    @Test
    @DisplayName("Trả về quận/huyện theo mã tỉnh")
    void shouldReturnDistricts() {
      Area area = createArea("001", "Ba Đình", AreaType.DISTRICT.getCode());
      AreaDTO dto = createAreaDto("001", "Ba Đình");

      when(areaRepository.listDistrictsByProvinceCode("01")).thenReturn(List.of(area));
      when(areaMapper.toDto(List.of(area))).thenReturn(List.of(dto));

      List<AreaDTO> result = areaService.getAllDistrictsOfProvince("01");

      assertThat(result).hasSize(1);
      assertThat(result.get(0).code()).isEqualTo("001");
    }

    @Test
    @DisplayName("Trả về rỗng khi tỉnh không có quận/huyện")
    void shouldReturnEmptyWhenNoDistricts() {
      when(areaRepository.listDistrictsByProvinceCode("99")).thenReturn(List.of());
      when(areaMapper.toDto(List.of())).thenReturn(List.of());

      assertThat(areaService.getAllDistrictsOfProvince("99")).isEmpty();
    }
  }

  @Nested
  @DisplayName("getAllPrecinctOfProvinceOfDistrict()")
  class GetPrecincts {

    @Test
    @DisplayName("Trả về phường/xã theo mã tỉnh và quận")
    void shouldReturnPrecincts() {
      Area area = createArea("00001", "Phúc Xá", AreaType.COMMUNE.getCode());
      AreaDTO dto = createAreaDto("00001", "Phúc Xá");

      when(areaRepository.listPrecinctByProvinceCodeAndDistrictCode("01", "001"))
          .thenReturn(List.of(area));
      when(areaMapper.toDto(List.of(area))).thenReturn(List.of(dto));

      List<AreaDTO> result = areaService.getAllPrecinctOfProvinceOfDistrict("01", "001");

      assertThat(result).hasSize(1);
      assertThat(result.get(0).code()).isEqualTo("00001");
    }
  }

  @Nested
  @DisplayName("getAreaByCode()")
  class GetAreaByCode {

    @Test
    @DisplayName("Trả về khu vực theo mã")
    void shouldReturnAreaByCode() {
      Area area = createArea("01", "Hà Nội", AreaType.PROVINCE.getCode());
      AreaDTO dto = createAreaDto("01", "Hà Nội");

      when(areaRepository.getByCode("01")).thenReturn(area);
      when(areaMapper.toDto(area)).thenReturn(dto);

      AreaDTO result = areaService.getAreaByCode("01");

      assertThat(result.code()).isEqualTo("01");
      assertThat(result.name()).isEqualTo("Hà Nội");
    }

    @Test
    @DisplayName("Trả về null khi không tìm thấy")
    void shouldReturnNullWhenNotFound() {
      when(areaRepository.getByCode("99")).thenReturn(null);
      when(areaMapper.toDto((Area) null)).thenReturn(null);

      AreaDTO result = areaService.getAreaByCode("99");

      assertThat(result).isNull();
    }
  }

  @Nested
  @DisplayName("getChildrenAreaCode()")
  class GetChildrenAreaCode {

    @Test
    @DisplayName("Trả về danh sách mã con")
    void shouldReturnChildrenCodes() {
      when(areaRepository.getChildrenAreaCodeByParentCode("01"))
          .thenReturn(List.of("001", "002", "003"));

      List<String> result = areaService.getChildrenAreaCode("01");

      assertThat(result).containsExactly("001", "002", "003");
    }

    @Test
    @DisplayName("Trả về rỗng khi không có mã con")
    void shouldReturnEmptyWhenNoChildren() {
      when(areaRepository.getChildrenAreaCodeByParentCode("99")).thenReturn(List.of());

      assertThat(areaService.getChildrenAreaCode("99")).isEmpty();
    }
  }
}
