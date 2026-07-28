package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import static org.assertj.core.api.Assertions.*;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Tooth;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothProblem;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothSide;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ToothCondition;

@DisplayName("DentalChartSvgRenderer — Unit Tests")
class DentalChartSvgRendererTest {

  private DentalChartSvgRenderer renderer;

  @BeforeEach
  void setUp() {
    renderer = new DentalChartSvgRenderer();
  }

  // ── renderSvgFileToPng() ──

  @Nested
  @DisplayName("renderSvgFileToPng()")
  class RenderSvgFileToPng {

    @Test
    @DisplayName("Render FDI numbering SVG thành PNG")
    void shouldRenderFdiSvg() {
      byte[] png = renderer.renderSvgFileToPng("template/Full_FDI_numbering_system.svg", 500f);

      assertThat(png).isNotEmpty();
      // PNG magic bytes: 0x89 0x50 0x4E 0x47
      assertThat(png[0]).isEqualTo((byte) 0x89);
      assertThat(png[1]).isEqualTo((byte) 0x50); // P
      assertThat(png[2]).isEqualTo((byte) 0x4E); // N
      assertThat(png[3]).isEqualTo((byte) 0x47); // G
    }

    @Test
    @DisplayName("Render polygon mapping SVG thành PNG")
    void shouldRenderPolygonMappingSvg() {
      byte[] png = renderer.renderSvgFileToPng("template/polygon_mapping.svg", 300f);

      assertThat(png).isNotEmpty();
      assertThat(png[0]).isEqualTo((byte) 0x89);
    }

    @Test
    @DisplayName("Ném RuntimeException khi file SVG không tồn tại")
    void shouldThrowWhenSvgNotFound() {
      assertThatThrownBy(() -> renderer.renderSvgFileToPng("template/non_existent.svg", 500f))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("Lỗi khi render SVG");
    }
  }

  // ── renderDentalChart() ──

  @Nested
  @DisplayName("renderDentalChart()")
  class RenderDentalChart {

    @Test
    @DisplayName("Render dental chart với dữ liệu rỗng")
    void shouldRenderWithEmptyRecord() {
      Map<Tooth, ToothCondition> record = new EnumMap<>(Tooth.class);

      byte[] png = renderer.renderDentalChart(record);

      assertThat(png).isNotEmpty();
      assertThat(png[0]).isEqualTo((byte) 0x89); // PNG magic
    }

    @Test
    @DisplayName("Render dental chart với record null")
    void shouldRenderWithNullRecord() {
      byte[] png = renderer.renderDentalChart(null);

      assertThat(png).isNotEmpty();
      assertThat(png[0]).isEqualTo((byte) 0x89);
    }

    @Test
    @DisplayName("Render răng sâu (CARIES)")
    void shouldRenderCaries() {
      Map<Tooth, ToothCondition> record = new EnumMap<>(Tooth.class);
      ToothCondition condition = new ToothCondition();
      condition.setProblem(ToothProblem.CARIES);
      condition.setLocations(List.of(ToothSide.CHEW));
      record.put(Tooth._11, condition);

      byte[] png = renderer.renderDentalChart(record);

      assertThat(png).isNotEmpty();
    }

    @Test
    @DisplayName("Render răng mất do sâu (LOST_CARIES) — hiển thị X mark")
    void shouldRenderLostCaries() {
      Map<Tooth, ToothCondition> record = new EnumMap<>(Tooth.class);
      ToothCondition condition = new ToothCondition();
      condition.setProblem(ToothProblem.LOST_CARIES);
      record.put(Tooth._16, condition);

      byte[] png = renderer.renderDentalChart(record);

      assertThat(png).isNotEmpty();
    }

    @Test
    @DisplayName("Render răng chưa mọc (YET_TO_GROW) — dashed outline")
    void shouldRenderYetToGrow() {
      Map<Tooth, ToothCondition> record = new EnumMap<>(Tooth.class);
      ToothCondition condition = new ToothCondition();
      condition.setProblem(ToothProblem.YET_TO_GROW);
      record.put(Tooth._48, condition);

      byte[] png = renderer.renderDentalChart(record);

      assertThat(png).isNotEmpty();
    }

    @Test
    @DisplayName("Render răng trám tốt (FILLING_NO_PROBLEM) với nhiều vị trí")
    void shouldRenderFillingWithMultipleLocations() {
      Map<Tooth, ToothCondition> record = new EnumMap<>(Tooth.class);
      ToothCondition condition = new ToothCondition();
      condition.setProblem(ToothProblem.FILLING_NO_PROBLEM);
      condition.setLocations(List.of(ToothSide.CHEW, ToothSide.INSIDE, ToothSide.NEAR));
      record.put(Tooth._26, condition);

      byte[] png = renderer.renderDentalChart(record);

      assertThat(png).isNotEmpty();
    }

    @Test
    @DisplayName("Render răng không có vị trí cụ thể — tô màu toàn bộ")
    void shouldRenderWithNoLocations() {
      Map<Tooth, ToothCondition> record = new EnumMap<>(Tooth.class);
      ToothCondition condition = new ToothCondition();
      condition.setProblem(ToothProblem.CARIES_FILLING);
      condition.setLocations(null);
      record.put(Tooth._31, condition);

      byte[] png = renderer.renderDentalChart(record);

      assertThat(png).isNotEmpty();
    }

    @Test
    @DisplayName("Render răng NO_PROBLEM — bỏ qua")
    void shouldSkipNoProblem() {
      Map<Tooth, ToothCondition> record = new EnumMap<>(Tooth.class);
      ToothCondition condition = new ToothCondition();
      condition.setProblem(ToothProblem.NO_PROBLEM);
      record.put(Tooth._11, condition);

      byte[] png = renderer.renderDentalChart(record);

      assertThat(png).isNotEmpty();
    }

    @Test
    @DisplayName("Render nhiều răng với nhiều loại bệnh khác nhau")
    void shouldRenderMultipleTeeth() {
      Map<Tooth, ToothCondition> record = new EnumMap<>(Tooth.class);

      ToothCondition caries = new ToothCondition();
      caries.setProblem(ToothProblem.CARIES);
      caries.setLocations(List.of(ToothSide.OUTSIDE));
      record.put(Tooth._11, caries);

      ToothCondition lost = new ToothCondition();
      lost.setProblem(ToothProblem.LOST_OTHER);
      record.put(Tooth._16, lost);

      ToothCondition filling = new ToothCondition();
      filling.setProblem(ToothProblem.FILLING_NO_PROBLEM);
      filling.setLocations(List.of(ToothSide.CHEW));
      record.put(Tooth._26, filling);

      ToothCondition bitHoRanh = new ToothCondition();
      bitHoRanh.setProblem(ToothProblem.BIT_HO_RANH);
      bitHoRanh.setLocations(List.of(ToothSide.CHEW));
      record.put(Tooth._36, bitHoRanh);

      ToothCondition truCau = new ToothCondition();
      truCau.setProblem(ToothProblem.TRU_CAU);
      truCau.setLocations(List.of(ToothSide.INSIDE));
      record.put(Tooth._46, truCau);

      byte[] png = renderer.renderDentalChart(record);

      assertThat(png).isNotEmpty();
      assertThat(png.length).isGreaterThan(100);
    }

    @Test
    @DisplayName("Render răng với condition null — bỏ qua")
    void shouldSkipNullCondition() {
      Map<Tooth, ToothCondition> record = new EnumMap<>(Tooth.class);
      record.put(Tooth._11, null);

      byte[] png = renderer.renderDentalChart(record);

      assertThat(png).isNotEmpty();
    }

    @Test
    @DisplayName("Render răng với problem null — bỏ qua")
    void shouldSkipNullProblem() {
      Map<Tooth, ToothCondition> record = new EnumMap<>(Tooth.class);
      ToothCondition condition = new ToothCondition();
      condition.setProblem(null);
      record.put(Tooth._11, condition);

      byte[] png = renderer.renderDentalChart(record);

      assertThat(png).isNotEmpty();
    }
  }
}
