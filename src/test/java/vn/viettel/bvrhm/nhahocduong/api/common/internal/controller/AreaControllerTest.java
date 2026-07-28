package vn.viettel.bvrhm.nhahocduong.api.common.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import vn.viettel.bvrhm.nhahocduong.api.common.internal.dto.AreaDTO;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.dto.AreaLookupDTO;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.service.AreaService;

@DisplayName("AreaController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class AreaControllerTest {

  @Mock AreaService areaService;
  @InjectMocks AreaController controller;

  @Nested
  @DisplayName("GET /api/areas/lookup")
  class GetProvinces {

    @Test
    @DisplayName("Trả về tất cả tỉnh khi lookupDTO null")
    void shouldReturnAllProvincesWhenNull() {
      when(areaService.getAllProvinces()).thenReturn(List.of());
      assertThat(controller.getProvinces(null)).isEmpty();
    }

    @Test
    @DisplayName("Trả về tỉnh theo điều kiện")
    void shouldReturnProvincesByCondition() {
      AreaLookupDTO lookup = mock(AreaLookupDTO.class);
      when(areaService.getAllProvincesByCondition(lookup)).thenReturn(List.of());
      assertThat(controller.getProvinces(lookup)).isEmpty();
    }
  }

  @Nested
  @DisplayName("GET /api/areas/lookup/{code1}")
  class GetDistricts {

    @Test
    @DisplayName("Trả về quận/huyện theo mã tỉnh")
    void shouldReturnDistricts() {
      when(areaService.getAllDistrictsOfProvince("01")).thenReturn(List.of());
      assertThat(controller.getDistrictsByProvinceCode("01")).isEmpty();
    }
  }

  @Nested
  @DisplayName("GET /api/areas/lookup/{code1}/{code2}")
  class GetPrecincts {

    @Test
    @DisplayName("Trả về phường/xã theo mã tỉnh và quận")
    void shouldReturnPrecincts() {
      when(areaService.getAllPrecinctOfProvinceOfDistrict("01", "001")).thenReturn(List.of());
      assertThat(controller.getPrecinctsByProvinceCodeAndDistrictCode("01", "001")).isEmpty();
    }
  }

  @Nested
  @DisplayName("GET /api/areas/{code}")
  class GetByCode {

    @Test
    @DisplayName("Trả về khu vực theo mã")
    void shouldReturnAreaByCode() {
      AreaDTO dto = mock(AreaDTO.class);
      when(areaService.getAreaByCode("01")).thenReturn(dto);
      assertThat(controller.getByCode("01")).isSameAs(dto);
    }

    @Test
    @DisplayName("Ném 404 khi không tìm thấy")
    void shouldThrow404WhenNotFound() {
      when(areaService.getAreaByCode("99")).thenReturn(null);
      assertThatThrownBy(() -> controller.getByCode("99"))
          .isInstanceOf(ResponseStatusException.class);
    }
  }
}
