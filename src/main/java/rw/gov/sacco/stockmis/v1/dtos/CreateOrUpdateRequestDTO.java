package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CreateOrUpdateRequestDTO {

    @NotNull
    private UUID itemId;

    @NotNull
    private int quantity;

    @NotNull
    private int availableQuantity;
}

