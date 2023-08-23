package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;
import rw.gov.sacco.stockmis.v1.security.ValidPassword;

import javax.validation.constraints.NotBlank;

@Data
public class ResetPasswordDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String activationCode;

    @ValidPassword
    private String password;
}
