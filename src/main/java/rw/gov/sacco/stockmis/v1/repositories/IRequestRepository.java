package rw.gov.sacco.stockmis.v1.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.gov.sacco.stockmis.v1.enums.ERequestStatus;
import rw.gov.sacco.stockmis.v1.models.Item;
import rw.gov.sacco.stockmis.v1.models.Request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IRequestRepository extends JpaRepository<Request, UUID> {
    Page<Request> findAllByStatus(ERequestStatus status, Pageable pageable);

    // number of Branches with pending Requests

    // Count distinct branches where request status is pending
    @Query("SELECT COUNT(DISTINCT u.branch) FROM Request r JOIN User u ON r.createdBy = u.id WHERE r.status = :status")
    Long countBranchesWithPendingRequests(@Param("status") ERequestStatus status);

    @Query("SELECT r FROM Request r JOIN User u ON r.createdBy = u.id JOIN Branch b ON u.branch = b WHERE b.id = :branchId")
    Page<Request> findAllByUserBranch(@Param("branchId") Long branchId, Pageable pageable);

    @Query("SELECT r FROM Request r JOIN User u ON r.createdBy = u.id JOIN Branch b ON u.branch = b WHERE b.id = :branchId AND (:status is null OR r.status = :status)")
    Page<Request> findAllByUserBranchAndStatus(@Param("branchId") Long branchId, ERequestStatus status, Pageable pageable);

//    @Query("SELECT u FROM User u WHERE (:role MEMBER OF u.roles)  AND u.status = :status AND u.gender = :gender AND LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE CONCAT('%', LOWER(:fullNames), '%')")
//    Page<User> findByRoleAndStatusAndGenderAndSearchByFullName(Role role, EUserStatus status, EGender gender, String fullNames, Pageable pageable);


//    @Query("SELECT r FROM Request r JOIN User u ON r.createdBy = u.id JOIN Branch b ON u.branch = b WHERE b.id = :branchId")
//    Page<Request> findAllByItemAndStatusAndCategoryAndBranch( UUID itemId, ERequestStatus status, UUID categoryId, Long branchId, Pageable pageable);

    @Query("SELECT r FROM Request r JOIN User u ON r.createdBy = u.id JOIN Branch b ON u.branch = b WHERE b.id = :branchId AND (:itemId is null OR r.item.id = :itemId) AND (:status is null OR r.status = :status)")
    Page<Request> findAllByItemAndStatusAndBranch( UUID itemId, ERequestStatus status, Long branchId, Pageable pageable);
    @Query("SELECT r FROM Request r JOIN User u ON r.createdBy = u.id JOIN Branch b ON u.branch = b WHERE b.id = :branchId AND (:categoryId is null OR r.item.itemCategory.id = :categoryId) AND (:status is null OR r.status = :status)")
    Page<Request> findAllByCategoryAndStatusAndBranch( UUID categoryId, ERequestStatus status,Long branchId, Pageable pageable);

    @Query("SELECT r FROM Request r WHERE (:itemId is null OR r.item.id = :itemId) AND (:status is null OR r.status = :status)")
    Page<Request> findAllByItemAndStatus( UUID itemId, ERequestStatus status, Pageable pageable);
    @Query("SELECT r FROM Request r WHERE (:categoryId is null OR r.item.itemCategory.id = :categoryId) AND (:status is null OR r.status = :status)")
    Page<Request> findAllByCategoryAndStatus( UUID categoryId, ERequestStatus status, Pageable pageable);
    @Query("SELECT r FROM Request r WHERE (:itemId is null OR r.item.id = :itemId) AND (:categoryId is null OR r.item.itemCategory.id = :categoryId) AND (:status is null OR r.status = :status)")
    Page<Request> findAllByItemAndStatusAndCategory( UUID itemId, ERequestStatus status, UUID categoryId, Pageable pageable);
    @Query("SELECT r FROM Request r JOIN User u ON r.createdBy = u.id JOIN Branch b ON u.branch = b WHERE b.id = :branchId AND (:itemId is null OR r.item.id = :itemId) AND (:categoryId is null OR r.item.itemCategory.id = :categoryId) AND (:status is null OR r.status = :status)")
    Page<Request> findAllByItemAndStatusAndCategoryAndBranch( UUID itemId, ERequestStatus status, UUID categoryId, Long branchId, Pageable pageable);

    Page<Request> findAllByItem_ItemCategory_Id(UUID categoryId, Pageable pageable);

    List<Request> findAllByItemAndDateOfStatusChangeStoreKeeperGrantedBetween(Item item, LocalDate startDate, LocalDate endDate);

    List<Request> findAllByItem(Item item);

//    @Query("SELECT r FROM Request r JOIN r.item i JOIN User u ON r.createdBy = u.id JOIN u.branch b WHERE i.id = :itemId AND r.status = :status AND i.itemCategory.id = :categoryId AND b.id = :branchId")
//    Page<Request> findAllByItemAndStatusAndCategoryAndBranch(@Param("itemId") UUID itemId, @Param("status") ERequestStatus status, @Param("categoryId") UUID categoryId, @Param("branchId") Long branchId, Pageable pageable);

//    @Query("SELECT r FROM Request r JOIN Item i ON r.item.id = i.id JOIN ItemCategory c ON i.itemCategory.id = c.id JOIN User u ON r.createdBy = u.id JOIN Branch b ON u.branch = b WHERE (:itemId is null or i.id = :itemId) AND (:status is null or r.status = :status) AND (:categoryId is null or (:itemId is not null and c.id = :categoryId)) AND (:branchId is null or b.id = :branchId)")
//    Page<Request> findAllByItemIdAndStatusAndCategoryIdAndBranchId(@Param("itemId") UUID itemId, @Param("status") ERequestStatus status, @Param("categoryId") UUID categoryId, @Param("branchId") Long branchId, Pageable pageable);

}
