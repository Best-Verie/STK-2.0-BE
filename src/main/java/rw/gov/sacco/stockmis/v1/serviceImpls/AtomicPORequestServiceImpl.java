package rw.gov.sacco.stockmis.v1.serviceImpls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateAtomicPORequestDTO;
import rw.gov.sacco.stockmis.v1.enums.EPORequestStatus;
import rw.gov.sacco.stockmis.v1.exceptions.ResourceNotFoundException;
import rw.gov.sacco.stockmis.v1.models.AtomicPORequest;
import rw.gov.sacco.stockmis.v1.models.Item;
import rw.gov.sacco.stockmis.v1.models.PORequest;
import rw.gov.sacco.stockmis.v1.repositories.IAtomicPORequestRepository;
import rw.gov.sacco.stockmis.v1.services.IAtomicPORequestService;
import rw.gov.sacco.stockmis.v1.services.IItemService;
import rw.gov.sacco.stockmis.v1.services.IPORequestService;

import java.util.UUID;

@Service
public class AtomicPORequestServiceImpl implements IAtomicPORequestService {

    private final IAtomicPORequestRepository atomicPORequestRepository;
    private final IItemService itemService;

    private final IPORequestService poRequestService;


    @Autowired
    public AtomicPORequestServiceImpl(IAtomicPORequestRepository atomicPORequestRepository, IItemService itemService, @Lazy IPORequestService poRequestService) {
        this.atomicPORequestRepository = atomicPORequestRepository;
        this.itemService = itemService;
        this.poRequestService = poRequestService;
    }

    @Override
    public AtomicPORequest create(CreateOrUpdateAtomicPORequestDTO atomicPORequestDTO, UUID poRequestId) {
        //find PORequest
        PORequest poRequest = poRequestService.findById(poRequestId);
        AtomicPORequest atomicPORequest;

        if(atomicPORequestDTO.getItemId() != null){
            Item item = itemService.findById(atomicPORequestDTO.getItemId());
            atomicPORequest = new AtomicPORequest(item, atomicPORequestDTO.getQuantity(), atomicPORequestDTO.getDescription(), poRequest, item.getPrice());
        } else {
            atomicPORequest = new AtomicPORequest(atomicPORequestDTO.getDirectPurchaseItem(), atomicPORequestDTO.getQuantity(), atomicPORequestDTO.getDescription(), poRequest, atomicPORequestDTO.getDirectPurchasePrice());
        }

        return  atomicPORequestRepository.save(atomicPORequest);
    }

    @Override
    public AtomicPORequest findById(UUID id) {
        return atomicPORequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Atomic PO Request", "id", id.toString()));
    }

    @Override
    public boolean existsById(UUID id) {
        return atomicPORequestRepository.existsById(id);
    }

    @Override
    public AtomicPORequest update(UUID id, CreateOrUpdateAtomicPORequestDTO atomicPORequestDTO) {
        AtomicPORequest atomicPORequest = findById(id);

        //find item

        if(atomicPORequestDTO.getItemId() != null){
            Item item = itemService.findById(atomicPORequestDTO.getItemId());
            atomicPORequest.setItem(item);
            atomicPORequest.setPrice(item.getPrice());
        }else {
            atomicPORequest.setDirectPurchaseItem(atomicPORequestDTO.getDirectPurchaseItem());
            atomicPORequest.setItem(null);
            atomicPORequest.setPrice(atomicPORequestDTO.getDirectPurchasePrice());
        }
        //update atomic po request

        atomicPORequest.setQuantity(atomicPORequestDTO.getQuantity());
        atomicPORequest.setDescription(atomicPORequestDTO.getDescription());

        return atomicPORequestRepository.save(atomicPORequest);
    }

    @Override
    public boolean delete(UUID id) {
        AtomicPORequest atomicPORequest = findById(id);

        //check if the po request which it belongs is not approved
        if(atomicPORequest.getPoRequest().getStatus() != EPORequestStatus.PENDING){
            throw new RuntimeException("Cannot delete an atomic po request which belongs to a po request which is not pending");
        }

        atomicPORequestRepository.delete(atomicPORequest);
        return true;
    }
}
