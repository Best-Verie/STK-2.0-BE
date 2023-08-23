package rw.gov.sacco.stockmis.v1.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rw.gov.sacco.stockmis.v1.dtos.CreatePORequestDTO;
import rw.gov.sacco.stockmis.v1.enums.EPORequestStatus;
import rw.gov.sacco.stockmis.v1.models.PORequest;
import rw.gov.sacco.stockmis.v1.payload.ApiResponse;
import rw.gov.sacco.stockmis.v1.services.IPORequestService;
import rw.gov.sacco.stockmis.v1.utils.Constants;

import javax.validation.Valid;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/po-requests")
public class PORequestController {
    private final IPORequestService poRequestService;

    @Autowired
    public PORequestController(IPORequestService poRequestService) {
        this.poRequestService = poRequestService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'DIRECTOR_GENERAL', 'PO_APPROVER', 'PO_REQUEST','PROC_MANAGER')")
    @GetMapping("/count")
    public ResponseEntity<?> count(@RequestParam(value = "status") EPORequestStatus status) {
        return ResponseEntity.ok(ApiResponse.success(poRequestService.count(status)));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'DIRECTOR_GENERAL', 'PO_APPROVER', 'PO_REQUEST','PROC_MANAGER')")
    @GetMapping("/all/paginated")
    public ResponseEntity<ApiResponse> getPaginated(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
            @RequestParam(value = "status", required = false) EPORequestStatus status
            ) {

        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.ASC, "createdAt");
        if(status == null) {
            return ResponseEntity.ok(ApiResponse.success(poRequestService.findAll(pageable)));
        }
        return ResponseEntity.ok(ApiResponse.success(poRequestService.findAllByStatus(pageable,status)));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTOR_GENERAL', 'PO_APPROVER', 'PO_REQUEST','PROC_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable UUID id) {
        try {
            PORequest request = poRequestService.findById(id);
            return ResponseEntity.ok(ApiResponse.success(request));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail(e.toString()));
        }
    }

    @PreAuthorize("hasAnyAuthority('PO_REQUEST')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody CreatePORequestDTO requestDTO) {
        try {
            PORequest request = poRequestService.create(requestDTO);
            return ResponseEntity.ok(ApiResponse.success(request));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail(e.toString()));
        }
    }


    @PreAuthorize("hasAnyAuthority('PROC_MANAGER','DIRECTOR_GENERAL', 'PO_APPROVER', 'PO_REQUEST')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse> approve(@PathVariable UUID id) {
        try {
            PORequest request = poRequestService.approve(id);
            return ResponseEntity.ok(ApiResponse.success(request));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail(e.toString()));
        }
    }

    @PreAuthorize("hasAnyAuthority('PROC_MANAGER','DIRECTOR_GENERAL', 'PO_APPROVER', 'PO_REQUEST')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse> reject(@PathVariable UUID id, @RequestParam String rejectionComment) {
        try {
            PORequest request = poRequestService.reject(id, rejectionComment);
            return ResponseEntity.ok(ApiResponse.success(request));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail(e.toString()));
        }
    }

    @PreAuthorize("hasAnyAuthority('PO_REQUEST')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        try {
            poRequestService.delete(id);
            return ResponseEntity.ok(ApiResponse.success(true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail(e.toString()));
        }
    }
}
