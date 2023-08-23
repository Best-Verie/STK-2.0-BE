package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;
import rw.gov.sacco.stockmis.v1.enums.ERole;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class SendUsersEmailDTO {

    @NotBlank
    private String subject;

    @NotBlank
    private String content;

    List<ERole> userTypes;
}
