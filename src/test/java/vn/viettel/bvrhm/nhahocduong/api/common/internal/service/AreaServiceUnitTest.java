package vn.viettel.bvrhm.nhahocduong.api.common.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.common.internal.dto.AreaDTO;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.entity.Area;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.mapper.AreaMapper;
import vn.viettel.bvrhm.nhahocduong.api.common.internal.repository.AreaRepository;

@DisplayName("AreaService — Unit Test (2 dependencies)")
@ExtendWith(MockitoExtension.class)
public class AreaServiceUnitTest {

    @Mock AreaMapper mapper;
    @Mock AreaRepository repository; 
    @InjectMocks AreaService service;

    @DisplayName("getAllProvinces() unit test")
    @Nested
    class getAllProvinces {
        @Test
        @DisplayName("trả về list dto khi repo có data")
        void shouldReturnAreaDtoList(){
            List<Area> ancestor = new ArrayList<>();
            List<AreaDTO> ancestorDto = new ArrayList<>();
            Area area = new Area();
            area.setAncestor(ancestor);
            area.setCode("1");
            area.setId(0);
            area.setName("test");
            area.setParentAreaId(1);
            area.setType(100);

            AreaDTO areaDTO = new AreaDTO(area.getId(), area.getCode(), area.getName(), area.getType(), ancestorDto);
            List<AreaDTO> mockResult = List.of(areaDTO);

            when(repository.findAllByType(anyInt())).thenReturn(List.of(area));
            when(mapper.toDto(anyList())).thenReturn(mockResult);

            List<AreaDTO> result = service.getAllProvinces();
            assertThat(result).isEqualTo(mockResult);

            verify(repository).findAllByType(anyInt());
            verify(mapper).toDto(anyList());
        }
    }
}
