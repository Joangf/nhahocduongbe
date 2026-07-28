package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import vn.viettel.bvrhm.nhahocduong.api.common.internal.model.response.UpsertResponseModel;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.data.criteria.OrganizationSearchCriteria;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.OrganizationService;

@DisplayName("OrganizationController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class OrganizationControllerTest {

  @Mock OrganizationService organizationService;
  @InjectMocks OrganizationController controller;

  @Nested
  @DisplayName("GET /api/organization/all")
  class GetAll {

    @Test
    @DisplayName("Trả về tất cả tổ chức")
    void shouldReturnAll() {
      when(organizationService.getAll()).thenReturn(List.of());
      assertThat(controller.getAll()).isEmpty();
    }
  }

  @Nested
  @DisplayName("GET /api/organization/search")
  class Search {

    @Test
    @DisplayName("Tìm kiếm tổ chức")
    void shouldSearch() {
      OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
      Page<OrganizationDTO> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
      when(organizationService.search(any(), any())).thenReturn(page);

      var result = controller.search(criteria, PageRequest.of(0, 10));
      assertThat(result.getContent()).isEmpty();
    }
  }

  @Nested
  @DisplayName("POST /api/organization")
  class Create {

    @Test
    @DisplayName("Tạo tổ chức mới")
    void shouldCreate() {
      OrganizationDTO dto = mock(OrganizationDTO.class);
      when(organizationService.createOrganization(dto)).thenReturn(dto);
      assertThat(controller.createOrganization(dto)).isSameAs(dto);
    }
  }

  @Nested
  @DisplayName("PUT /api/organization/{id}")
  class Update {

    @Test
    @DisplayName("Cập nhật tổ chức")
    void shouldUpdate() {
      OrganizationDTO dto = mock(OrganizationDTO.class);
      when(organizationService.updateOrganization(dto, 1L)).thenReturn(dto);
      assertThat(controller.updateOrganization(dto, 1L)).isSameAs(dto);
    }
  }

  @Nested
  @DisplayName("GET /api/organization/{id}")
  class GetById {

    @Test
    @DisplayName("Trả về tổ chức theo id")
    void shouldGetById() {
      OrganizationDTO dto = mock(OrganizationDTO.class);
      when(organizationService.getOrganizationById(1L)).thenReturn(dto);
      assertThat(controller.getOrganizationById(1L)).isSameAs(dto);
    }
  }

  @Nested
  @DisplayName("DELETE /api/organization/{id}")
  class Delete {

    @Test
    @DisplayName("Xóa tổ chức")
    void shouldDelete() {
      when(organizationService.delete(1L)).thenReturn(true);
      assertThat(controller.deleteOrganization(1L)).isTrue();
    }
  }

  @Nested
  @DisplayName("POST /api/organization/{id}/classes/deletable")
  class CheckDeletable {

    @Test
    @DisplayName("Kiểm tra lớp có thể xóa")
    void shouldCheckDeletable() {
      UpsertResponseModel model = mock(UpsertResponseModel.class);
      when(organizationService.checkDeletableClasses(1L, List.of("10A"))).thenReturn(model);
      assertThat(controller.checkDeletableClass(1L, List.of("10A"))).isSameAs(model);
    }
  }
}
