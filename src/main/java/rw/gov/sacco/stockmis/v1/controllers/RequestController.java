package rw.gov.sacco.stockmis.v1.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateRequestDTO;
import rw.gov.sacco.stockmis.v1.enums.ERequestStatus;
import rw.gov.sacco.stockmis.v1.enums.ERole;
import rw.gov.sacco.stockmis.v1.models.User;
import rw.gov.sacco.stockmis.v1.payload.ApiResponse;
import rw.gov.sacco.stockmis.v1.services.IRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rw.gov.sacco.stockmis.v1.services.IUserService;
import rw.gov.sacco.stockmis.v1.utils.Constants;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {
    private final IRequestService requestService;

    private final IUserService userService;

    @Autowired
    public RequestController(IRequestService requestService, IUserService userService) {
        this.requestService = requestService;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyAuthority('STORE_KEEPER')")
    @GetMapping("/count")
    public ResponseEntity<?> count() {
        return ResponseEntity.ok(ApiResponse.success(requestService.count()));
    }

    @PreAuthorize("hasAnyAuthority('STORE_KEEPER')")
    @GetMapping("/count/branches")
    public ResponseEntity<?> countBranchesWithPendingRequests() {
        return ResponseEntity.ok(ApiResponse.success(requestService.countBranchesWithPendingRequests()));
    }
    @GetMapping("/all/paginated")
    public ResponseEntity<ApiResponse> getPaginated(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
            @RequestParam(value = "branch", required = false) Long branchId,
            @RequestParam(value = "item", required = false) UUID itemId,
            @RequestParam(value = "status", required = false) ERequestStatus status,
            @RequestParam(value = "category", required = false) UUID categoryId
            ) {
        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.ASC, "createdAt");

        // get logged in user
        User user = userService.getLoggedInUser();

        // if branchId and ItemId and status and categoryId are null, return all
        if (branchId == null && itemId == null && status == null && categoryId == null) {
            return ResponseEntity.ok(ApiResponse.success(requestService.findAll(pageable)));
        } else {
            // if logged user's role is not STORE_KEEPER or HoHR throw exception
            if (userService.getLoggedInUser().getRole() != ERole.STORE_KEEPER && userService.getLoggedInUser().getRole() != ERole.HO_HR) {
                return ResponseEntity.ok(ApiResponse.success(requestService.findAllByBranchIdAnItemIdAndStatusAndCategoryId(user.getBranch().getId(), itemId, status, categoryId, pageable)));
            }
            return ResponseEntity.ok(ApiResponse.success(requestService.findAllByBranchIdAnItemIdAndStatusAndCategoryId(branchId, itemId, status, categoryId, pageable)));
        }
    }

    @PreAuthorize("hasAnyAuthority('STORE_KEEPER','HO_HR', 'APPROVER', 'INITIATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(requestService.findById(id)));
    }

    @PreAuthorize("hasAnyAuthority('INITIATOR')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody CreateOrUpdateRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(requestService.create(dto)));
    }

    @PreAuthorize("hasAnyAuthority('INITIATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id, @Valid @RequestBody CreateOrUpdateRequestDTO dto) {
        try {
            return ResponseEntity.ok(ApiResponse.success(requestService.update(id, dto)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail(e.toString()));
        }
    }

    @PreAuthorize("hasAnyAuthority('STORE_KEEPER','HO_HR', 'APPROVER')")
    @PutMapping("/{id}/rejection-comment")
    public ResponseEntity<ApiResponse> updateRejectionComment(@PathVariable UUID id, @Valid @RequestBody String newRejectionComment) {
        try {
            return ResponseEntity.ok(ApiResponse.success(requestService.updateRejectionComment(id, newRejectionComment)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail(e.toString()));
        }
    }

    @PreAuthorize("hasAnyAuthority('INITIATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(requestService.delete(id)));
    }

    @PreAuthorize("hasAnyAuthority('STORE_KEEPER','HO_HR', 'APPROVER')")
    @GetMapping("/status/{status}/paginated")
    public ResponseEntity<ApiResponse> findAllByStatus(@PathVariable ERequestStatus status,
                                                       @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                                                       @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.ASC, "createdAt");
        return ResponseEntity.ok(ApiResponse.success(requestService.findAllByStatus(status, pageable)));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<ApiResponse> changeStatus(@PathVariable UUID id, @Valid @RequestBody ERequestStatus status, @RequestParam(value = "suggested grant", required = false) Integer suggestedGrant) {
        requestService.changeStatus(id, status, suggestedGrant);
        return  ResponseEntity.ok(ApiResponse.success("The status has been updated"));
    }


    @PutMapping("/reject/{id}")
    public ResponseEntity<ApiResponse> reject(@PathVariable UUID id, @Valid @RequestBody String reason) {
        requestService.reject(id, reason);
        return  ResponseEntity.ok(ApiResponse.success("The request has been rejected"));
    }

}
