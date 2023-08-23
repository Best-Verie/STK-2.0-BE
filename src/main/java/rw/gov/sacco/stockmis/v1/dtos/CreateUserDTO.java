package rw.gov.sacco.stockmis.v1.dtos;

import lombok.*;
import rw.gov.sacco.stockmis.v1.security.ValidPassword;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class CreateUserDTO extends UpdateUserDTO{

    @NotBlank
    @ValidPassword
    private String password;
}
