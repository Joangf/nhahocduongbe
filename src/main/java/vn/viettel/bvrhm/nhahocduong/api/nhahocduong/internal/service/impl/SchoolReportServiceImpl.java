package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.SchoolStatsDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.dto.StudentExamStatusDTO;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.OrganizationRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.repository.PatientRepository;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.SchoolReportService;

@Service
public class SchoolReportServiceImpl implements SchoolReportService {

  @Autowired private OrganizationRepository organizationRepository;
  @Autowired private PatientRepository patientRepository;

  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  @Override
  public byte[] exportAllSchoolsExcel() {
    List<Object[]> rows = organizationRepository.findSchoolStatsRaw();
    List<SchoolStatsDTO> stats = rows.stream().map(r -> new SchoolStatsDTO(
        ((Number) r[0]).longValue(),
        (String) r[1],
        ((Number) r[2]).longValue(),
        ((Number) r[3]).longValue(),
        ((Number) r[4]).doubleValue()
    )).toList();

    try (XSSFWorkbook wb = new XSSFWorkbook()) {
      Sheet sheet = wb.createSheet("Tổng hợp các trường");

      // Styles
      CellStyle headerStyle = createHeaderStyle(wb);
      CellStyle dataStyle = createDataStyle(wb);
      CellStyle totalStyle = createTotalStyle(wb);
      CellStyle pctStyle = createDataStyle(wb);
      pctStyle.setDataFormat(wb.createDataFormat().getFormat("0.0%"));

      // Title row
      Row titleRow = sheet.createRow(0);
      Cell titleCell = titleRow.createCell(0);
      titleCell.setCellValue("BÁO CÁO TỔNG HỢP KHÁM RĂNG MIỆNG THEO TRƯỜNG");
      titleCell.setCellStyle(createTitleStyle(wb));
      sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

      // Header
      Row headerRow = sheet.createRow(2);
      String[] headers = {"STT", "Tên trường", "Tổng số học sinh", "Số học sinh đã khám", "Tỷ lệ đã khám (%)"};
      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(headerStyle);
      }

      // Data rows
      int rowIdx = 3;
      int stt = 0;
      long sumTotal = 0, sumExamined = 0;
      for (SchoolStatsDTO s : stats) {
        Row row = sheet.createRow(rowIdx++);
        stt++;
        fillCell(row, 0, stt, dataStyle);
        fillCell(row, 1, s.getSchoolName(), dataStyle);
        fillCell(row, 2, s.getTotalStudents(), dataStyle);
        fillCell(row, 3, s.getExaminedStudents(), dataStyle);
        fillCell(row, 4, s.getExamRate(), pctStyle);
        sumTotal += s.getTotalStudents();
        sumExamined += s.getExaminedStudents();
      }

      // Summary row
      Row totalRow = sheet.createRow(rowIdx);
      totalRow.createCell(0).setCellStyle(totalStyle);
      fillCell(totalRow, 1, "TỔNG CỘNG", totalStyle);
      fillCell(totalRow, 2, sumTotal, totalStyle);
      fillCell(totalRow, 3, sumExamined, totalStyle);
      double overallRate = sumTotal > 0 ? (double) sumExamined / sumTotal : 0;
      Cell rateCell = totalRow.createCell(4);
      rateCell.setCellValue(overallRate);
      rateCell.setCellStyle(totalStyle);

      // Auto-size
      for (int i = 0; i < 5; i++) {
        sheet.autoSizeColumn(i);
      }

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      wb.write(out);
      return out.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Lỗi tạo Excel: " + e.getMessage(), e);
    }
  }

  @Override
  public byte[] exportSchoolStudentsExcel(Long schoolId, String schoolName) {
    List<Object[]> rows = patientRepository.findStudentsWithExamStatusBySchoolId(schoolId);
    List<StudentExamStatusDTO> students = rows.stream().map(r -> new StudentExamStatusDTO(
        ((Number) r[0]).longValue(),  // patientId
        (String) r[1],                // fullName
        (String) r[2],                // code
        (String) r[3],                // schoolClass
        (String) r[4],                // phoneNumber
        r[5] != null ? ((Number) r[5]).longValue() : null,  // examId
        r[6] != null ? ((java.sql.Date) r[6]).toLocalDate() : null, // examDate
        (String) r[7],                // examPlace
        (String) r[8]                 // status
    )).toList();

    try (XSSFWorkbook wb = new XSSFWorkbook()) {
      Sheet sheet = wb.createSheet("Danh sách học sinh");

      CellStyle headerStyle = createHeaderStyle(wb);
      CellStyle dataStyle = createDataStyle(wb);
      CellStyle examinedStyle = createDataStyle(wb);
      examinedStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
      examinedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      CellStyle notExaminedStyle = createDataStyle(wb);
      notExaminedStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
      notExaminedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

      // Title
      Row titleRow = sheet.createRow(0);
      Cell titleCell = titleRow.createCell(0);
      titleCell.setCellValue("DANH SÁCH HỌC SINH - " + schoolName);
      titleCell.setCellStyle(createTitleStyle(wb));
      sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

      // Stats summary
      long examined = students.stream().filter(s -> "EXAMINED".equals(s.getStatus())).count();
      long total = students.size();
      double rate = total > 0 ? (double) examined / total : 0;
      Row statRow = sheet.createRow(2);
      Cell statCell = statRow.createCell(0);
      statCell.setCellValue(String.format("Tỷ lệ đã khám: %.1f%% (%d/%d)",
          rate * 100, examined, total));
      statCell.setCellStyle(createTitleStyle(wb));
      sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

      // Header
      Row headerRow = sheet.createRow(4);
      String[] headers = {"STT", "Mã phiếu khám", "Tên học sinh", "Lớp", "Ngày khám", "Nơi khám", "Tình trạng"};
      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(headerStyle);
      }

      // Data rows
      int rowIdx = 5;
      int stt = 0;
      for (StudentExamStatusDTO s : students) {
        Row row = sheet.createRow(rowIdx++);
        stt++;
        boolean isExamined = "EXAMINED".equals(s.getStatus());
        CellStyle rowStyle = isExamined ? examinedStyle : notExaminedStyle;

        fillCell(row, 0, stt, rowStyle);
        fillCell(row, 1, s.getExamId() != null ? String.valueOf(s.getExamId()) : "", rowStyle);
        fillCell(row, 2, s.getFullName(), rowStyle);
        fillCell(row, 3, s.getSchoolClass(), rowStyle);
        fillCell(row, 4, s.getExamDate() != null ? DATE_FMT.format(s.getExamDate()) : "", rowStyle);
        fillCell(row, 5, s.getExamPlace() != null ? s.getExamPlace() : "", rowStyle);
        fillCell(row, 6, isExamined ? "Đã khám" : "Chưa khám", rowStyle);
      }

      for (int i = 0; i < 7; i++) {
        sheet.autoSizeColumn(i);
      }

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      wb.write(out);
      return out.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Lỗi tạo Excel: " + e.getMessage(), e);
    }
  }

  // ── Style helpers ──

  private CellStyle createTitleStyle(XSSFWorkbook wb) {
    CellStyle style = wb.createCellStyle();
    Font font = wb.createFont();
    font.setBold(true);
    font.setFontHeightInPoints((short) 14);
    style.setFont(font);
    style.setAlignment(HorizontalAlignment.CENTER);
    return style;
  }

  private CellStyle createHeaderStyle(XSSFWorkbook wb) {
    CellStyle style = wb.createCellStyle();
    Font font = wb.createFont();
    font.setBold(true);
    font.setFontHeightInPoints((short) 11);
    style.setFont(font);
    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    style.setAlignment(HorizontalAlignment.CENTER);
    return style;
  }

  private CellStyle createDataStyle(XSSFWorkbook wb) {
    CellStyle style = wb.createCellStyle();
    Font font = wb.createFont();
    font.setFontHeightInPoints((short) 10);
    style.setFont(font);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    return style;
  }

  private CellStyle createTotalStyle(XSSFWorkbook wb) {
    CellStyle style = wb.createCellStyle();
    Font font = wb.createFont();
    font.setBold(true);
    font.setFontHeightInPoints((short) 11);
    style.setFont(font);
    style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    return style;
  }

  private void fillCell(Row row, int col, String value, CellStyle style) {
    Cell cell = row.createCell(col);
    cell.setCellValue(value);
    cell.setCellStyle(style);
  }

  private void fillCell(Row row, int col, long value, CellStyle style) {
    Cell cell = row.createCell(col);
    cell.setCellValue(value);
    cell.setCellStyle(style);
  }

  private void fillCell(Row row, int col, double value, CellStyle style) {
    Cell cell = row.createCell(col);
    cell.setCellValue(value);
    cell.setCellStyle(style);
  }
}
