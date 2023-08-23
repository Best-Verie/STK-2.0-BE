package rw.gov.sacco.stockmis.v1.serviceImpls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rw.gov.sacco.stockmis.v1.enums.ERole;
import rw.gov.sacco.stockmis.v1.exceptions.ResourceNotFoundException;
import rw.gov.sacco.stockmis.v1.models.Role;
import rw.gov.sacco.stockmis.v1.repositories.IRoleRepository;
import rw.gov.sacco.stockmis.v1.services.IRoleService;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleServiceImpl implements IRoleService {
    private final IRoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(IRoleRepository iRoleRepository){
        this.roleRepository = iRoleRepository;
    }

    @Override
    public Role findByName(ERole role) {
        return roleRepository.findByName(role).orElseThrow(() -> new ResourceNotFoundException("Role", "name", role.toString()));
    }

    @Override
    public Set<Role> getRoleInaHashSet(ERole role) {
        Set<Role> roles = new HashSet<>();
        roles.add(this.findByName(role));

        return roles;
    }
}
