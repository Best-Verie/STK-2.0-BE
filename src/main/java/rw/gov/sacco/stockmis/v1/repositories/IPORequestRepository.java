package rw.gov.sacco.stockmis.v1.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rw.gov.sacco.stockmis.v1.enums.EPORequestStatus;
import rw.gov.sacco.stockmis.v1.models.PORequest;

import java.util.UUID;

public interface IPORequestRepository extends JpaRepository<PORequest, UUID> {
    @Query("SELECT MAX(p.poNumber) FROM PORequest p")
    Long findMaxPoNumber();

    Long countByStatus(EPORequestStatus status);

    Page<PORequest> findAllByStatus(Pageable pageable, EPORequestStatus pending);
}

