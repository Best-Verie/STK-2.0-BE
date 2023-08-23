package rw.gov.sacco.stockmis.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateAtomicPORequestDTO;
import rw.gov.sacco.stockmis.v1.payload.ApiResponse;
import rw.gov.sacco.stockmis.v1.services.IAtomicPORequestService;

import javax.validation.Valid;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/atomic-po-requests")
public class AtomicPORequestController {

    private final IAtomicPORequestService  atomicPORequestService;

    @Autowired
    public AtomicPORequestController(IAtomicPORequestService atomicPORequestService) {
        this.atomicPORequestService = atomicPORequestService;
    }

    @PreAuthorize("hasAnyAuthority('PO_REQUEST')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateAtomicPORequest(@PathVariable UUID id, @Valid @RequestBody CreateOrUpdateAtomicPORequestDTO dto) {
        try{
            return ResponseEntity.ok(ApiResponse.success(atomicPORequestService.update(id, dto)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail(e.toString()));
        }
    }

    @PreAuthorize("hasAnyAuthority('PO_REQUEST')")
    @PostMapping()
    public ResponseEntity<ApiResponse> createAtomicPORequest(@Valid @RequestBody CreateOrUpdateAtomicPORequestDTO dto, @RequestParam(value = "poRequestId") UUID poRequestId) {
        try{
            return ResponseEntity.ok(ApiResponse.success(atomicPORequestService.create(dto, poRequestId)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail(e.toString()));
        }
    }

    //delete method
    @PreAuthorize("hasAnyAuthority('PO_REQUEST')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(atomicPORequestService.delete(id)));
    }
}
