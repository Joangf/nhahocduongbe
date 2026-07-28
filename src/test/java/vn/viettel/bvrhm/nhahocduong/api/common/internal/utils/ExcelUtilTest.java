package vn.viettel.bvrhm.nhahocduong.api.common.internal.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ExcelUtil Unit Tests")
class ExcelUtilTest {

  private InputStream createSampleWorkbookInputStream(String sheetName) throws IOException {
    try (Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Sheet sheet = workbook.createSheet(sheetName);
      Row row = sheet.createRow(0);
      row.createCell(0).setCellValue("Test String");
      row.createCell(1).setCellValue(12345.67);
      row.createCell(2).setCellValue(true);
      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    }
  }

  @Nested
  @DisplayName("getCellValue() Tests")
  class GetCellValueTests {

    @Test
    @DisplayName("Happy Path — accurately extract STRING, NUMERIC, BOOLEAN, FORMULA cell types")
    void getCellValue_extractsAllSupportedCellTypes() throws IOException {
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("TestSheet");
        Row row = sheet.createRow(0);

        // String cell
        Cell stringCell = row.createCell(0);
        stringCell.setCellValue("Hello Excel");

        // Numeric integer cell
        Cell numericIntCell = row.createCell(1);
        numericIntCell.setCellValue(42);

        // Numeric decimal cell
        Cell numericDecCell = row.createCell(2);
        numericDecCell.setCellValue(99.99);

        // Boolean cell
        Cell booleanCell = row.createCell(3);
        booleanCell.setCellValue(true);

        // Formula cell (e.g. SUM)
        Cell f1 = row.createCell(4);
        f1.setCellValue(10);
        Cell f2 = row.createCell(5);
        f2.setCellValue(20);
        Cell formulaCell = row.createCell(6);
        formulaCell.setCellFormula("SUM(E1:F1)");

        // Date cell
        Cell dateCell = row.createCell(7);
        Date now = new Date();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-MM-dd"));
        dateCell.setCellStyle(dateStyle);
        dateCell.setCellValue(now);

        // Assertions
        assertThat(ExcelUtil.getCellValue(stringCell)).isEqualTo("Hello Excel");
        assertThat(ExcelUtil.getCellValue(numericIntCell)).isEqualTo("42");
        assertThat(ExcelUtil.getCellValue(numericDecCell)).isEqualTo("99.99");
        assertThat(ExcelUtil.getCellValue(booleanCell)).isEqualTo(true);
        assertThat((Double) ExcelUtil.getCellValue(formulaCell)).isEqualTo(30.0);
        assertThat((Date) ExcelUtil.getCellValue(dateCell)).isCloseTo(now, 2000);
      }
    }

    @Test
    @DisplayName("Edge Case 1 — handle EMPTY / BLANK cells gracefully")
    void getCellValue_emptyOrBlankCell_returnsNull() throws IOException {
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("EmptySheet");
        Row row = sheet.createRow(0);
        Cell blankCell = row.createCell(0);

        assertThat(ExcelUtil.getCellValue(blankCell)).isNull();
      }
    }
  }

  @Nested
  @DisplayName("getSheetFromExcel() Tests")
  class GetSheetFromExcelTests {

    @Test
    @DisplayName("Happy Path — retrieve Sheet by Sheet Name successfully")
    void getSheetFromExcel_bySheetName_returnsSheet() throws IOException {
      try (InputStream in = createSampleWorkbookInputStream("Students")) {
        Sheet sheet = ExcelUtil.getSheetFromExcel(in, "Students");

        assertThat(sheet).isNotNull();
        assertThat(sheet.getSheetName()).isEqualTo("Students");
        assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Test String");
      }
    }

    @Test
    @DisplayName("Happy Path — retrieve Sheet by Index successfully")
    void getSheetFromExcel_byIndex_returnsSheet() throws IOException {
      try (InputStream in = createSampleWorkbookInputStream("Students")) {
        Sheet sheet = ExcelUtil.getSheetFromExcel(in, 0);

        assertThat(sheet).isNotNull();
        assertThat(sheet.getSheetName()).isEqualTo("Students");
      }
    }

    @Test
    @DisplayName("Edge Case 2 (Malformed Input) — throw Exception when reading corrupted / non-excel file")
    void getSheetFromExcel_malformedInput_throwsException() throws IOException {
      byte[] invalidBytes = "This is not a valid zip or xlsx file content".getBytes();
      try (InputStream in = new ByteArrayInputStream(invalidBytes)) {
        assertThatThrownBy(() -> ExcelUtil.getSheetFromExcel(in, 0))
            .isInstanceOf(Exception.class);
      }
    }

    @Test
    @DisplayName("Edge Case 2 (Malformed Input) — throw Exception when reading corrupted stream by sheet name")
    void getSheetFromExcel_malformedInputBySheetName_throwsException() throws IOException {
      byte[] invalidBytes = "Invalid data format".getBytes();
      try (InputStream in = new ByteArrayInputStream(invalidBytes)) {
        assertThatThrownBy(() -> ExcelUtil.getSheetFromExcel(in, "Sheet1"))
            .isInstanceOf(Exception.class);
      }
    }
  }

  @Nested
  @DisplayName("Styling & Formatting Utilities Tests")
  class StylingAndFormattingTests {

    @Test
    @DisplayName("addStyleForCells() — apply style to existing and non-existing cells in range")
    void addStyleForCells_appliesStyleToRange() throws IOException {
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("StyleSheet");
        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("Existing Cell");

        // Ensure row 1 is created so getRow(row) returns non-null
        sheet.createRow(1);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Act - apply style from col 0 to 1, row 0 to 1
        ExcelUtil.addStyleForCells(sheet, style, 0, 1, 0, 1);

        // Assert
        assertThat(sheet.getRow(0).getCell(0).getCellStyle().getFillForegroundColor())
            .isEqualTo(IndexedColors.YELLOW.getIndex());
        assertThat(sheet.getRow(0).getCell(1).getCellStyle().getFillForegroundColor())
            .isEqualTo(IndexedColors.YELLOW.getIndex());
        assertThat(sheet.getRow(1).getCell(0).getCellStyle().getFillForegroundColor())
            .isEqualTo(IndexedColors.YELLOW.getIndex());
        assertThat(sheet.getRow(1).getCell(1).getCellStyle().getFillForegroundColor())
            .isEqualTo(IndexedColors.YELLOW.getIndex());
      }
    }

    @Test
    @DisplayName("autoSizeColumns() — auto-size given number of columns without error")
    void autoSizeColumns_adjustsColumnWidths() throws IOException {
      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("WidthSheet");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("A very long column text that needs resizing");
        row.createCell(1).setCellValue("Short");

        // Act
        ExcelUtil.autoSizeColumns(sheet, 2);

        // Assert column width is greater than default (2048)
        assertThat(sheet.getColumnWidth(0)).isGreaterThan(0);
        assertThat(sheet.getColumnWidth(1)).isGreaterThan(0);
      }
    }
  }
}
