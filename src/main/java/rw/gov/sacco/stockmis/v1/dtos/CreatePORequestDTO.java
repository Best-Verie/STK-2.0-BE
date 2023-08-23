package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;
import rw.gov.sacco.stockmis.v1.enums.EEntryType;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class CreatePORequestDTO {
    private UUID supplierId;

    private String directPurchaseSupplier;

    @NotNull
    private EEntryType entryType;

    private List<CreateOrUpdateAtomicPORequestDTO> atomicPORequests;
}



