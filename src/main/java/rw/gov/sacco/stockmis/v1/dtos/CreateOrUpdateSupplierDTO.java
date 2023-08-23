package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Data
public class CreateOrUpdateSupplierDTO {
    private String name;
    private String address;
    private String email;
    private String phone;
    private String tinNumber;
    private Integer deliveryTerms;
    private Integer warrantyPeriod;
    private List<UUID> itemsSupplied;

}
