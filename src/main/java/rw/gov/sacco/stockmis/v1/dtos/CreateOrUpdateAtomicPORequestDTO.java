package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;


import java.util.UUID;

@Data
public class CreateOrUpdateAtomicPORequestDTO {
    private UUID itemId;
    private int quantity;
    private String description;
    private String directPurchaseItem;
    private Double directPurchasePrice;
}
