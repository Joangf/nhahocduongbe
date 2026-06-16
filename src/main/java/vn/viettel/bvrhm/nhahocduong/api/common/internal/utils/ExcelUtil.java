package vn.viettel.bvrhm.nhahocduong.api.common.internal.utils;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author: longlb1
 * @since: 26-Sep-23
 */
public class ExcelUtil {
  //    public static void addCellsBorders(Workbook workbook, Sheet sheet, BorderStyle borderStyle,
  // int startCol, int startRow, int endCol, int endRow, ) {
  //        CellStyle cellStyle = workbook.createCellStyle();
  //        cellStyle.setBorderTop(borderStyle);
  //        cellStyle.setBorderBottom(borderStyle);
  //        cellStyle.setBorderLeft(borderStyle);
  //        cellStyle.setBorderRight(borderStyle);
  //
  //        for (int col = 0; col < numberOfCols; col++) {
  //            for (int row = 0; row < numberOfRows; row++) {
  //                Cell cell = sheet.getRow(row).getCell(col);
  //                if (cell == null) {
  //                    cell = sheet.getRow(row).createCell(col);
  //                }
  //                cell.setCellStyle(cellStyle);
  //            }
  //        }
  //    }

  public static void addStyleForCells(
      Sheet sheet, CellStyle cellStyle, int startCol, int endCol, int startRow, int endRow) {
    for (int col = startCol; col <= endCol; col++) {
      for (int row = startRow; row <= endRow; row++) {
        Cell cell = sheet.getRow(row).getCell(col);
        if (cell == null) {
          cell = sheet.getRow(row).createCell(col);
        }
        cell.setCellStyle(cellStyle);
      }
    }
  }

  public static void autoSizeColumns(Sheet sheet, int numberOfCols) {
    for (int col = 0; col < numberOfCols; col++) {
      sheet.autoSizeColumn(col);
    }
  }

  public static Sheet getSheetFromExcel(InputStream inputStream, String sheetName)
      throws IOException {
    Workbook workbook = new XSSFWorkbook(inputStream);
    return workbook.getSheet(sheetName);
  }

  public static Sheet getSheetFromExcel(InputStream inputStream, int index) throws IOException {
    Workbook workbook = new XSSFWorkbook(inputStream);
    return workbook.getSheetAt(index);
  }

  public static Object getCellValue(Cell cell) {
    CellType cellType = cell.getCellType();
    Object cellValue = null;
    switch (cellType) {
      case BOOLEAN:
        cellValue = cell.getBooleanCellValue();
        break;
      case FORMULA:
        Workbook workbook = cell.getSheet().getWorkbook();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        cellValue = evaluator.evaluate(cell).getNumberValue();
        break;
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          cellValue = cell.getDateCellValue();
        } else {
          cellValue = NumberToTextConverter.toText(cell.getNumericCellValue());
        }
        break;
      case STRING:
        cellValue = cell.getStringCellValue();
        break;
    }
    return cellValue;
  }
}
