package rw.gov.sacco.stockmis.v1.serviceImpls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateAtomicPORequestDTO;
import rw.gov.sacco.stockmis.v1.dtos.CreatePORequestDTO;
import rw.gov.sacco.stockmis.v1.enums.EEntryType;
import rw.gov.sacco.stockmis.v1.enums.EPORequestStatus;
import rw.gov.sacco.stockmis.v1.enums.ERole;
import rw.gov.sacco.stockmis.v1.models.PORequest;
import rw.gov.sacco.stockmis.v1.models.User;
import rw.gov.sacco.stockmis.v1.repositories.IPORequestRepository;
import rw.gov.sacco.stockmis.v1.services.IAtomicPORequestService;
import rw.gov.sacco.stockmis.v1.services.IPORequestService;
import rw.gov.sacco.stockmis.v1.services.ISupplierService;
import rw.gov.sacco.stockmis.v1.services.IUserService;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class PORequestServiceImpl implements IPORequestService {

    private IPORequestRepository poRequestRepository;
    private IAtomicPORequestService atomicPORequestService;
    private IUserService userService;
    private ISupplierService supplierService;

    @Autowired
    public PORequestServiceImpl(IPORequestRepository poRequestRepository, @Lazy IAtomicPORequestService atomicPORequestService, @Lazy IUserService userService, @Lazy ISupplierService supplierService) {
        this.poRequestRepository = poRequestRepository;
        this.atomicPORequestService = atomicPORequestService;
        this.userService = userService;
        this.supplierService = supplierService;
    }
    @Override
    public long count(EPORequestStatus status) {
        return poRequestRepository.countByStatus(status);
    }

    @Transactional
    @Override
    public PORequest create(CreatePORequestDTO requestDTO) {
        //create a new PORequest
        PORequest poRequest = new PORequest();

        Long poNumber = poRequestRepository.findMaxPoNumber();
        if (poNumber == null) {
            poNumber = 1L;
        }else {
            poNumber++;
        }

        poRequest.setPoNumber(poNumber);
        poRequest.setEntryType(requestDTO.getEntryType());

        if(requestDTO.getEntryType() == EEntryType.DIRECT_PURCHASE) {
            poRequest.setSupplier(null);
            poRequest.setDirectPurchaseSupplier(requestDTO.getDirectPurchaseSupplier());
        }else {
            poRequest.setSupplier(supplierService.findById(requestDTO.getSupplierId()));
            poRequest.setDirectPurchaseSupplier(null);
        }

        //save the PORequest
        poRequest = poRequestRepository.save(poRequest);

        for(CreateOrUpdateAtomicPORequestDTO atomicPORequest: requestDTO.getAtomicPORequests()){
            atomicPORequestService.create(atomicPORequest, poRequest.getId());
        }
        //find poRequest by id
        poRequest = poRequestRepository.findById(poRequest.getId()).get();


        return poRequest;
    }

    @Override
    public PORequest findById(UUID id) {
        return poRequestRepository.findById(id).get();
    }

    @Override
    public boolean existsById(UUID id) {
        return poRequestRepository.existsById(id);
    }

    @Override
    public Iterable<PORequest> findAll() {
        return poRequestRepository.findAll();
    }

    @Override
    public Page<PORequest> findAll(Pageable pageable) {
        return poRequestRepository.findAll(pageable);
    }

    @Override
    public Page<PORequest> findAllByStatus(Pageable pageable, EPORequestStatus status) {
        return poRequestRepository.findAllByStatus(pageable, status);
    }

    @Override
    public PORequest approve(UUID id) {
        //get logged in user
        User user = userService.getLoggedInUser();
        PORequest poRequest = poRequestRepository.findById(id).get();

        //if user is PROC_MANAGER
        if(user.getRole() == ERole.PROC_MANAGER){
            poRequest.setStatus(EPORequestStatus.PROC_MANAGER_APPROVAL);
            poRequest.setProcManager(user);
            poRequest.setDateOfStatusChangeHoHR(LocalDate.now());
        } else if(user.getRole() == ERole.PO_APPROVER){
            poRequest.setStatus(EPORequestStatus.APPROVER_APPROVAL);
            poRequest.setApprover(user);
            poRequest.setDateOfStatusChangeApprover(LocalDate.now());
        }  else if(user.getRole() == ERole.DIRECTOR_GENERAL){
            poRequest.setStatus(EPORequestStatus.DIRECTOR_GENERAL_APPROVAL);
            poRequest.setDirectorGeneral(user);
            poRequest.setDateOfStatusChangeDirectorGeneral(LocalDate.now());
        } else {
            throw new RuntimeException("You are not authorized to approve this request");
        }

        return poRequestRepository.save(poRequest);
    }

    @Override
    public PORequest reject(UUID id, String rejectionComment){
        //get logged in user
        User user = userService.getLoggedInUser();
        PORequest poRequest = poRequestRepository.findById(id).get();

        //if user is PROC_MANAGER
        if(user.getRole() == ERole.PROC_MANAGER){
            poRequest.setStatus(EPORequestStatus.PROC_MANAGER_REJECTION);
            poRequest.setProcManager(user);
            poRequest.setDateOfStatusChangeHoHR(LocalDate.now());
            poRequest.setRejectionComment(rejectionComment);
        } else if(user.getRole() == ERole.PO_APPROVER){
            poRequest.setStatus(EPORequestStatus.APPROVER_REJECTION);
            poRequest.setApprover(user);
            poRequest.setDateOfStatusChangeApprover(LocalDate.now());
            poRequest.setRejectionComment(rejectionComment);
        } else if(user.getRole() == ERole.DIRECTOR_GENERAL){
            poRequest.setStatus(EPORequestStatus.DIRECTOR_GENERAL_REJECTION);
            poRequest.setDirectorGeneral(user);
            poRequest.setDateOfStatusChangeDirectorGeneral(LocalDate.now());
            poRequest.setRejectionComment(rejectionComment);
        } else {
            throw new RuntimeException("You are not authorized to reject this request");
        }

        poRequest.setRejectionComment(rejectionComment);
        return poRequestRepository.save(poRequest);
    }

    @Override
    public boolean delete(UUID id) {
        //find poRequest by id
        PORequest poRequest = poRequestRepository.findById(id).get();
        //delete poRequest
        poRequest.setStatus(EPORequestStatus.DELETED);
        //save poRequest
        poRequestRepository.save(poRequest);

        return true;
    }
}
