package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.DentistWithUserDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.DentistService;

@DisplayName("DentistController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class DentistControllerTest {

  @Mock DentistService dentistService;
  @InjectMocks DentistController controller;

  @Nested
  @DisplayName("GET /api/dentists")
  class GetAllDentists {

    @Test
    @DisplayName("Trả về danh sách nha sĩ")
    void shouldReturnAllDentists() {
      List<DentistWithUserDTO> expected = List.of();
      when(dentistService.getAllDentistsWithUserInfo()).thenReturn(expected);

      var result = controller.getAllDentists();

      assertThat(result).isEqualTo(expected);
      verify(dentistService).getAllDentistsWithUserInfo();
    }
  }
}
