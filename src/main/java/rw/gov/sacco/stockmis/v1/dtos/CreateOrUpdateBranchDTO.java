package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;

@Data
public class CreateOrUpdateBranchDTO {
    private String name;

    private String address;
}
