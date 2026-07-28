package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.MedicalEnumService;

@DisplayName("MedicalEnumController — Unit Tests")
@ExtendWith(MockitoExtension.class)
class MedicalEnumControllerTest {

  @Mock MedicalEnumService medicalEnumService;
  @InjectMocks MedicalEnumController controller;

  @Test
  @DisplayName("GET /api/tartarCondition — trả về danh sách")
  void shouldReturnTartarCondition() {
    when(medicalEnumService.getListTartarCondition()).thenReturn(List.of(mock(TartarConditionDTO.class)));
    assertThat(controller.getListTartarCondition()).hasSize(1);
  }

  @Test
  @DisplayName("GET /api/plaqueCondition — trả về danh sách")
  void shouldReturnPlaqueCondition() {
    when(medicalEnumService.getListPlaqueCondition()).thenReturn(List.of(mock(PlaqueConditionDTO.class)));
    assertThat(controller.getListPlaqueConditionDTO()).hasSize(1);
  }

  @Test
  @DisplayName("GET /api/toothProblem — trả về danh sách")
  void shouldReturnToothProblem() {
    when(medicalEnumService.getListToothProblem()).thenReturn(List.of(mock(ToothProblemDTO.class)));
    assertThat(controller.getListToothProblemDTO()).hasSize(1);
  }

  @Test
  @DisplayName("GET /api/toothSide — trả về danh sách")
  void shouldReturnToothSide() {
    when(medicalEnumService.getListToothSide()).thenReturn(List.of(mock(ToothSideDTO.class)));
    assertThat(controller.getListToothSideDTO()).hasSize(1);
  }

  @Test
  @DisplayName("GET /api/toothTreatment — trả về danh sách")
  void shouldReturnToothTreatment() {
    when(medicalEnumService.getListToothTreatment()).thenReturn(List.of(mock(ToothTreatmentDTO.class)));
    assertThat(controller.getListToothTreatmentDTO()).hasSize(1);
  }

  @Test
  @DisplayName("GET /api/ethnics — trả về danh sách")
  void shouldReturnEthnics() {
    when(medicalEnumService.getListEthnics()).thenReturn(List.of(mock(EthnicDTO.class)));
    assertThat(controller.getListEthnics()).hasSize(1);
  }
}
