package rw.gov.sacco.stockmis.v1.utils.projections;

import lombok.AllArgsConstructor;
import lombok.Data;
import rw.gov.sacco.stockmis.v1.enums.ERole;
import rw.gov.sacco.stockmis.v1.exceptions.BadRequestException;
import rw.gov.sacco.stockmis.v1.models.Role;
import rw.gov.sacco.stockmis.v1.models.User;

@Data
@AllArgsConstructor
public class Profile {
    Object profile;

    public User asUser() {
        return (User) profile;
    }

    public Role asRole() {
        return (Role) profile;
    }
    private void is(ERole role) {
        User user = (User) profile;
        if (user.getRole() != role)
            throw new BadRequestException("You must be a " + role.toString() + " to use this resource ...");
    }

}
