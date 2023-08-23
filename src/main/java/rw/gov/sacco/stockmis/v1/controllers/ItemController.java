package rw.gov.sacco.stockmis.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateItemDTO;
import rw.gov.sacco.stockmis.v1.enums.EStockStatus;
import rw.gov.sacco.stockmis.v1.payload.ApiResponse;
import rw.gov.sacco.stockmis.v1.services.IItemService;
import rw.gov.sacco.stockmis.v1.utils.Constants;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {
    private final IItemService itemService;

    @Autowired
    public ItemController(IItemService itemService) {
        this.itemService = itemService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STORE_KEEPER','HO_HR','DIRECTOR_GENERAL', 'PO_APPROVER')")
    @GetMapping("/count")
    public ResponseEntity<?> count() {
        return ResponseEntity.ok(ApiResponse.success(itemService.count()));
    }

//    @PreAuthorize("hasAnyAuthority('ADMIN', 'STORE_KEEPER','HO_HR','DIRECTOR_GENERAL', 'PO_APPROVER')")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all/paginated")
    public ResponseEntity<ApiResponse> getPaginated(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
            @RequestParam(value = "category", required = false) UUID categoryId,
            @RequestParam(value = "status", required = false) EStockStatus status,
            @RequestParam(value = "name", required = false) String name
            ) {
        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.ASC, "createdAt");

        return ResponseEntity.ok(ApiResponse.success(itemService.findAll(categoryId, status,name, pageable)));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(itemService.findById(id)));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody CreateOrUpdateItemDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(itemService.create(dto)));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateItem(@PathVariable UUID id, @Valid @RequestBody CreateOrUpdateItemDTO dto, String reason) {
        try {
            if (reason == null || reason.isEmpty())
            {
                return ResponseEntity.badRequest().body(ApiResponse.fail("Reason is required"));
            }
            return ResponseEntity.ok(ApiResponse.success(itemService.update(id, dto, reason)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail(e.toString()));
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id, String reason) {
        if (reason == null || reason.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("Reason is required"));
        }
        return ResponseEntity.ok(ApiResponse.success(itemService.delete(id, reason)));
    }
}
