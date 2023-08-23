package rw.gov.sacco.stockmis.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateSupplierDTO;
import rw.gov.sacco.stockmis.v1.payload.ApiResponse;
import rw.gov.sacco.stockmis.v1.services.ISupplierService;
import rw.gov.sacco.stockmis.v1.utils.Constants;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    private final ISupplierService supplierService;

    @Autowired
    public SupplierController(ISupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','STORE_KEEPER','HO_HR','APPROVER')")
    @GetMapping("/count")
    public ResponseEntity<?> count() {
        return ResponseEntity.ok(ApiResponse.success(supplierService.count()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all/paginated")
    public ResponseEntity<ApiResponse> getPaginated(
    @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
    @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
    @RequestParam(value = "name", required = false) String name,
    @RequestParam(value = "tin", required = false) String tinNumber,
    @RequestParam(value = "phone", required = false) String phoneNumber
    ) {
        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.ASC, "createdAt");
        return ResponseEntity.ok(ApiResponse.success(supplierService.findAll(name, tinNumber, phoneNumber, pageable)));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(supplierService.findById(id)));
    }

    //    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody CreateOrUpdateSupplierDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(supplierService.create(dto)));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateSupplier(@PathVariable UUID id, @Valid @RequestBody CreateOrUpdateSupplierDTO dto) {
        try {
            return ResponseEntity.ok(ApiResponse.success(supplierService.update(id, dto)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail(e.toString()));
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(supplierService.delete(id)));
    }

}

