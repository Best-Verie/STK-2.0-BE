package rw.gov.sacco.stockmis.v1.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

@Data
public class RejectManyUsersDTO {

    @NotEmpty
    List<UUID> usersIds;

    @NotBlank
    String rejectionMessage;
}
