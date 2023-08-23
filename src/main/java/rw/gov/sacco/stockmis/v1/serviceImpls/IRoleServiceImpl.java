package rw.gov.sacco.stockmis.v1.serviceImpls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rw.gov.sacco.stockmis.v1.models.Role;
import rw.gov.sacco.stockmis.v1.repositories.IRoleRepository;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateRoleDTO;

import java.util.List;
import java.util.UUID;

@Service
public class IRoleServiceImpl {
    private final IRoleRepository roleRepository;

    @Autowired
    public IRoleServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role addRole(CreateOrUpdateRoleDTO roleDTO) {
        Role role = new Role();
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        return roleRepository.save(role);
    }

    public Role getRoleById(UUID id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role updateRole(UUID id, CreateOrUpdateRoleDTO roleDTO) {
        Role existingRole = roleRepository.findById(id).orElse(null);
        if (existingRole != null) {
            existingRole.setName(roleDTO.getName());
            existingRole.setDescription(roleDTO.getDescription());
            return roleRepository.save(existingRole);
        } else {
            return null;
        }
    }

    public void deleteRole(UUID id) {
        roleRepository.deleteById(id);
    }
}

