package rw.gov.sacco.stockmis.v1.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.gov.sacco.stockmis.v1.dtos.CreatePORequestDTO;
import rw.gov.sacco.stockmis.v1.enums.EPORequestStatus;
import rw.gov.sacco.stockmis.v1.models.PORequest;

import java.util.UUID;

public interface IPORequestService {
    long count(EPORequestStatus status);
    PORequest create(CreatePORequestDTO requestDTO);
    PORequest findById(UUID id);
    boolean existsById(UUID id);
    Iterable<PORequest> findAll();
    Page<PORequest> findAll(Pageable pageable);
    Page<PORequest> findAllByStatus(Pageable pageable, EPORequestStatus status);
    PORequest approve(UUID id);

    PORequest reject(UUID id, String rejectionComment);

    boolean delete(UUID id);
}
