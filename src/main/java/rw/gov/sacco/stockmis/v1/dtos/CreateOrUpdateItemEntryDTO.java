package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;
import rw.gov.sacco.stockmis.v1.enums.EEntryType;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateOrUpdateItemEntryDTO {
    @NotNull
    private UUID itemId;
    @NotNull
    private Integer quantity;
    @NotNull
    private EEntryType entryType;
    private UUID supplierId;
    private String directPurchaseSupplier;
    private Double directPurchasePrice;
    @NotNull
    private LocalDate dateOfPurchase;


}
