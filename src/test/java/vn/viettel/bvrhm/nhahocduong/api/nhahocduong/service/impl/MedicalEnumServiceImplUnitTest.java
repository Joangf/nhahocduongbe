package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.*;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.MedicalEnumServiceImpl;

@DisplayName("MedicalEnumServiceImpl — Unit Test (0 dependency)")
class MedicalEnumServiceImplUnitTest {

  MedicalEnumServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new MedicalEnumServiceImpl();
  }

  // ──────────────────────────────────────────────
  // getListTartarCondition
  // ──────────────────────────────────────────────

  @Nested
  @DisplayName("getListTartarCondition()")
  class GetListTartarCondition {

    @Test
    @DisplayName("trả về list không rỗng")
    void shouldReturnNonEmptyList() {
      List<TartarConditionDTO> result = service.getListTartarCondition();

      assertThat(result).isNotEmpty();
    }

    @Test 
    @DisplayName("mỗi item có code và description không null")
    void shouldHaveCodeAndDesc(){
      List<TartarConditionDTO> result = service.getListTartarCondition();

      assertThat(result)
        .allMatch(item -> item.code() != null && !item.code().isBlank(),
                  "code không được rỗng, không được null")
        .allMatch(item -> item.description() != null && !item.description().isBlank(), 
                  "description không được null, không được rỗng");   
    }
  }

  // ──────────────────────────────────────────────
  // getListToothProblem
  // ──────────────────────────────────────────────

  @Nested
  @DisplayName("getListToothProblem()")
  class GetListToothProblem {

    @Test
    @DisplayName("trả về list không rỗng")
    void shouldReturnNonEmptyList() {
      List<ToothProblemDTO> result = service.getListToothProblem();

      assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("mỗi item có code và description hợp lệ")
    void shouldHaveValidCodeAndDescription() {
      List<ToothProblemDTO> result = service.getListToothProblem();

      assertThat(result)
          .allMatch(dto -> dto.code() != null && !dto.code().isBlank())
          .allMatch(dto -> dto.description() != null && !dto.description().isBlank());
    }
  }

  // ──────────────────────────────────────────────
  // getListToothSide
  // ──────────────────────────────────────────────

  @Nested
  @DisplayName("getListToothSide()")
  class GetListToothSide {

    @Test
    @DisplayName("trả về list không rỗng")
    void shouldReturnNonEmptyList() {
      List<ToothSideDTO> result = service.getListToothSide();

      assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("mỗi item có code và description hợp lệ")
    void shouldHaveValidCodeAndDescription() {
      List<ToothSideDTO> result = service.getListToothSide();

      assertThat(result)
          .allMatch(dto -> dto.code() != null && !dto.code().isBlank())
          .allMatch(dto -> dto.description() != null && !dto.description().isBlank());
    }
  }

  // ──────────────────────────────────────────────
  // getListToothTreatment
  // ──────────────────────────────────────────────

  @Nested
  @DisplayName("getListToothTreatment()")
  class GetListToothTreatment {

    @Test
    @DisplayName("trả về list không rỗng")
    void shouldReturnNonEmptyList() {
      List<ToothTreatmentDTO> result = service.getListToothTreatment();

      assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("mỗi item có code và description hợp lệ")
    void shouldHaveValidCodeAndDescription() {
      List<ToothTreatmentDTO> result = service.getListToothTreatment();

      assertThat(result)
          .allMatch(dto -> dto.code() != null && !dto.code().isBlank())
          .allMatch(dto -> dto.description() != null && !dto.code().isBlank());
    }
  }

  // ──────────────────────────────────────────────
  // getListEthnics
  // ──────────────────────────────────────────────

  @Nested
  @DisplayName("getListEthnics()")
  class GetListEthnics {

    @Test
    @DisplayName("trả về list không rỗng")
    void shouldReturnNonEmptyList() {
      List<EthnicDTO> result = service.getListEthnics();

      assertThat(result).isNotEmpty();
    }

    @Test 
    @DisplayName("description không được phép rỗng")
    void shouldReturnValidCodeAndDesc(){
      List<EthnicDTO> result = service.getListEthnics();
      
      assertThat(result)
        .allMatch(item -> item.description() != null && !item.description().isBlank());
    }
  }
}
