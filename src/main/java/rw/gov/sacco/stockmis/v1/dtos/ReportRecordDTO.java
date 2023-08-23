package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;

@Data
public class ReportRecordDTO {
    private String itemName;
    private Integer openingBalance;
    private Integer entrees;
    private Integer balanceQuantity;
    private Integer stockOut;
    private Integer closingBalance;
    private Double unitPrice;
    private Double amountIn;
    private Double amountOut;
    private Double totalValueInStock;

}
