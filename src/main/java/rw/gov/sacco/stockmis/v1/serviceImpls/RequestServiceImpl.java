package rw.gov.sacco.stockmis.v1.serviceImpls;

import org.springframework.data.domain.Page;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateRequestDTO;
import rw.gov.sacco.stockmis.v1.enums.ERequestStatus;
import rw.gov.sacco.stockmis.v1.enums.ERole;
import rw.gov.sacco.stockmis.v1.exceptions.BadRequestException;
import rw.gov.sacco.stockmis.v1.exceptions.ResourceNotFoundException;
import rw.gov.sacco.stockmis.v1.models.Request;
import rw.gov.sacco.stockmis.v1.models.User;
import rw.gov.sacco.stockmis.v1.repositories.IRequestRepository;
import rw.gov.sacco.stockmis.v1.services.IItemService;
import rw.gov.sacco.stockmis.v1.services.IRequestService;
import rw.gov.sacco.stockmis.v1.models.Item;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rw.gov.sacco.stockmis.v1.services.IUserService;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import static rw.gov.sacco.stockmis.v1.enums.ERequestStatus.*;


@Service
public class RequestServiceImpl implements IRequestService {

    private final IRequestRepository requestRepository;
    private final IItemService itemService;

    private final IUserService userService;

    @Autowired
    private final EntityManager entityManager;

    @Autowired
    public RequestServiceImpl(IRequestRepository requestRepository, IItemService itemService, IUserService userService, EntityManager entityManager) {
        this.requestRepository = requestRepository;
        this.itemService = itemService;
        this.userService = userService;
        this.entityManager= entityManager;
    }

    @Override
    public long count() {
        return requestRepository.count();
    }

    @Override
    public long countBranchesWithPendingRequests() {
        return requestRepository.countBranchesWithPendingRequests(ERequestStatus.PENDING);
    }

    @Override
    public Request create(CreateOrUpdateRequestDTO requestDTO) {

        Item item = itemService.findById(requestDTO.getItemId());

        Request request = new Request(ERequestStatus.PENDING, item, requestDTO.getQuantity(), requestDTO.getAvailableQuantity());
        return requestRepository.save(request);
    }

    @Override
    public List<Request> findAllByItemAndDateOfStatusChangeStoreKeeperGrantedBetween(Item item, LocalDate startDate, LocalDate endDate) {
        return requestRepository.findAllByItemAndDateOfStatusChangeStoreKeeperGrantedBetween(item, startDate, endDate);
    }

    @Override
    public Request findById(UUID id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", id.toString()));
        User user = userService.findById(request.getCreatedBy());
        request.setCreator(user);
        return request;
    }

    @Override
    public boolean existsById(UUID id) {
        return requestRepository.existsById(id);
    }

    @Override
    public Iterable<Request> findAll() {
        return requestRepository.findAll();
    }

    @Override
    public Page<Request> findAll(Pageable pageable) {
        User user = userService.getLoggedInUser();

        if (user.getRole() == ERole.INITIATOR || user.getRole() == ERole.APPROVER) {
            Page<Request> requests = requestRepository.findAllByUserBranch(user.getBranch().getId(), pageable);
            setCreator(requests);
            return requests;
        }
        Page<Request> requests =  requestRepository.findAll(pageable);
        setCreator(requests);
        return requests;
    }

    @Override
    public Request update(UUID id, CreateOrUpdateRequestDTO requestDTO) {
        Request request = findById(id);
        User user = userService.getLoggedInUser();

        if (!request.getCreatedBy().equals(user.getId())) {
            throw new BadRequestException("You cannot update a request you did not create");
        }

        if (!request.getStatus().equals(PENDING)) {
            throw new BadRequestException("You cannot update a request that is not pending");
        }

        Item item = itemService.findById(requestDTO.getItemId());
        request.setItem(item);
        request.setQuantity(requestDTO.getQuantity());
        request.setAvailableQuantity(requestDTO.getAvailableQuantity());
        return requestRepository.save(request);
    }

    @Override
    public Request updateRejectionComment(UUID id, String rejectionComment) {
        User user = userService.getLoggedInUser();

        Request request = findById(id);

        if(!(request.getStatus() == ERequestStatus.APPROVER_REJECTION || request.getStatus() == ERequestStatus.STORE_KEEPER_REJECTION || request.getStatus() == ERequestStatus.HOHR_REJECTION)){
            throw new BadRequestException("You cannot update a rejection comment for a request that is not rejected");
        }

        if (request.getStatus() == ERequestStatus.APPROVER_REJECTION && user.getRole() != ERole.APPROVER) {
            throw new BadRequestException("You cannot update a rejection comment for a request that is not rejected by you");
        }

        if (request.getStatus() == ERequestStatus.STORE_KEEPER_REJECTION && user.getRole() != ERole.STORE_KEEPER) {
            throw new BadRequestException("You cannot update a rejection comment for a request that is not rejected by you");
        }

        if (request.getStatus() == ERequestStatus.HOHR_REJECTION && user.getRole() != ERole.HO_HR) {
            throw new BadRequestException("You cannot update a rejection comment for a request that is not rejected by you");
        }

        request.setRejectionComment(rejectionComment);

        return requestRepository.save(request);
    }

    @Override
    public boolean delete(UUID id) {
        Request existingRequest = findById(id);

        //if request is not pending, it cannot be deleted
        if (existingRequest.getStatus() != ERequestStatus.PENDING) {
            throw new BadRequestException("You cannot delete a request that is not pending");
        }

        requestRepository.delete(existingRequest);

        return true;
    }

    @Override
    public Page<Request> findAllByStatus(ERequestStatus status, Pageable pageable) {
        Page<Request> requests = requestRepository.findAllByStatus(status, pageable);
        setCreator(requests);
        return requests;
    }

    @Override
    public Page<Request> findAllByBranchIdAnItemIdAndStatusAndCategoryId(Long branchId, UUID itemId, ERequestStatus status, UUID categoryId, Pageable pageable) {

        // get logged in user
        User user = userService.getLoggedInUser();

        //if only status and item id is provided
        if (branchId == null && itemId != null && status != null && categoryId == null) {
            Page<Request> requests = requestRepository.findAllByItemAndStatus(itemId, status, pageable);
            setCreator(requests);
            return requests;
        }

        //if only status and category id is provided
        if (branchId == null && itemId == null && status != null && categoryId != null) {
            Page<Request> requests = requestRepository.findAllByCategoryAndStatus(categoryId, status, pageable);
            setCreator(requests);
            return requests;
        }

        //if only status and item and category id is provided
        if (branchId == null && itemId != null && status != null && categoryId != null) {
            Page<Request> requests = requestRepository.findAllByItemAndStatusAndCategory(itemId, status, categoryId,  pageable);
            setCreator(requests);
            return requests;
        }
        //if only branch is provided
        if (branchId != null && itemId == null && categoryId == null) {
            Page<Request> requests = requestRepository.findAllByUserBranchAndStatus(branchId, status, pageable);
            setCreator(requests);
            return requests;
        }

        // if only branch and item and status is provided
        if (branchId != null && itemId != null && status != null && categoryId == null) {
            Page<Request> requests = requestRepository.findAllByItemAndStatusAndBranch( itemId, status, branchId, pageable);
            setCreator(requests);
            return requests;
        }

        // if only branch and status and category is provided
        if (branchId != null && itemId == null && status != null && categoryId != null) {
            Page<Request> requests = requestRepository.findAllByCategoryAndStatusAndBranch( categoryId, status, branchId, pageable);
            setCreator(requests);
            return requests;
        }

        //if everything is provided
        if (branchId != null && itemId != null && status != null && categoryId != null) {
            Page<Request> requests = requestRepository.findAllByItemAndStatusAndCategoryAndBranch( itemId, status, categoryId, branchId, pageable);
            setCreator(requests);
            return requests;
        }

        //if only status is provided
        if (branchId == null && itemId == null && status != null && categoryId == null) {
            Page<Request> requests = requestRepository.findAllByStatus(status, pageable);
            setCreator(requests);
            return requests;
        }

        // if only category is provided
        if (branchId == null && itemId == null && status == null && categoryId != null) {
            Page<Request> requests = requestRepository.findAllByItem_ItemCategory_Id(categoryId, pageable);
            setCreator(requests);
            return requests;
        }

        // if only category and branch is provided
        if (branchId != null && itemId == null && status == null && categoryId != null) {
            Page<Request> requests = requestRepository.findAllByCategoryAndStatusAndBranch(categoryId, status, branchId, pageable);
            setCreator(requests);
            return requests;
        }

        return null;
//        return requestRepository.findAll(pageable);

    }

    @Override
    public Request changeStatus(UUID id, ERequestStatus status, Integer suggestedGrant) {
        Request existingRequest = findById(id);
        existingRequest.setStatus(status);

        User user = userService.getLoggedInUser();

        if ( (user.getRole() == ERole.STORE_KEEPER) && suggestedGrant == null && status == STORE_KEEPER_APPROVAL) {
            throw new BadRequestException("Suggested Grant is required");
        }

        if ( !(user.getRole() == ERole.STORE_KEEPER) && suggestedGrant != null) {
            throw new BadRequestException("Only Store Keepers can suggest grants");
        }

        if (status == ERequestStatus.GRANTED && user.getRole() != ERole.STORE_KEEPER) {
            throw new BadRequestException("Only Store Keepers can set status to Granted");
        }



        switch (status) {
            case APPROVER_APPROVAL:
                existingRequest.setDateOfStatusChangeApprover(LocalDate.now());
                existingRequest.setApprover(user);
                break;
            case STORE_KEEPER_APPROVAL:
                existingRequest.setDateOfStatusChangeStoreKeeper(LocalDate.now());
                existingRequest.setStoreKeeper(user);
                existingRequest.setSuggestedQuantity(suggestedGrant);
                break;
            case HOHR_APPROVAL:
                existingRequest.setDateOfStatusChangeHoHR(LocalDate.now());
                existingRequest.setHoHR(user);
                break;
            case GRANTED:
                existingRequest.setDateOfStatusChangeStoreKeeperGranted(LocalDate.now());
                existingRequest.setStoreKeeperWhoGranted(user);
                break;
            default:
                break;
        }

        if(status == ERequestStatus.GRANTED) {
            Item item = existingRequest.getItem();
            //suggestedQuantity should not be greater than Nbr of items in stock
            if (existingRequest.getSuggestedQuantity() > item.getNbrOfItemsAvailable()) {
                throw new BadRequestException("Suggested Grant is greater than the available quantity in stock");
            }

            if(item.getNbrOfItemsAvailable() == 0){
                throw new BadRequestException("This item is out of stock!");
            }

            itemService.decrementNbrOfItemsAvailable(item.getId(), existingRequest.getSuggestedQuantity());

            try {
                if (entityManager == null) {
                    throw new IllegalStateException("entityManager is not initialized");
                }

                StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("simulate_item_price");
                storedProcedure.registerStoredProcedureParameter("p_itemid", UUID.class, ParameterMode.IN);
                storedProcedure.registerStoredProcedureParameter("p_quantity", Integer.class, ParameterMode.IN);
                storedProcedure.registerStoredProcedureParameter("p_transaction_date", Date.class, ParameterMode.IN);

                storedProcedure.setParameter("p_itemid", item.getId());
                storedProcedure.setParameter("p_quantity", existingRequest.getSuggestedQuantity());
                // Get the current date and set it as the value for the "p_transaction_date" parameter
                Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
                storedProcedure.setParameter("p_transaction_date", currentDate);

                storedProcedure.execute();
            } catch (Exception e) {
                // Log the error here
                System.err.println("An error occurred during the procedure call: " + e.getMessage());
            }
        }

        return requestRepository.save(existingRequest);
    }


    @Override
    public Request reject(UUID id, String rejectionComment) {
        Request existingRequest = findById(id);

        // get logged in user object
        User user = userService.getLoggedInUser();

        if(user.getRole() == ERole.APPROVER) {
            existingRequest.setStatus(APPROVER_REJECTION);
            existingRequest.setDateOfStatusChangeApprover(LocalDate.now());
            existingRequest.setApprover(user);
        } else if(user.getRole() == ERole.STORE_KEEPER) {
            existingRequest.setStatus(STORE_KEEPER_REJECTION);
            existingRequest.setDateOfStatusChangeStoreKeeper(LocalDate.now());
            existingRequest.setStoreKeeper(user);
        } else if(user.getRole() == ERole.HO_HR) {
            existingRequest.setStatus(HOHR_REJECTION);
            existingRequest.setDateOfStatusChangeHoHR(LocalDate.now());
            existingRequest.setHoHR(user);
        }

        existingRequest.setRejectionComment(rejectionComment);
        return requestRepository.save(existingRequest);
    }

    //method to setCreator for a page of requests
    private Page<Request> setCreator(Page<Request> requests) {
        requests.forEach(request -> {
            User user;
            if(request.getCreatedBy()!=null) {
                user = userService.findById(request.getCreatedBy());
                request.setCreator(user);
                request.getItem().setSharableQuantity(request.getItem().getNbrOfItemsAvailable() - getNbrOfItemsInUse(request.getItem()));
            }
        });
        return requests;
    }

    private Integer getNbrOfItemsInUse(Item item) {
        Integer nbrOfItemsInUse = 0;
        List<Request> requests = requestRepository.findAllByItem(item);
        for (Request request : requests) {
            if (request.getStatus() == ERequestStatus.STORE_KEEPER_APPROVAL || request.getStatus() == ERequestStatus.HOHR_APPROVAL) {
                nbrOfItemsInUse += request.getSuggestedQuantity();
            }
        }
        return nbrOfItemsInUse;
    }



}

