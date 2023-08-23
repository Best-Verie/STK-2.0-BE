package rw.gov.sacco.stockmis.v1.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import rw.gov.sacco.stockmis.v1.enums.EGender;
import rw.gov.sacco.stockmis.v1.enums.ERole;
import rw.gov.sacco.stockmis.v1.security.ValidPassword;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Data
@AllArgsConstructor
public class SignUpDTO {

    @Email
    private  String email;

    @NotBlank
    private  String firstName;

    @NotBlank
    private  String lastName;

    @NotBlank
    @Pattern(regexp = "[0-9]{9,12}", message = "Your phone is not a valid tel we expect 2507***, or 07*** or 7***")
    private  String mobile;

    private EGender gender;

//    @JsonIgnore
//    ERoleWrapper roleWrapper = new ERoleWrapper();
//    private ERole[] role = roleWrapper.getAllowedRoles();
    private ERole role;

    @ValidPassword
    private  String password;

    private Long branchId;

    public SignUpDTO(){}

}
