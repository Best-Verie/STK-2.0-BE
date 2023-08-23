package rw.gov.sacco.stockmis.v1.utils;

import org.apache.poi.ss.usermodel.*;

public class ExcelReportingUtil {

    public static void createBoldCell(Workbook workbook, Row row, int i, String value) {
        Cell cell = row.createCell(i);
        cell.setCellValue(value);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        cell.setCellStyle(style);
    }
}
