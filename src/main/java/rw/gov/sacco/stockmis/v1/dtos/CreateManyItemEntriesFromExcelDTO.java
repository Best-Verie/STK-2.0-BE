package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class CreateManyItemEntriesFromExcelDTO {
    @NotEmpty
    private List<ItemEntryFromExcelDTO> itemEntries;
}
