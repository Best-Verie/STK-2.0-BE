package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateOrUpdateItemSubCategoryDTO {
    //code for the class
    private String name;

    private UUID itemCategoryId;

}
