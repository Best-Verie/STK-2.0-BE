package rw.gov.sacco.stockmis.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateRoleDTO;
import rw.gov.sacco.stockmis.v1.models.Role;
import rw.gov.sacco.stockmis.v1.serviceImpls.IRoleServiceImpl;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final IRoleServiceImpl roleService;

    @Autowired
    public RoleController(IRoleServiceImpl roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping
    public Role addRole(@RequestBody CreateOrUpdateRoleDTO roleDTO) {
        return roleService.addRole(roleDTO);
    }

    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable UUID id) {
        return roleService.getRoleById(id);
    }

    @PutMapping("/{id}")
    public Role updateRole(@PathVariable UUID id, @RequestBody CreateOrUpdateRoleDTO roleDTO) {
        return roleService.updateRole(id, roleDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
    }
}