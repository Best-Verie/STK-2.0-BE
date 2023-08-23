package rw.gov.sacco.stockmis.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.gov.sacco.stockmis.v1.dtos.CreateManyItemEntriesFromExcelDTO;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateItemEntryDTO;
import rw.gov.sacco.stockmis.v1.payload.ApiResponse;
import rw.gov.sacco.stockmis.v1.services.IItemEntryService;
import rw.gov.sacco.stockmis.v1.utils.Constants;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/itemEntries")
public class ItemEntryController {
    private final IItemEntryService itemEntryService;

    @Autowired
    public ItemEntryController(IItemEntryService itemEntryService) {
        this.itemEntryService = itemEntryService;
    }

    @PreAuthorize("hasAnyAuthority('STORE_KEEPER')")
    @GetMapping("/all/paginated")
    public ResponseEntity<ApiResponse> getPaginated(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.ASC, "createdAt");
        return ResponseEntity.ok(ApiResponse.success(itemEntryService.findAll(pageable)));
    }

    @PreAuthorize("hasAnyAuthority('STORE_KEEPER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(itemEntryService.findById(id)));
    }

    @PreAuthorize("hasAnyAuthority('STORE_KEEPER')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody CreateOrUpdateItemEntryDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(itemEntryService.create(dto)));
    }

    @PreAuthorize("hasAnyAuthority('STORE_KEEPER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateItemEntry(@PathVariable UUID id, @Valid @RequestBody CreateOrUpdateItemEntryDTO dto, String reason) {
        try {
            //reason is required
            if (reason == null || reason.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.fail("Reason is required"));
            }
            return ResponseEntity.ok(ApiResponse.success(itemEntryService.update(id, dto, reason)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail(e.toString()));
        }
    }

    // register many item entries from excel
    @PreAuthorize("hasAnyAuthority('STORE_KEEPER')")
    @PostMapping("/registerMany")
    public ResponseEntity<ApiResponse> registerManyItemEntriesFromExcel(@Valid @RequestBody CreateManyItemEntriesFromExcelDTO dto) {

       itemEntryService.createManyItemEntriesFromExcel(dto);

       return ResponseEntity.ok(ApiResponse.success("Item entries registered successfully"));

    }


}

