package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;
import rw.gov.sacco.stockmis.v1.security.ValidPassword;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePasswordDTO {
    @NotBlank
    private String currentPassword;

    @NotBlank
    @ValidPassword
    private String newPassword;
}
