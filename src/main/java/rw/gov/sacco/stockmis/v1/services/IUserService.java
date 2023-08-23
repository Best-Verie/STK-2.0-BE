package rw.gov.sacco.stockmis.v1.services;

import rw.gov.sacco.stockmis.v1.dtos.ChangePasswordDTO;
import rw.gov.sacco.stockmis.v1.enums.EGender;
import rw.gov.sacco.stockmis.v1.enums.ERole;
import rw.gov.sacco.stockmis.v1.enums.EUserStatus;
import rw.gov.sacco.stockmis.v1.fileHandling.File;
import rw.gov.sacco.stockmis.v1.models.Branch;
import rw.gov.sacco.stockmis.v1.models.Role;
import rw.gov.sacco.stockmis.v1.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.gov.sacco.stockmis.v1.utils.projections.Profile;


import java.util.List;
import java.util.UUID;


public interface IUserService {

    long count();

    List<User> getAll();

    Page<User> getAll(Pageable pageable);

    Page<User> getAllActive(Pageable pageable);

    Page<User> getAllRejected(Pageable pageable);

    User findById(UUID id);

    User create(User user);

    User save(User user);

    User update(UUID id, User user, Branch branch);

    boolean delete(UUID id);

    boolean isNotUnique(User user);

    boolean isNotUniqueInVerified(User user);

    void validateNewRegistration(User user);

    List<User> getAllByRole(ERole role);

    List<User> getAllByRoleAndActive(ERole role);

    Page<User> getAllByRole(Pageable pageable, ERole role);

    List<User> searchUser(String searchKey);

    Page<User> searchUser(Pageable pageable, String searchKey);

    User getLoggedInUser();

    Profile getLoggedInProfile();

    User getByEmail(String email);

    User approve(User user);

    void approveManyUsers(List<UUID> userIds);

    User reject(User user, String rejectionMessage);

    void rejectManyUsers(List<UUID> userIds, String message);

    User changeStatus(UUID id, EUserStatus status);

    void verifyEmail(String email, String activationCode);

    void verifyEmail(User user);

    void deActivate(User user);

    User changeProfileImage(UUID id, File file);

    User changeSignatureImage(UUID id, File file);

    Page<User> search(EUserStatus status, String name, EGender gender, Pageable pageable);

    Page<User> search(EUserStatus status, Branch branch, String name, EGender gender, Pageable pageable);

    Page<User> search(Role role, EUserStatus status, String name, EGender gender, Pageable pageable);

    Page<User> search(Role role, Branch branch, EUserStatus status, String name, EGender gender, Pageable pageable);

    boolean isCodeValid(String email, String activationCode);

    Integer countAllActive();

    Integer countAllActiveByRole(Role role);

    void changePassword(ChangePasswordDTO dto);

    void reActivate(User user);

    String generateUserName(String lastName, String firstName);

    void createManyUsers(String jsonData);
}