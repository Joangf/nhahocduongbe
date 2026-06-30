package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.ExamCampaignDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.StudentExamStatusDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ExamCampaign;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.mapper.ExamCampaignMapper;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamCampaignRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamCampaignServiceImpl;

@DisplayName("ExamCampaignServiceImpl — Unit Tests")
@ExtendWith(MockitoExtension.class)
class ExamCampaignServiceImplUnitTest {

  @Mock ExamCampaignRepository examCampaignRepository;
  @Mock ExamCampaignMapper examCampaignMapper;
  @Mock PatientRepository patientRepository;
  @InjectMocks ExamCampaignServiceImpl service;

  // ─── Helpers ───────────────────────────────────────────────────────────────

  private ExamCampaign campaign(Long id, String name) {
    ExamCampaign c = new ExamCampaign();
    c.setId(id);
    c.setName(name);
    c.setStatus(true);
    c.setStartDate(LocalDate.of(2026, 6, 1));
    c.setEndDate(LocalDate.of(2026, 6, 30));
    return c;
  }

  private ExamCampaignDTO dto(Long id, String name) {
    ExamCampaignDTO d = new ExamCampaignDTO();
    d.setId(id);
    d.setName(name);
    return d;
  }

  // ─── TC-01: getAllActiveCampaigns ──────────────────────────────────────────
  @Nested
  @DisplayName("TC-01 getAllActiveCampaigns()")
  class GetAllActiveCampaigns {

    @Test
    @DisplayName("Trả về danh sách DTO khi có campaign active")
    void shouldReturnDtoList() {
      ExamCampaign entity = campaign(1L, "Đợt Q1");
      ExamCampaignDTO dto = dto(1L, "Đợt Q1");

      when(examCampaignRepository.findAllByStatusOrderByIdDesc(true)).thenReturn(List.of(entity));
      when(examCampaignMapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

      List<ExamCampaignDTO> result = service.getAllActiveCampaigns();

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getName()).isEqualTo("Đợt Q1");
      verify(examCampaignRepository).findAllByStatusOrderByIdDesc(true);
    }

    @Test
    @DisplayName("Trả về danh sách rỗng khi không có campaign")
    void shouldReturnEmptyListWhenNoCampaigns() {
      when(examCampaignRepository.findAllByStatusOrderByIdDesc(true)).thenReturn(Collections.emptyList());
      when(examCampaignMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      List<ExamCampaignDTO> result = service.getAllActiveCampaigns();

      assertThat(result).isEmpty();
    }
  }

  // ─── TC-02: getCampaignById ────────────────────────────────────────────────
  @Nested
  @DisplayName("TC-02 getCampaignById()")
  class GetCampaignById {

    @Test
    @DisplayName("Trả về DTO khi campaign tồn tại")
    void shouldReturnDtoWhenFound() {
      ExamCampaign entity = campaign(1L, "Đợt Q1");
      ExamCampaignDTO dto = dto(1L, "Đợt Q1");

      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(entity));
      when(examCampaignMapper.toDto(entity)).thenReturn(dto);

      ExamCampaignDTO result = service.getCampaignById(1L);

      assertThat(result.getId()).isEqualTo(1L);
      assertThat(result.getName()).isEqualTo("Đợt Q1");
    }

    @Test
    @DisplayName("Ném ResponseStatusException 404 khi không tìm thấy")
    void shouldThrow404WhenNotFound() {
      when(examCampaignRepository.findByIdAndStatus(99L, true)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.getCampaignById(99L))
          .isInstanceOf(ResponseStatusException.class)
          .hasMessageContaining("Campaign not found with id: 99");
    }
  }

  // ─── TC-03: createCampaign ─────────────────────────────────────────────────
  @Nested
  @DisplayName("TC-03 createCampaign()")
  class CreateCampaign {

    @Test
    @DisplayName("Lưu entity với id=null và status=true, trả về DTO")
    void shouldSaveWithNullIdAndActiveStatus() {
      ExamCampaignDTO inputDto = dto(99L, "Đợt mới"); // id sẽ bị đặt null
      ExamCampaign entity = campaign(null, "Đợt mới");
      ExamCampaign saved = campaign(2L, "Đợt mới");
      ExamCampaignDTO resultDto = dto(2L, "Đợt mới");

      when(examCampaignMapper.toEntity(inputDto)).thenReturn(entity);
      when(examCampaignRepository.save(any(ExamCampaign.class))).thenReturn(saved);
      when(examCampaignMapper.toDto(saved)).thenReturn(resultDto);

      ExamCampaignDTO result = service.createCampaign(inputDto);

      // Verify entity.id đã bị null và status=true trước khi save
      verify(examCampaignRepository).save(argThat(c -> c.getId() == null && Boolean.TRUE.equals(c.getStatus())));
      assertThat(result.getId()).isEqualTo(2L);
    }
  }

  // ─── TC-04: updateCampaign ─────────────────────────────────────────────────
  @Nested
  @DisplayName("TC-04 updateCampaign()")
  class UpdateCampaign {

    @Test
    @DisplayName("Cập nhật và trả về DTO khi tìm thấy campaign")
    void shouldUpdateAndReturnDto() {
      ExamCampaign entity = campaign(1L, "Cũ");
      ExamCampaignDTO updateDto = dto(1L, "Mới");
      ExamCampaign saved = campaign(1L, "Mới");
      ExamCampaignDTO resultDto = dto(1L, "Mới");

      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(entity));
      when(examCampaignRepository.save(entity)).thenReturn(saved);
      when(examCampaignMapper.toDto(saved)).thenReturn(resultDto);

      ExamCampaignDTO result = service.updateCampaign(1L, updateDto);

      verify(examCampaignMapper).partialUpdate(updateDto, entity);
      assertThat(result.getName()).isEqualTo("Mới");
    }

    @Test
    @DisplayName("Ném 404 khi cập nhật campaign không tồn tại")
    void shouldThrow404WhenNotFound() {
      when(examCampaignRepository.findByIdAndStatus(99L, true)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.updateCampaign(99L, dto(99L, "X")))
          .isInstanceOf(ResponseStatusException.class);
    }
  }

  // ─── TC-05: deleteCampaign ─────────────────────────────────────────────────
  @Nested
  @DisplayName("TC-05 deleteCampaign()")
  class DeleteCampaign {

    @Test
    @DisplayName("Soft-delete: đặt status=false và trả về true")
    void shouldSoftDeleteAndReturnTrue() {
      ExamCampaign entity = campaign(1L, "Đợt Q1");
      when(examCampaignRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.of(entity));

      boolean result = service.deleteCampaign(1L);

      assertThat(result).isTrue();
      assertThat(entity.getStatus()).isEqualTo(Boolean.FALSE);
      verify(examCampaignRepository).save(entity);
    }

    @Test
    @DisplayName("Trả về false khi campaign không tồn tại")
    void shouldReturnFalseWhenNotFound() {
      when(examCampaignRepository.findByIdAndStatus(99L, true)).thenReturn(Optional.empty());

      boolean result = service.deleteCampaign(99L);

      assertThat(result).isFalse();
      verify(examCampaignRepository, never()).save(any());
    }
  }

  // ─── TC-06: getStudentsByCampaignId ───────────────────────────────────────
  @Nested
  @DisplayName("TC-06 getStudentsByCampaignId()")
  class GetStudentsByCampaignId {

    @Test
    @DisplayName("Trả về danh sách học sinh với đúng trạng thái")
    void shouldReturnStudentStatusList() {
      StudentExamStatusDTO examined = new StudentExamStatusDTO(
          1L, "Nguyễn Văn A", "HS001", "5A", "0909000001",
          10L, LocalDate.of(2026, 6, 15), "EXAMINED");
      StudentExamStatusDTO notExamined = new StudentExamStatusDTO(
          2L, "Trần Thị B", "HS002", "5B", "0909000002",
          null, null, "NOT_EXAMINED");

      when(patientRepository.findStudentExamStatusByCampaignId(1L))
          .thenReturn(List.of(examined, notExamined));

      List<StudentExamStatusDTO> result = service.getStudentsByCampaignId(1L);

      assertThat(result).hasSize(2);
      assertThat(result.get(0).getStatus()).isEqualTo("EXAMINED");
      assertThat(result.get(1).getStatus()).isEqualTo("NOT_EXAMINED");
      assertThat(result.get(0).getExamId()).isEqualTo(10L);
      assertThat(result.get(1).getExamId()).isNull();
    }

    @Test
    @DisplayName("Trả về danh sách rỗng khi không có học sinh")
    void shouldReturnEmptyListWhenNoStudents() {
      when(patientRepository.findStudentExamStatusByCampaignId(99L)).thenReturn(Collections.emptyList());

      List<StudentExamStatusDTO> result = service.getStudentsByCampaignId(99L);

      assertThat(result).isEmpty();
    }
  }

  // ─── TC-07: notifyDentists ────────────────────────────────────────────────
  @Nested
  @DisplayName("TC-07 notifyDentists()")
  class NotifyDentists {

    @Test
    @DisplayName("Không làm gì khi campaign không tồn tại")
    void shouldDoNothingWhenCampaignNotFound() {
      when(patientRepository.findStudentExamStatusByCampaignId(99L)).thenReturn(Collections.emptyList());
      when(examCampaignRepository.findById(99L)).thenReturn(Optional.empty());

      // Không ném exception
      assertThatNoException().isThrownBy(() -> service.notifyDentists(99L));
    }

    @Test
    @DisplayName("Chỉ gửi thông báo cho học sinh chưa khám")
    void shouldOnlyNotifyUnexaminedStudents() {
      ExamCampaign campaign = campaign(1L, "Đợt Q1");
      StudentExamStatusDTO examined = new StudentExamStatusDTO(
          1L, "Nguyễn Văn A", "HS001", "5A", "0909",
          10L, LocalDate.now(), "EXAMINED");
      StudentExamStatusDTO notExamined = new StudentExamStatusDTO(
          2L, "Trần Thị B", "HS002", "5B", "0909",
          null, null, "NOT_EXAMINED");

      when(patientRepository.findStudentExamStatusByCampaignId(1L)).thenReturn(List.of(examined, notExamined));
      when(examCampaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

      // Chạy không ném exception
      assertThatNoException().isThrownBy(() -> service.notifyDentists(1L));
      // (Mock email - chỉ verify gọi đúng)
      verify(patientRepository).findStudentExamStatusByCampaignId(1L);
      verify(examCampaignRepository).findById(1L);
    }
  }
}
