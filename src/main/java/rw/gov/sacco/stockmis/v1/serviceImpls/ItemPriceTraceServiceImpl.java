package rw.gov.sacco.stockmis.v1.serviceImpls;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rw.gov.sacco.stockmis.v1.exceptions.BadRequestException;
import rw.gov.sacco.stockmis.v1.models.Item;
import rw.gov.sacco.stockmis.v1.models.ItempriceTrace;
import rw.gov.sacco.stockmis.v1.repositories.IItemPriceTraceRepository;
import rw.gov.sacco.stockmis.v1.repositories.IItemRepository;
import rw.gov.sacco.stockmis.v1.services.IItemPriceTraceService;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ItemPriceTraceServiceImpl implements IItemPriceTraceService {
    private final IItemPriceTraceRepository itemPriceTraceRepository;

    private final IItemRepository itemRepository;


    @Autowired
    public ItemPriceTraceServiceImpl(IItemPriceTraceRepository itemPriceTraceRepository, IItemRepository itemRepository) {
        this.itemPriceTraceRepository = itemPriceTraceRepository;
        this.itemRepository = itemRepository;
    }


    @Override
    public void generatePriceReport(UUID itemId, LocalDate startDate, LocalDate endDate) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Item Price Report");

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
            titleRow.createCell(3).setCellValue("Item Price Report: " + startDate + " - " + endDate);
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(font);
            titleRow.getCell(3).setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(10, 10, 0, 10));

            CellStyle borderStyle = workbook.createCellStyle();
            setBorders(borderStyle);

            CellStyle titleBorderStyle = workbook.createCellStyle();
            setBorders(titleBorderStyle);
            titleBorderStyle.setFont(fontTitle);

            //data codes


            List<ItempriceTrace> priceTraces = this.itemPriceTraceRepository.findByItemIdAndDateBetweenOrderByIdAsc(itemId,startDate,endDate);

            int rowNum = 12;
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Item");
            headerRow.createCell(2).setCellValue("Activity");
            headerRow.createCell(3).setCellValue("Quantity");
            headerRow.createCell(4).setCellValue("Unit Price");
            headerRow.createCell(5).setCellValue("Stock Value");
            headerRow.createCell(6).setCellValue("Date");

            for (int i = 0; i < 7; i++) {
                headerRow.getCell(i).setCellStyle(titleBorderStyle);
            }
            for (int i = 0; i < 7; i++) {
                sheet.setColumnWidth(i, 3000); // Increase column width of cells in headerRow
            }

            sheet.setColumnWidth(1,8000);
            Optional<Item> item = this.itemRepository.findById(itemId);

            for (ItempriceTrace priceTrace : priceTraces) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(priceTrace.getId());
                row.createCell(1).setCellValue(item.get().getName());
                row.createCell(2).setCellValue(priceTrace.getActivity());
                row.createCell(3).setCellValue(priceTrace.getQuantity());
                row.createCell(4).setCellValue(priceTrace.getUnitPrice());
                row.createCell(5).setCellValue(priceTrace.getStockValue());
                row.createCell(6).setCellValue(String.valueOf(priceTrace.getDate()));

                for (int i = 0; i < 7; i++) {
                    row.getCell(i).setCellStyle(borderStyle);
                }

            }



// Center-align the table horizontally
            CellStyle centerAlignStyle = workbook.createCellStyle();
            centerAlignStyle.setAlignment(HorizontalAlignment.CENTER);
            int lastRowNum = sheet.getLastRowNum();
            for (int i = rowNum; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    for (int j = 0; j < 7; j++) {
                        Cell cell = row.getCell(j);
                        if (cell != null) {
                            cell.setCellStyle(centerAlignStyle);
                        }
                    }
                }
            }

            // fileOutput stream codes
            String filePath = "price_report.xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }



        }catch(Exception e){
            System.out.println(e.getMessage());
            throw new BadRequestException("failed to generate report");
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
