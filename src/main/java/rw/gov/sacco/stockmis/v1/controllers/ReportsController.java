package rw.gov.sacco.stockmis.v1.controllers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.gov.sacco.stockmis.v1.dtos.ReportRecordDTO;
import rw.gov.sacco.stockmis.v1.exceptions.BadRequestException;
import rw.gov.sacco.stockmis.v1.models.Item;
import rw.gov.sacco.stockmis.v1.models.ItemCategory;
import rw.gov.sacco.stockmis.v1.models.ItemRecord;
import rw.gov.sacco.stockmis.v1.payload.ApiResponse;
import rw.gov.sacco.stockmis.v1.repositories.IItemCategoryRepository;
import rw.gov.sacco.stockmis.v1.repositories.IItemRecordRepository;
import rw.gov.sacco.stockmis.v1.repositories.IItemRepository;
import rw.gov.sacco.stockmis.v1.services.IItemService;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportsController {

    private final IItemRecordRepository itemRecordRepository;
    private final IItemCategoryRepository itemCategoryRepository;
    private final IItemService itemService;

    private final IItemRepository itemRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ReportsController(IItemRecordRepository itemRecordRepository, IItemRepository itemRepository, IItemService itemService, IItemCategoryRepository itemCategoryRepository) {
        this.itemRecordRepository = itemRecordRepository;
        this.itemService = itemService;
        this.itemCategoryRepository = itemCategoryRepository;
        this.itemRepository = itemRepository;
    }

    @PostMapping("/reporting/force/records")
    public ResponseEntity<ApiResponse> forceCreateRecordsInDB(@RequestParam("date") String date) {
        if(itemService.forceSaveItemRecord(LocalDate.parse(date))) {
            return ResponseEntity.ok(new ApiResponse(true, "Item record saved successfully"));
        }
        return ResponseEntity.ok(new ApiResponse(false, "Item record not saved"));
    }


    @PreAuthorize("hasAnyAuthority('STORE_KEEPER')")
    @GetMapping("/reporting/items")
    public void downloadItemReports(HttpServletResponse response,
                                           @RequestParam("startMonth") String startMonth,
                                           @RequestParam("endMonth") String endMonth,
                                           @RequestParam("year") Year year) {
        makeOverallReport(response, startMonth, endMonth, year);
    }


    private void makeOverallReport(HttpServletResponse response, String startMonth, String endMonth, Year year){
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Stock Report");

            /* VALIDATIONS */

            if (!itemService.isValidMonth(startMonth) || !itemService.isValidMonth(endMonth)) {
                throw new BadRequestException("Invalid month name");
            }

            if (Month.valueOf(endMonth.toUpperCase()).getValue() < (Month.valueOf(startMonth.toUpperCase())).getValue()) {
                throw new BadRequestException("End month cannot come before start month");
            }

            String startMonthWithYear = "1st "+ startMonth + " " + year.getValue();

            int lastDayOfTheMonth = itemService.getLastDayOfMonth(endMonth);
            String endMonthWithYear = lastDayOfTheMonth+"th "+ endMonth + " " + year.getValue();


            /**** DEALING WITH DATES ****/

            // Parse the startMonth and endMonth strings to Month enum (e.g., JANUARY, FEBRUARY, etc.)
            Month startMonthEnum = Month.valueOf(startMonth.toUpperCase());
            Month endMonthEnum = Month.valueOf(endMonth.toUpperCase());

            // Get the first day of the startMonth
            LocalDate startDate = Year.of(year.getValue()).atMonth(startMonthEnum).atDay(1);

            // Get the last day of the endMonth
            LocalDate endDate = Year.of(year.getValue()).atMonth(endMonthEnum).atEndOfMonth();

            if(year.getValue()<2023 || year.getValue() > Integer.valueOf(String.valueOf(Year.now()))){
                throw  new BadRequestException("No report found for the selected year!");
            }

            Month currentMonth = Month.from(LocalDate.now());


            if (startMonthEnum.getValue() > currentMonth.getValue() ||
                    endMonthEnum.getValue() > currentMonth.getValue()) {
                throw new BadRequestException("No report found for the selected period");
            }

            System.out.println("Start Date" + startDate);
            System.out.println("End date" + endDate);

            /* EXCEL CODE */

            Font font = workbook.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 24);

            Font fontname = workbook.createFont();
            fontname.setBold(true);
            fontname.setFontHeightInPoints((short) 18);

            Font fontTitle = workbook.createFont();
            fontTitle.setBold(true);
            fontTitle.setFontHeightInPoints((short) 11);

            CellStyle titleTableStyle = workbook.createCellStyle();
            titleTableStyle.setFont(fontTitle);

            InputStream is = this.getClass().getClassLoader().getResourceAsStream("public/logo.png");
            byte[] bytes = IOUtils.toByteArray(is);
            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            is.close();

            CreationHelper helper = workbook.getCreationHelper();
            Drawing drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = helper.createClientAnchor();

            anchor.setCol1(0);
            anchor.setRow1(0);
            Picture picture = drawing.createPicture(anchor, pictureIdx);
            picture.resize(4);

            Row titleRow = sheet.createRow(5);
            titleRow.createCell(3).setCellValue("Stock Report: " + startMonthWithYear + " - " + endMonthWithYear);
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(font);
            titleRow.getCell(3).setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 10));

            List<ItemCategory> itemCategories = itemCategoryRepository.findAll();

            CellStyle borderStyle = workbook.createCellStyle();
            setBorders(borderStyle);

            CellStyle titleBorderStyle = workbook.createCellStyle();
            setBorders(titleBorderStyle);
            titleBorderStyle.setFont(fontTitle);

            int rowIdx = 8;
            int rowNum = 1;
            Boolean report = false;


            // Loop through each category
            for (ItemCategory itemCategory : itemCategories) {
                System.out.println(
                        "categoryIds" + itemCategory.getId()
                );

                UUID itemCat = itemCategory.getId();
                Optional<List<ReportRecordDTO>> optionalReportRecords = Optional.ofNullable(
                        jdbcTemplate.query(
                                "SELECT * FROM generate_monthly_items_report(?::date, ?::date) WHERE categoryid = ?",
                                new Object[]{startDate, endDate, itemCat},
                                (rs, rowNumber) -> {
                                    ReportRecordDTO reportRecord = new ReportRecordDTO();
                                    UUID itemId = (UUID) rs.getObject("itemid");
                                    System.out.println("ItemId: " + itemId); // Add this line for debugging
                                    String itemName = null;
                                    if (itemId != null) {
                                        Optional<Item> itemOptional = this.itemRepository.findById(itemId);
                                        System.out.println("ItemRecord found: " + itemOptional.isPresent()); // Add this line for debugging
                                        itemName = itemOptional.map(item -> item.getName()).orElse(null);
                                    }
                                    reportRecord.setItemName(itemName);
                                    reportRecord.setOpeningBalance(rs.wasNull() ? 0 : rs.getInt("opening_balance"));
                                    reportRecord.setEntrees(rs.wasNull() ? 0 : rs.getInt("entries"));
                                    reportRecord.setBalanceQuantity(rs.wasNull() ? 0: rs.getInt("balance_quantity"));
                                    reportRecord.setStockOut(rs.wasNull() ? 0 : rs.getInt("stock_out"));
                                    reportRecord.setClosingBalance(rs.wasNull() ? 0 : rs.getInt("closing_balance"));
                                    reportRecord.setUnitPrice(rs.wasNull() ? 0.0 : rs.getDouble("unitprice"));
                                    reportRecord.setAmountIn(rs.wasNull() ? 0.0 : rs.getDouble("amount_in"));
                                    reportRecord.setAmountOut(rs.wasNull() ? 0.0 : rs.getDouble("amount_out"));
                                    reportRecord.setTotalValueInStock(rs.wasNull() ? 0.0 : rs.getDouble("total_value_in_stock"));
                                    return reportRecord;

                                }
                        )
                );

                List<ReportRecordDTO> reportRecords = optionalReportRecords.orElse(new ArrayList<>());

                if (reportRecords.isEmpty()) {
                    // Handle the case when there are no records for the specified date range
                    // For example, you can add a message to the Excel file indicating no records found
                    if(rowIdx!=8){
                        sheet.createRow(rowIdx++);
                        sheet.createRow(rowIdx++);
                    }
                    Row categoryRow = sheet.createRow(rowIdx++);
                    categoryRow.createCell(0).setCellValue(itemCategory.getName());
                    CellStyle rowNameStyle = workbook.createCellStyle();
                    rowNameStyle.setFont(fontname);
                    categoryRow.getCell(0).setCellStyle(rowNameStyle);
                    sheet.addMergedRegion(new CellRangeAddress(rowIdx-1, rowIdx-1, 0, 10));

                    Row noRecordsRow = sheet.createRow(rowIdx++);

                    noRecordsRow.createCell(0).setCellValue("No");
                    noRecordsRow.createCell(1).setCellValue("Item Name");
                    noRecordsRow.createCell(2).setCellValue("Opening Balance");
                    noRecordsRow.createCell(3).setCellValue("Entrees");
                    noRecordsRow.createCell(4).setCellValue("Balance Quantity");
                    noRecordsRow.createCell(5).setCellValue("Stock Out");
                    noRecordsRow.createCell(6).setCellValue("Closing Stock-In");
                    noRecordsRow.createCell(7).setCellValue("Unit Price");
                    noRecordsRow.createCell(8).setCellValue("Stock-In value");
                    noRecordsRow.createCell(9).setCellValue("Stock-Out value");
                    noRecordsRow.createCell(10).setCellValue("Total Value in Stock");

                    for (int i = 0; i < 11; i++) {
                        noRecordsRow.getCell(i).setCellStyle(titleBorderStyle);
                    }

                } else {
                    System.out.println("==== Reached here  1 ====");
                    //print category name
                    if(rowIdx!=8){
                        sheet.createRow(rowIdx++);
                        sheet.createRow(rowIdx++);
                    }
                    Row categoryRow = sheet.createRow(rowIdx++);
                    categoryRow.createCell(0).setCellValue(itemCategory.getName());
                    CellStyle rowNameStyle = workbook.createCellStyle();
                    rowNameStyle.setFont(fontname);
                    categoryRow.getCell(0).setCellStyle(rowNameStyle);
                    sheet.addMergedRegion(new CellRangeAddress(rowIdx-1, rowIdx-1, 0, 10));

                    Row headersRow = sheet.createRow(rowIdx++);
                    headersRow.createCell(0).setCellValue("No");
                    headersRow.createCell(1).setCellValue("Item Name");
                    headersRow.createCell(2).setCellValue("Opening Balance");
                    headersRow.createCell(3).setCellValue("Entrees");
                    headersRow.createCell(4).setCellValue("Balance Quantity");
                    headersRow.createCell(5).setCellValue("Stock Out");
                    headersRow.createCell(6).setCellValue("Closing Stock-In");
                    headersRow.createCell(7).setCellValue("Unit Price");
                    headersRow.createCell(8).setCellValue("Stock-In value");
                    headersRow.createCell(9).setCellValue("Stock-Out value");
                    headersRow.createCell(10).setCellValue("Total Value in Stock");

                    for (int i = 0; i < 11; i++) {
                        headersRow.getCell(i).setCellStyle(titleBorderStyle);
                    }

                    for (ReportRecordDTO reportRecord : reportRecords) {
                        System.out.println("==== Reached here  2 ====");

                        Row row = sheet.createRow(rowIdx++);
                        row.createCell(0).setCellValue(rowNum++);
                        row.createCell(1).setCellValue(reportRecord.getItemName());
                        row.createCell(2).setCellValue(reportRecord.getOpeningBalance());
                        row.createCell(3).setCellValue(reportRecord.getEntrees());
                        row.createCell(4).setCellValue(reportRecord.getBalanceQuantity());
                        row.createCell(5).setCellValue(reportRecord.getStockOut());
                        row.createCell(6).setCellValue(reportRecord.getClosingBalance());
                        row.createCell(7).setCellValue(reportRecord.getUnitPrice());
                        row.createCell(8).setCellValue(reportRecord.getAmountIn());
                        row.createCell(9).setCellValue(reportRecord.getAmountOut());
                        row.createCell(10).setCellValue(reportRecord.getTotalValueInStock());

                        for (int i = 0; i < 11; i++) {
                            row.getCell(i).setCellStyle(borderStyle);
                        }
                    }
                }

            }


            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);
            sheet.autoSizeColumn(9);
            sheet.autoSizeColumn(10);


            workbook.write(out);
            InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));
            response.setContentType(MediaType.parseMediaType("application/vnd.ms-excel").toString());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monthly_item_report.xlsx");
            ServletOutputStream servletOutputStream = response.getOutputStream();
            workbook.write(servletOutputStream);
            workbook.close();
            servletOutputStream.close();


        } catch (Exception e) {
        e.printStackTrace(); // Print the full stack trace
        throw new BadRequestException("Failed to Make Your File: " + e.getMessage());
    }

}



    private void setBorders(CellStyle style) {
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
    }
}
