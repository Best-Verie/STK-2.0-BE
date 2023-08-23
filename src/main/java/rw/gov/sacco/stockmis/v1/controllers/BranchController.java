package rw.gov.sacco.stockmis.v1.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateBranchDTO;
import rw.gov.sacco.stockmis.v1.payload.ApiResponse;
import rw.gov.sacco.stockmis.v1.services.IBranchService;
import rw.gov.sacco.stockmis.v1.utils.Constants;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/branches")
public class BranchController {
    private final IBranchService branchService;

    @Autowired
    public BranchController(IBranchService branchService) {
        this.branchService = branchService;
    }

//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all/paginated")
    public ResponseEntity<ApiResponse> getPaginated(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.ASC, "createdAt");
        return ResponseEntity.ok(ApiResponse.success(branchService.findAll(pageable)));
    }

//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(branchService.findById(id)));
    }

//    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody CreateOrUpdateBranchDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(branchService.create(dto)));
    }

//    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateBranch(@PathVariable Long id, @Valid @RequestBody CreateOrUpdateBranchDTO dto) {
        try {
            return ResponseEntity.ok(ApiResponse.success(branchService.update(id, dto)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail(e.toString()));
        }
    }

//    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(branchService.delete(id)));
    }
}