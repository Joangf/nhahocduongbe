package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Exam;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.Patient;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.PlaqueRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TartarRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.entity.TreatmentRecord;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.ExamRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.StudentExamReportService;

@Service
@Transactional(readOnly = true)
public class StudentExamReportServiceImpl implements StudentExamReportService {

  @Autowired private PatientRepository patientRepository;
  @Autowired private ExamRepository examRepository;

  private final DentalChartSvgRenderer dentalChartRenderer = new DentalChartSvgRenderer();

  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  @Override
  public byte[] generateExamReportPdf(Long examId) {
    Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu khám"));
            
    Patient patient = exam.getPatient();
    if (patient == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học sinh");
    }

    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Document document = new Document(PageSize.A4, 36, 36, 36, 36);
      PdfWriter.getInstance(document, out);
      document.open();

      BaseFont baseFont = loadUnicodeFont();
      Font titleFont = new Font(baseFont, 16, Font.BOLD);
      Font headerFont = new Font(baseFont, 11, Font.BOLD);
      Font normalFont = new Font(baseFont, 10, Font.NORMAL);
      Font smallFont = new Font(baseFont, 9, Font.NORMAL);

      // ── Title ──
      Paragraph title = new Paragraph("PHIẾU KHÁM RĂNG MIỆNG HỌC SINH", titleFont);
      title.setAlignment(Paragraph.ALIGN_CENTER);
      title.setSpacingAfter(16);
      document.add(title);

      // ── Student Info Table ──
      PdfPTable infoTable = new PdfPTable(4);
      infoTable.setWidthPercentage(100);
      infoTable.setSpacingAfter(10);
      infoTable.setWidths(new float[] {2f, 3f, 2f, 3f});

      addInfoRow(infoTable, "Họ và tên:", patient.getFullName(), headerFont, normalFont);
      addInfoRow(infoTable, "Mã học sinh:", nvl(patient.getCode()), headerFont, normalFont);
      addInfoRow(
          infoTable,
          "Ngày sinh:",
          patient.getBirthDate() != null ? DATE_FMT.format(patient.getBirthDate()) : "",
          headerFont,
          normalFont);
      addInfoRow(
          infoTable,
          "Giới tính:",
          patient.getGender() != null ? (patient.getGender() == 1 ? "Nam" : "Nữ") : "",
          headerFont,
          normalFont);
      addInfoRow(
          infoTable,
          "Trường:",
          patient.getOrganization() != null ? patient.getOrganization().getName() : "",
          headerFont,
          normalFont);
      addInfoRow(infoTable, "Lớp:", nvl(patient.getSchoolClass()), headerFont, normalFont);
      addInfoRow(
          infoTable,
          "Ngày khám:",
          exam.getDate() != null ? DATE_FMT.format(exam.getDate()) : "",
          headerFont,
          normalFont);
      addInfoRow(
          infoTable,
          "Bác sĩ:",
          exam.getDentist() != null ? nvl(exam.getDentist().getTitle()) : "",
          headerFont,
          normalFont);
      document.add(infoTable);

      // ── 1. Teeth Condition ──
      document.add(spacedTitle("1. TÌNH TRẠNG RĂNG", headerFont));

      // ── Dental Chart (SVG → PNG) ──
      if (exam.getTeethRecord() != null && exam.getTeethRecord().getRecord() != null) {
        try {
          byte[] chartPng =
              dentalChartRenderer.renderDentalChart(exam.getTeethRecord().getRecord());
          Image chartImage = Image.getInstance(chartPng);
          chartImage.scaleToFit(500, 250);
          chartImage.setAlignment(Image.ALIGN_CENTER);
          document.add(chartImage);
          
          // ── Annotation Section (FDI, Mapping, Legend) ──
          PdfPTable annotationTable = new PdfPTable(2);
          annotationTable.setWidthPercentage(100);
          annotationTable.setSpacingBefore(10);
          annotationTable.setSpacingAfter(10);
          annotationTable.setWidths(new float[]{1f, 1f}); // 50% 50%

          // Left Column (FDI)
          byte[] fdiPng = dentalChartRenderer.renderSvgFileToPng("template/Full_FDI_numbering_system.svg", 500f);
          Image fdiImage = Image.getInstance(fdiPng);
          fdiImage.scaleToFit(160, 240);
          fdiImage.setAlignment(Element.ALIGN_CENTER);
          PdfPCell leftCell = new PdfPCell(fdiImage);
          leftCell.setBorder(Rectangle.NO_BORDER);
          leftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
          leftCell.setVerticalAlignment(Element.ALIGN_TOP);
          annotationTable.addCell(leftCell);

          // Right Column (Mapping + Legend)
          PdfPCell rightCell = new PdfPCell();
          rightCell.setBorder(Rectangle.NO_BORDER);
          rightCell.setVerticalAlignment(Element.ALIGN_TOP);

          byte[] mappingPng = dentalChartRenderer.renderSvgFileToPng("template/polygon_mapping.svg", 300f);
          Image mappingImage = Image.getInstance(mappingPng);
          mappingImage.scaleToFit(140, 140);
          mappingImage.setAlignment(Element.ALIGN_CENTER);
          rightCell.addElement(mappingImage);

          PdfPTable legend = buildChartLegend(smallFont);
          rightCell.addElement(legend);

          annotationTable.addCell(rightCell);
          document.add(annotationTable);

        } catch (Exception e) {
          // Fallback: continue without chart if rendering fails
          document.add(new Paragraph("  [Không thể tạo sơ đồ răng: " + e.getMessage() + "]", smallFont));
        }
      }

      // ── Teeth Table ──
      if (exam.getTeethRecord() != null && exam.getTeethRecord().getRecord() != null) {
        PdfPTable teethTable = new PdfPTable(4);
        teethTable.setWidthPercentage(100);
        teethTable.setSpacingAfter(10);
        teethTable.setWidths(new float[] {1.5f, 3f, 3f, 2.5f});

        for (String h : new String[] {"Răng", "Vấn đề", "Vị trí", "Điều trị"}) {
          teethTable.addCell(headerCell(h, headerFont));
        }

        exam.getTeethRecord().getRecord().forEach(
            (tooth, condition) -> {
              if (condition != null && condition.getProblem() != null 
                  && condition.getProblem() != vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothProblem.NO_PROBLEM) {
                teethTable.addCell(new Phrase(tooth.name().replace("_", ""), normalFont));
                teethTable.addCell(new Phrase(condition.getProblem().getDescription(), normalFont));
                teethTable.addCell(
                    new Phrase(
                        condition.getLocations() != null
                            ? condition.getLocations().stream()
                                .map(vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constants.enums.ToothSide::getDescription)
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("")
                            : "",
                        normalFont));
                teethTable.addCell(
                    new Phrase(
                        condition.getTreatment() != null
                            ? condition.getTreatment().getDescription()
                            : "",
                        normalFont));
              }
            });
        document.add(teethTable);
      } else {
        document.add(new Paragraph("  Không có dữ liệu.", smallFont));
      }

      // ── 2. OHI-S ──
      document.add(spacedTitle("2. TÌNH TRẠNG VỆ SINH RĂNG MIỆNG (OHI-S)", headerFont));
      document.add(renderOhiTable(exam.getPlaqueRecord(), exam.getTartarRecord(), headerFont, normalFont));

      // ── 3. Treatments ──
      document.add(spacedTitle("3. ĐIỀU TRỊ", headerFont));
      if (exam.getTreatmentRecords() != null && !exam.getTreatmentRecords().isEmpty()) {
        PdfPTable treatTable = new PdfPTable(4);
        treatTable.setWidthPercentage(100);
        treatTable.setSpacingAfter(10);
        treatTable.setWidths(new float[] {1.5f, 3f, 3f, 2.5f});

        for (String h : new String[] {"Răng", "Dịch vụ", "Chẩn đoán", "Thuốc"}) {
          treatTable.addCell(headerCell(h, headerFont));
        }
        for (TreatmentRecord tr : exam.getTreatmentRecords()) {
          treatTable.addCell(
              new Phrase(tr.getTooth() != null ? tr.getTooth().name() : "", normalFont));
          treatTable.addCell(
              new Phrase(tr.getService() != null ? tr.getService().name() : "", normalFont));
          treatTable.addCell(new Phrase(nvl(tr.getDiagnosis()), normalFont));
          treatTable.addCell(
              new Phrase(
                  tr.getPrescription() != null
                      ? tr.getPrescription().stream()
                          .map(p -> p.medicationCode() + " x" + p.quantity())
                          .reduce((a, b) -> a + "; " + b)
                          .orElse("")
                      : "",
                  normalFont));
        }
        document.add(treatTable);
      } else {
        document.add(new Paragraph("  Không có điều trị.", smallFont));
      }

      // ── 4. Assessment ──
      document.add(spacedTitle("4. ĐÁNH GIÁ BỆNH LÝ", headerFont));
      document.add(
          new Paragraph("  " + nvl(exam.getPathologyAssessment(), "Trống"), normalFont));

      // ── 5. Treatment Note ──
      document.add(spacedTitle("5. GHI CHÚ ĐIỀU TRỊ", headerFont));
      document.add(new Paragraph("  " + nvl(exam.getTreatmentNote(), "Trống"), normalFont));

      document.close();
      return out.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Lỗi tạo PDF: " + e.getMessage(), e);
    }
  }

  // ── Helpers ──

  private static final String FONT_PATH =
      "fonts/dejavu-fonts-ttf-2.37/dejavu-fonts-ttf-2.37/ttf/DejaVuSans.ttf";

  private BaseFont loadUnicodeFont() {
    try {
      ClassPathResource resource = new ClassPathResource(FONT_PATH);
      try (InputStream is = resource.getInputStream()) {
        return BaseFont.createFont(
            FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, null, is.readAllBytes());
      }
    } catch (Exception e) {
      throw new RuntimeException("Không thể tải font DejaVu Sans cho PDF: " + e.getMessage(), e);
    }
  }

  private PdfPTable renderOhiTable(
      PlaqueRecord plaque, TartarRecord tartar, Font headerFont, Font normalFont) {
    PdfPTable table = new PdfPTable(3);
    table.setWidthPercentage(100);
    table.setSpacingAfter(10);
    table.setWidths(new float[] {3f, 3f, 3f});

    table.addCell(headerCell("Vị trí", headerFont));
    table.addCell(headerCell("Mảng bám (DI)", headerFont));
    table.addCell(headerCell("Vôi răng (CI)", headerFont));

    String[] positions = {"17-16n", "11n", "26-27n", "47-46t", "31n", "36-37t"};
    for (String pos : positions) {
      table.addCell(new Phrase(pos, normalFont));
      table.addCell(new Phrase(getPlaqueDesc(plaque, pos), normalFont));
      table.addCell(new Phrase(getTartarDesc(tartar, pos), normalFont));
    }
    return table;
  }

  private String getPlaqueDesc(PlaqueRecord p, String key) {
    if (p == null) return "-";
    return switch (key) {
      case "17-16n" -> p.get_17_16n() != null ? p.get_17_16n().getDescription() : "-";
      case "11n" -> p.get_11n() != null ? p.get_11n().getDescription() : "-";
      case "26-27n" -> p.get_26_27n() != null ? p.get_26_27n().getDescription() : "-";
      case "47-46t" -> p.get_47_46t() != null ? p.get_47_46t().getDescription() : "-";
      case "31n" -> p.get_31n() != null ? p.get_31n().getDescription() : "-";
      case "36-37t" -> p.get_36_37t() != null ? p.get_36_37t().getDescription() : "-";
      default -> "-";
    };
  }

  private String getTartarDesc(TartarRecord t, String key) {
    if (t == null) return "-";
    return switch (key) {
      case "17-16n" -> t.get_17_16n() != null ? t.get_17_16n().getDescription() : "-";
      case "11n" -> t.get_11n() != null ? t.get_11n().getDescription() : "-";
      case "26-27n" -> t.get_26_27n() != null ? t.get_26_27n().getDescription() : "-";
      case "47-46t" -> t.get_47_46t() != null ? t.get_47_46t().getDescription() : "-";
      case "31n" -> t.get_31n() != null ? t.get_31n().getDescription() : "-";
      case "36-37t" -> t.get_36_37t() != null ? t.get_36_37t().getDescription() : "-";
      default -> "-";
    };
  }

  private PdfPCell headerCell(String text, Font font) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    cell.setBackgroundColor(new Color(220, 220, 220));
    cell.setPadding(4);
    return cell;
  }

  private Paragraph spacedTitle(String text, Font font) {
    Paragraph p = new Paragraph(text, font);
    p.setSpacingBefore(10);
    p.setSpacingAfter(6);
    return p;
  }

  private void addInfoRow(
      PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
    PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
    labelCell.setBorder(Rectangle.NO_BORDER);
    labelCell.setPadding(3);
    table.addCell(labelCell);
    PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
    valueCell.setBorder(Rectangle.NO_BORDER);
    valueCell.setPadding(3);
    table.addCell(valueCell);
  }

  private String nvl(String s) {
    return s != null ? s : "";
  }

  private String nvl(String s, String fallback) {
    return s != null && !s.isEmpty() ? s : fallback;
  }

  // ── Dental Chart Legend ──

  private PdfPTable buildChartLegend(Font font) {
    PdfPTable legend = new PdfPTable(4);
    legend.setWidthPercentage(100);
    legend.setSpacingBefore(20);
    legend.setSpacingAfter(10);
    legend.setWidths(new float[] {0.4f, 1.6f, 0.4f, 1.6f});

    // Row 1
    addLegendEntry(legend, new Color(255, 255, 255), "Bình thường", font);
    addLegendEntry(legend, new Color(229, 57, 53), "Sâu", font);
    // Row 2
    addLegendEntry(legend, new Color(255, 152, 0), "Sâu trám lại", font);
    addLegendEntry(legend, new Color(30, 136, 229), "Trám tốt", font);
    // Row 3
    addLegendEntry(legend, new Color(97, 97, 97), "Mất do sâu", font);
    addLegendEntry(legend, new Color(158, 158, 158), "Mất lý do khác", font);
    // Row 4
    addLegendEntry(legend, new Color(123, 31, 162), "Bít hố rãnh", font);
    addLegendEntry(legend, new Color(0, 137, 123), "Trụ cầu", font);
    // Row 5
    addLegendEntry(legend, new Color(245, 245, 245), "Chưa mọc", font);
    addLegendEntry(legend, new Color(253, 216, 53), "Loại trừ", font);

    return legend;
  }

  private void addLegendEntry(PdfPTable table, Color color, String text, Font font) {
    PdfPCell colorCell = new PdfPCell();
    colorCell.setBackgroundColor(color);
    colorCell.setBorderColor(new Color(150, 150, 150));
    colorCell.setFixedHeight(10);
    table.addCell(colorCell);

    PdfPCell textCell = new PdfPCell(new Phrase(text, font));
    textCell.setBorder(Rectangle.NO_BORDER);
    textCell.setPaddingLeft(2);
    textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    table.addCell(textCell);
  }
}
