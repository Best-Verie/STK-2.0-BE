package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;
import rw.gov.sacco.stockmis.v1.enums.ERole;

@Data
public class CreateOrUpdateRoleDTO {

    private ERole name;

    private String description;
}
