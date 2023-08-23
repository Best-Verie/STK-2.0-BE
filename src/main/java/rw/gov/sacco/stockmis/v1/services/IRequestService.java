package rw.gov.sacco.stockmis.v1.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateRequestDTO;
import rw.gov.sacco.stockmis.v1.enums.ERequestStatus;
import rw.gov.sacco.stockmis.v1.models.Item;
import rw.gov.sacco.stockmis.v1.models.Request;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IRequestService {
    long count();
    long countBranchesWithPendingRequests();
    Request create(CreateOrUpdateRequestDTO requestDTO);
    Request findById(UUID id);
    boolean existsById(UUID id);
    Iterable<Request> findAll();
    Page<Request> findAll(Pageable pageable);
    Request update(UUID id, CreateOrUpdateRequestDTO requestDTO);
    boolean delete(UUID id);
    Page<Request> findAllByStatus(ERequestStatus status, Pageable pageable);
    Request changeStatus(UUID id, ERequestStatus status, Integer suggestedQuantity);
    Request reject(UUID id, String rejectionComment);
    Request updateRejectionComment(UUID id, String rejectionComment);
    Page<Request> findAllByBranchIdAnItemIdAndStatusAndCategoryId(Long branchId, UUID itemId, ERequestStatus status, UUID categoryId, Pageable pageable);

    List<Request> findAllByItemAndDateOfStatusChangeStoreKeeperGrantedBetween(Item item, LocalDate startDate, LocalDate endDate);
}
