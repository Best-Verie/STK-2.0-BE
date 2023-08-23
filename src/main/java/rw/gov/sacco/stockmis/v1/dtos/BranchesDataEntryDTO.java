package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BranchesDataEntryDTO {
    private List<Map<String, String>> data;
}
