package rw.gov.sacco.stockmis.v1.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SendEmailDTO {

    @NotBlank
    private  String subject;

    @NotBlank
    private  String nameOfRecipient;

    @NotBlank
    private  String emailOfRecipient;

    @NotBlank
    private  String content;
}
