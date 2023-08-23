package rw.gov.sacco.stockmis.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.sacco.stockmis.v1.models.AtomicPORequest;

import java.util.UUID;

public interface IAtomicPORequestRepository extends JpaRepository<AtomicPORequest, UUID> {

}
