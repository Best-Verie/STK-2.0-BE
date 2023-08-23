package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CheckActivationCode {

    @NotBlank
    private String email;

    @NotBlank
    private String activationCode;
}
