package rw.gov.sacco.stockmis.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import rw.gov.sacco.stockmis.v1.models.Item;
import rw.gov.sacco.stockmis.v1.services.IItemPriceTraceService;
import rw.gov.sacco.stockmis.v1.services.IItemService;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/price-report")
public class ItemPriceTraceController {
    private final IItemPriceTraceService itemPriceReportService;
    private final IItemService itemService;

    @Autowired
    public ItemPriceTraceController(IItemPriceTraceService itemPriceReportService, IItemService itemService) {
        this.itemPriceReportService = itemPriceReportService;
        this.itemService = itemService;
    }

    @PreAuthorize("hasAnyAuthority('STORE_KEEPER')")
    @GetMapping("/{itemId}/{startDate}/{endDate}")
    public ResponseEntity<InputStreamResource> generatePriceReport(
            @PathVariable UUID itemId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws IOException {
        itemPriceReportService.generatePriceReport(itemId, startDate, endDate);

        Item item = this.itemService.findById(itemId);

        // Define the file path of the generated Excel file
        String filePath = "price_report.xlsx";
        Path file = Paths.get(filePath);
        InputStreamResource resource;
        try {
            resource = new InputStreamResource(new FileInputStream(file.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

        // Set the content type and attachment disposition headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=price_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }


}
