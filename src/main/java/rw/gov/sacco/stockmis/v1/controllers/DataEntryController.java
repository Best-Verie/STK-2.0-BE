package rw.gov.sacco.stockmis.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.gov.sacco.stockmis.v1.dtos.BranchesDataEntryDTO;
import rw.gov.sacco.stockmis.v1.payload.ApiResponse;
import rw.gov.sacco.stockmis.v1.serviceImpls.IRoleServiceImpl;
import rw.gov.sacco.stockmis.v1.services.IBranchService;
import rw.gov.sacco.stockmis.v1.services.IItemService;
import rw.gov.sacco.stockmis.v1.services.IUserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/data-entry")
public class DataEntryController {

    private final IRoleServiceImpl roleService;

    private final IBranchService branchService;

    private final IUserService userService;

    private final IItemService itemService;

    @Autowired
    public DataEntryController(IRoleServiceImpl roleService, IBranchService branchService, IUserService userService, IItemService itemService) {
        this.roleService = roleService;
        this.branchService = branchService;
        this.userService = userService;
        this.itemService = itemService;
    }

    @PostMapping("/branches")
    public ResponseEntity<ApiResponse> createBranches(@Valid @RequestBody BranchesDataEntryDTO dto) {
        branchService.createManyBranches(dto);
        return ResponseEntity.ok(ApiResponse.success("Branches created successfully"));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse> createUsers(@Valid @RequestBody String JsonData) {
        userService.createManyUsers(JsonData);
        return ResponseEntity.ok(ApiResponse.success("Users created successfully"));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse> createItems(@Valid @RequestBody String JsonData, String itemCategoryName) {
        itemService.createManyItems(JsonData, itemCategoryName);
        return ResponseEntity.ok(ApiResponse.success("Items created successfully"));
    }



}
