package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;

@Data
public class ItemEntryFromExcelDTO {
    private String itemName;
    private String quantity;
    private String entryType;
    private String supplierName;
    private String directPurchasePrice;
    private String dateOfPurchase;
}
