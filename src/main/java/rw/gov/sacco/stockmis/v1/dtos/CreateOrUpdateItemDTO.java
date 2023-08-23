package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;
import rw.gov.sacco.stockmis.v1.enums.EItemType;


import javax.validation.constraints.NotNull;
import java.util.UUID;
@Data
public class CreateOrUpdateItemDTO {
    @NotNull
    private String name;
    @NotNull
    private double price;
    @NotNull
    private int overstockParameter;
    @NotNull
    private int understockParameter;
    @NotNull
    private int inStockDuration;
    @NotNull
    private UUID itemCategoryId;
    @NotNull
    private EItemType itemType;
}
