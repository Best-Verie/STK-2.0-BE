package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;
import rw.gov.sacco.stockmis.v1.enums.EGender;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
public class UpdateUserDTO {
    @NotBlank
    private String firstName;

    @Email
    private String email;

    @NotNull
    private Long branchId;

    @NotBlank
    private String lastName;

    @NotBlank
    private String userName;

    @Pattern(regexp = "[0-9]{9,12}", message = "Your phone is not a valid tel we expect 2507***, or 07*** or 7***")
    private String phoneNumber;

    @NotNull
    private EGender gender;

}
