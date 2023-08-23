package rw.gov.sacco.stockmis.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateItemCategoryDTO;
import rw.gov.sacco.stockmis.v1.payload.ApiResponse;
import rw.gov.sacco.stockmis.v1.services.IItemCategoryService;
import rw.gov.sacco.stockmis.v1.utils.Constants;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/item-categories")
public class ItemCategoryController {
    private final IItemCategoryService itemCategoryService;

    @Autowired
    public ItemCategoryController(IItemCategoryService itemCategoryService) {
        this.itemCategoryService = itemCategoryService;
    }

    // Get total number of item categories
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STORE_KEEPER','HO_HR','DIRECTOR_GENERAL', 'PO_APPROVER')")
    @GetMapping("/count")
    public ResponseEntity<?> count() {
        return ResponseEntity.ok(ApiResponse.success(itemCategoryService.count()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all/paginated")
    public ResponseEntity<ApiResponse> getPaginated(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.ASC, "createdAt");

        return ResponseEntity.ok(ApiResponse.success(this.itemCategoryService.findAll(pageable)));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(itemCategoryService.findById(id)));
    }

    //    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody CreateOrUpdateItemCategoryDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(this.itemCategoryService.create(dto)));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateItemCategory(@PathVariable UUID id, @Valid @RequestBody CreateOrUpdateItemCategoryDTO dto) {
        try {
            return ResponseEntity.ok(ApiResponse.success(this.itemCategoryService.update(id, dto)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail(e.toString()));
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(itemCategoryService.delete(id)));
    }
}
