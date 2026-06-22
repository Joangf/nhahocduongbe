import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GenerateMockPatientExcel {
  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      throw new IllegalArgumentException("Usage: GenerateMockPatientExcel <template> <output>");
    }

    try (FileInputStream input = new FileInputStream(args[0]);
        XSSFWorkbook workbook = new XSSFWorkbook(input)) {
      Sheet sheet = workbook.getSheetAt(0);
      Row row = sheet.getRow(1);
      if (row == null) {
        row = sheet.createRow(1);
      }

      row.createCell(0).setCellValue(1);
      row.createCell(1).setCellValue("Nguyen Van Mock");

      CellStyle dateStyle = workbook.createCellStyle();
      CreationHelper helper = workbook.getCreationHelper();
      dateStyle.setDataFormat(helper.createDataFormat().getFormat("dd/MM/yyyy"));
      Date birthDate =
          Date.from(
              LocalDate.of(2015, 6, 18)
                  .atStartOfDay(ZoneId.systemDefault())
                  .toInstant());
      row.createCell(2).setCellValue(birthDate);
      row.getCell(2).setCellStyle(dateStyle);

      row.createCell(3).setCellValue(1);
      row.createCell(4).setCellValue("VL001");
      row.createCell(5).setCellValue("1A");
      row.createCell(6).setCellValue("Nong thon");
      row.createCell(7).setCellValue("MOCK-2026-0001");
      row.createCell(8).setCellValue("Kinh");
      row.createCell(9).setCellValue("MOCK-BHYT-0001");
      row.createCell(10).setCellValue("Nguyen Van Phu Huynh");

      try (FileOutputStream output = new FileOutputStream(args[1])) {
        workbook.write(output);
      }
    }
  }
}
