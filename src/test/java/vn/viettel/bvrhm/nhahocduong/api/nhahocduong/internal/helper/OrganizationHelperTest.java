package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Grade;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.OrganizationDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Organization;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.OrganizationRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationHelper Unit Tests")
class OrganizationHelperTest {

  @Mock private OrganizationRepository organizationRepository;
  @InjectMocks private OrganizationHelper organizationHelper;

  // --- Helper methods ---

  private OrganizationDTO createDTO(Map<Grade, List<String>> classes) {
    OrganizationDTO dto = new OrganizationDTO();
    dto.setAreaCode("001");
    dto.setClasses(classes);
    return dto;
  }

  private OrganizationDTO createDTO(String areaCode) {
    OrganizationDTO dto = new OrganizationDTO();
    dto.setAreaCode(areaCode);
    return dto;
  }

  // --- getFlattenClassList() Tests ---

  @Nested
  @DisplayName("getFlattenClassList()")
  class GetFlattenClassListTests {

    @Test
    @DisplayName("classes = null → trả về null")
    void nullClasses_returnsNull() {
      OrganizationDTO dto = createDTO((Map<Grade, List<String>>) null);
      assertThat(organizationHelper.getFlattenClassList(dto)).isNull();
    }

    @Test
    @DisplayName("classes = empty map → trả về null")
    void emptyClasses_returnsNull() {
      OrganizationDTO dto = createDTO(Collections.emptyMap());
      assertThat(organizationHelper.getFlattenClassList(dto)).isNull();
    }

    @Test
    @DisplayName("1 grade, nhiều lớp → trả về danh sách phẳng")
    void singleGrade_returnsFlattenList() {
      Map<Grade, List<String>> classes = new HashMap<>();
      classes.put(Grade._1, List.of("1A", "1B"));
      OrganizationDTO dto = createDTO(classes);

      List<String> result = organizationHelper.getFlattenClassList(dto);
      assertThat(result).containsExactlyInAnyOrder("1A", "1B");
    }

    @Test
    @DisplayName("Nhiều grades → trả về tất cả lớp gộp lại")
    void multipleGrades_returnsAllClasses() {
      Map<Grade, List<String>> classes = new HashMap<>();
      classes.put(Grade._1, List.of("1A"));
      classes.put(Grade._5, List.of("5A", "5B"));
      classes.put(Grade._12, List.of("12A"));
      OrganizationDTO dto = createDTO(classes);

      List<String> result = organizationHelper.getFlattenClassList(dto);
      assertThat(result).containsExactlyInAnyOrder("1A", "5A", "5B", "12A");
    }

    @Test
    @DisplayName("Grade có list rỗng → bỏ qua grade đó")
    void gradeWithEmptyList_skipsThatGrade() {
      Map<Grade, List<String>> classes = new HashMap<>();
      classes.put(Grade._1, List.of("1A"));
      classes.put(Grade._2, Collections.emptyList());
      OrganizationDTO dto = createDTO(classes);

      List<String> result = organizationHelper.getFlattenClassList(dto);
      assertThat(result).containsExactly("1A");
    }
  }

  // --- getDuplicateClassList() Tests ---

  @Nested
  @DisplayName("getDuplicateClassList()")
  class GetDuplicateClassListTests {

    @Test
    @DisplayName("classes = null → trả về null")
    void nullClasses_returnsNull() {
      OrganizationDTO dto = createDTO((Map<Grade, List<String>>) null);
      assertThat(organizationHelper.getDuplicateClassList(dto)).isNull();
    }

    @Test
    @DisplayName("classes = empty map → trả về null")
    void emptyClasses_returnsNull() {
      OrganizationDTO dto = createDTO(Collections.emptyMap());
      assertThat(organizationHelper.getDuplicateClassList(dto)).isNull();
    }

    @Test
    @DisplayName("Không có trùng lặp → trả về list rỗng")
    void noDuplicates_returnsEmptyList() {
      Map<Grade, List<String>> classes = new HashMap<>();
      classes.put(Grade._1, List.of("1A", "1B"));
      classes.put(Grade._2, List.of("2A"));
      OrganizationDTO dto = createDTO(classes);

      List<String> result = organizationHelper.getDuplicateClassList(dto);
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Trùng lặp chính xác → trả về danh sách trùng")
    void exactDuplicates_returnsDuplicates() {
      Map<Grade, List<String>> classes = new HashMap<>();
      classes.put(Grade._1, List.of("1A", "1B"));
      classes.put(Grade._2, List.of("1A", "2A"));
      OrganizationDTO dto = createDTO(classes);

      List<String> result = organizationHelper.getDuplicateClassList(dto);
      assertThat(result).containsExactly("1A");
    }

    @Test
    @DisplayName("Trùng lặp khác hoa/thường → phát hiện duplicate")
    void caseInsensitiveDuplicates_returnsDuplicates() {
      Map<Grade, List<String>> classes = new HashMap<>();
      classes.put(Grade._1, List.of("1a"));
      classes.put(Grade._2, List.of("1A"));
      OrganizationDTO dto = createDTO(classes);

      List<String> result = organizationHelper.getDuplicateClassList(dto);
      assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Trùng lặp do khoảng trắng → phát hiện duplicate")
    void trimmedDuplicates_returnsDuplicates() {
      Map<Grade, List<String>> classes = new HashMap<>();
      classes.put(Grade._1, List.of(" 1A "));
      classes.put(Grade._2, List.of("1A"));
      OrganizationDTO dto = createDTO(classes);

      List<String> result = organizationHelper.getDuplicateClassList(dto);
      assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Nhiều duplicates → trả về tất cả")
    void multipleDuplicates_returnsAll() {
      Map<Grade, List<String>> classes = new HashMap<>();
      classes.put(Grade._1, List.of("1A", "2B"));
      classes.put(Grade._2, List.of("1A", "2B"));
      OrganizationDTO dto = createDTO(classes);

      List<String> result = organizationHelper.getDuplicateClassList(dto);
      assertThat(result).hasSize(2);
    }
  }

  // --- generateCode() Tests ---

  @Nested
  @DisplayName("generateCode()")
  class GenerateCodeTests {

    @Test
    @DisplayName("Không có organization trong DB → trả về areaCode + 001")
    void noExistingOrganization_returnsAreaCodePlus001() {
      OrganizationDTO dto = createDTO("005");
      when(organizationRepository.findFirstByAreaCodeOrderByCodeDesc("005"))
          .thenReturn(null);

      String code = organizationHelper.generateCode(dto);
      assertThat(code).isEqualTo("005001");
    }

    @Test
    @DisplayName("Có organization cuối cùng → tăng số thứ tự lên 1")
    void existingOrganization_incrementsOrderNumber() {
      OrganizationDTO dto = createDTO("001");
      Organization latest = new Organization();
      latest.setCode("001005");
      when(organizationRepository.findFirstByAreaCodeOrderByCodeDesc("001"))
          .thenReturn(latest);

      String code = organizationHelper.generateCode(dto);
      assertThat(code).isEqualTo("001006");
    }

    @Test
    @DisplayName("Organization cuối cùng có code 001001 → trả về 001002")
    void existingOrganization_firstCode_increments() {
      OrganizationDTO dto = createDTO("001");
      Organization latest = new Organization();
      latest.setCode("001001");
      when(organizationRepository.findFirstByAreaCodeOrderByCodeDesc("001"))
          .thenReturn(latest);

      String code = organizationHelper.generateCode(dto);
      assertThat(code).isEqualTo("001002");
    }

    @Test
    @DisplayName("Area code có số lớn → format đúng 3 chữ số")
    void largeAreaCode_formatsCorrectly() {
      OrganizationDTO dto = createDTO("12");
      when(organizationRepository.findFirstByAreaCodeOrderByCodeDesc("12"))
          .thenReturn(null);

      String code = organizationHelper.generateCode(dto);
      assertThat(code).isEqualTo("012001");
    }

    @Test
    @DisplayName("Organization cuối có số thứ tự lớn → tăng đúng")
    void existingOrganization_largeOrderNumber_increments() {
      OrganizationDTO dto = createDTO("001");
      Organization latest = new Organization();
      latest.setCode("001099");
      when(organizationRepository.findFirstByAreaCodeOrderByCodeDesc("001"))
          .thenReturn(latest);

      String code = organizationHelper.generateCode(dto);
      assertThat(code).isEqualTo("001100");
    }
  }
}
