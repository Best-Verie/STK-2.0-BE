package rw.gov.sacco.stockmis.v1.services;

import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateAtomicPORequestDTO;
import rw.gov.sacco.stockmis.v1.models.AtomicPORequest;

import java.util.UUID;

public interface IAtomicPORequestService {
    AtomicPORequest create(CreateOrUpdateAtomicPORequestDTO atomicPORequestDTO, UUID poRequestId);
    AtomicPORequest findById(UUID id);
    boolean existsById(UUID id);
    AtomicPORequest update(UUID id, CreateOrUpdateAtomicPORequestDTO atomicPORequestDTO);
    boolean delete(UUID id);

}
