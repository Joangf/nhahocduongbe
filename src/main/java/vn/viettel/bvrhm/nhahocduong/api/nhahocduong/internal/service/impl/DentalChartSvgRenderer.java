package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.Tooth;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothProblem;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothSide;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.ToothCondition;

/**
 * Renders the dental chart SVG template by applying tooth condition data
 * (fills, X-marks, dashed outlines) and transcoding to PNG for PDF embedding.
 *
 * <p>This class is package-private — used exclusively by
 * {@link StudentExamReportServiceImpl}. It receives a clean
 * {@code Map<Tooth, ToothCondition>} and returns a PNG {@code byte[]}.
 * No database access, no PDF logic — pure SVG manipulation + rasterization.
 */
class DentalChartSvgRenderer {

  private static final String SVG_TEMPLATE = "template/dental_chart_template.svg";

  /** Surface element suffixes matching the SVG template IDs. */
  private static final String[] SURFACE_NAMES = {"chew", "outside", "inside", "near", "far"};

  /** Mapping from ToothProblem → SVG fill color (hex). */
  private static final Map<ToothProblem, String> PROBLEM_COLORS =
      Map.ofEntries(
          Map.entry(ToothProblem.NO_PROBLEM, "#FFFFFF"),
          Map.entry(ToothProblem.CARIES, "#E53935"),
          Map.entry(ToothProblem.CARIES_FILLING, "#FF9800"),
          Map.entry(ToothProblem.FILLING_NO_PROBLEM, "#1E88E5"),
          Map.entry(ToothProblem.LOST_CARIES, "#616161"),
          Map.entry(ToothProblem.LOST_OTHER, "#9E9E9E"),
          Map.entry(ToothProblem.BIT_HO_RANH, "#7B1FA2"),
          Map.entry(ToothProblem.TRU_CAU, "#00897B"),
          Map.entry(ToothProblem.YET_TO_GROW, "#F5F5F5"),
          Map.entry(ToothProblem.OTHERS, "#FDD835"));

  /** Mapping from ToothSide enum → SVG surface-id suffix. */
  private static final Map<ToothSide, String> SIDE_TO_SVG_ID =
      Map.of(
          ToothSide.CHEW, "chew",
          ToothSide.OUTSIDE, "outside",
          ToothSide.INSIDE, "inside",
          ToothSide.NEAR, "near",
          ToothSide.FAR, "far");

  // ── Public API ──────────────────────────────────────────────────────

  /**
   * Renders a generic static SVG from the classpath.
   *
   * @param classPath Path to the SVG resource (e.g. "template/Full_FDI_numbering_system.svg")
   * @param widthHint The rendering width (e.g. 500f) to achieve high resolution
   * @return PNG bytes
   */
  public byte[] renderSvgFileToPng(String classPath, float widthHint) {
    try {
      ClassPathResource resource = new ClassPathResource(classPath);
      String parser = XMLResourceDescriptor.getXMLParserClassName();
      SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
      String uri = resource.getURL().toString();
      Document svgDoc;
      try (InputStream is = resource.getInputStream()) {
        svgDoc = factory.createDocument(uri, is);
      }

      PNGTranscoder transcoder = new PNGTranscoder();
      transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, widthHint);
      TranscoderInput input = new TranscoderInput(svgDoc);
      try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
        TranscoderOutput output = new TranscoderOutput(out);
        transcoder.transcode(input, output);
        return out.toByteArray();
      }
    } catch (Exception e) {
      throw new RuntimeException("Lỗi khi render SVG " + classPath + ": " + e.getMessage(), e);
    }
  }

  /**
   * Renders the dental chart by overlaying condition data onto the SVG template
   * and transcoding the result to a high-resolution PNG.
   *
   * @param teethRecord Map of tooth → condition from the exam record (may be null)
   * @return PNG image bytes ready for OpenPDF {@code Image.getInstance()}
   */
  byte[] renderDentalChart(Map<Tooth, ToothCondition> teethRecord) {
    try {
      Document svgDoc = loadSvgTemplate();

      if (teethRecord != null) {
        for (Tooth tooth : Tooth.values()) {
          String code = tooth.getCode();
          Element group = svgDoc.getElementById("tooth-" + code);
          if (group == null) continue; // skip teeth not present in chart (e.g. deciduous)

          ToothCondition condition = teethRecord.get(tooth);
          if (condition == null || condition.getProblem() == null) continue;

          ToothProblem problem = condition.getProblem();
          if (problem == ToothProblem.NO_PROBLEM) continue;

          String color = PROBLEM_COLORS.getOrDefault(problem, "#FFFFFF");

          // ── Missing teeth: fill all surfaces gray + show X marks ──
          if (problem == ToothProblem.LOST_CARIES || problem == ToothProblem.LOST_OTHER) {
            fillAllSurfaces(svgDoc, code, color);
            showXMark(svgDoc, code);
            continue;
          }

          // ── Not-yet-erupted: dashed outline + light fill ──
          if (problem == ToothProblem.YET_TO_GROW) {
            applyDashedOutline(svgDoc, code);
            continue;
          }

          // ── Conditions with specific surface locations ──
          if (condition.getLocations() != null && !condition.getLocations().isEmpty()) {
            for (ToothSide side : condition.getLocations()) {
              String suffix = SIDE_TO_SVG_ID.get(side);
              if (suffix == null) continue;
              Element surface = svgDoc.getElementById("tooth-" + code + "-" + suffix);
              if (surface != null) {
                surface.setAttribute("style", "fill: " + color + ";");
              }
            }
          } else {
            // No locations specified → color entire tooth
            fillAllSurfaces(svgDoc, code, color);
          }
        }
      }

      return transcodeToPng(svgDoc);
    } catch (Exception e) {
      throw new RuntimeException("Lỗi khi tạo sơ đồ răng: " + e.getMessage(), e);
    }
  }

  // ── Private helpers ─────────────────────────────────────────────────

  private Document loadSvgTemplate() throws Exception {
    String parser = XMLResourceDescriptor.getXMLParserClassName();
    SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
    ClassPathResource resource = new ClassPathResource(SVG_TEMPLATE);
    String uri = resource.getURL().toString();
    try (InputStream is = resource.getInputStream()) {
      return factory.createDocument(uri, is);
    }
  }

  /** Sets the {@code fill} attribute on all 5 surface elements of a tooth. */
  private void fillAllSurfaces(Document doc, String toothCode, String color) {
    for (String surfaceName : SURFACE_NAMES) {
      Element el = doc.getElementById("tooth-" + toothCode + "-" + surfaceName);
      if (el != null) {
        el.setAttribute("style", "fill: " + color + ";");
      }
    }
  }

  /** Makes the hidden X-mark lines visible for missing teeth. */
  private void showXMark(Document doc, String toothCode) {
    Element x1 = doc.getElementById("tooth-" + toothCode + "-x1");
    Element x2 = doc.getElementById("tooth-" + toothCode + "-x2");
    if (x1 != null) x1.setAttribute("visibility", "visible");
    if (x2 != null) x2.setAttribute("visibility", "visible");
  }

  /** Applies a dashed stroke and light fill to indicate an un-erupted tooth. */
  private void applyDashedOutline(Document doc, String toothCode) {
    for (String surfaceName : SURFACE_NAMES) {
      Element el = doc.getElementById("tooth-" + toothCode + "-" + surfaceName);
      if (el != null) {
        el.setAttribute("stroke-dasharray", "3,2");
        el.setAttribute("fill", "#F5F5F5");
      }
    }
  }

  /** Transcodes the SVG DOM to a high-resolution PNG byte array. */
  private byte[] transcodeToPng(Document svgDoc) throws Exception {
    PNGTranscoder transcoder = new PNGTranscoder();
    // Render at 2× for crisp output when scaled in the PDF
    transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 1480f);
    TranscoderInput input = new TranscoderInput(svgDoc);
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      TranscoderOutput output = new TranscoderOutput(out);
      transcoder.transcode(input, output);
      return out.toByteArray();
    }
  }
}
