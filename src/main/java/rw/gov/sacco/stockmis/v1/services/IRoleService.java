package rw.gov.sacco.stockmis.v1.services;

import rw.gov.sacco.stockmis.v1.enums.ERole;
import rw.gov.sacco.stockmis.v1.models.Role;

import java.util.Set;

public interface IRoleService {

    Role findByName(ERole role);

    Set<Role> getRoleInaHashSet(ERole role);
}
