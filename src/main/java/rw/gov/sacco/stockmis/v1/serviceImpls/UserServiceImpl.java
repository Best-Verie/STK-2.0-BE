package rw.gov.sacco.stockmis.v1.serviceImpls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.sacco.stockmis.v1.dtos.ChangePasswordDTO;
import rw.gov.sacco.stockmis.v1.enums.EGender;
import rw.gov.sacco.stockmis.v1.enums.ERole;
import rw.gov.sacco.stockmis.v1.enums.EUserStatus;
import rw.gov.sacco.stockmis.v1.exceptions.BadRequestException;
import rw.gov.sacco.stockmis.v1.exceptions.ResourceNotFoundException;
import rw.gov.sacco.stockmis.v1.fileHandling.File;
import rw.gov.sacco.stockmis.v1.models.Branch;
import rw.gov.sacco.stockmis.v1.models.Role;
import rw.gov.sacco.stockmis.v1.models.User;
import rw.gov.sacco.stockmis.v1.repositories.IBranchRepository;
import rw.gov.sacco.stockmis.v1.repositories.IUserRepository;
import rw.gov.sacco.stockmis.v1.services.IRoleService;
import rw.gov.sacco.stockmis.v1.services.IUserService;
import rw.gov.sacco.stockmis.v1.services.MailService;
import rw.gov.sacco.stockmis.v1.utils.Mapper;
import rw.gov.sacco.stockmis.v1.utils.projections.Profile;

import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;


@Service
public class UserServiceImpl implements IUserService {

    private final MailService mailService;
    private final IRoleService roleService;
    private final IUserRepository userRepository;

    private final IBranchRepository branchRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(IRoleService iRoleService, MailService mailService, IUserRepository userRepository, @Lazy IBranchRepository branchRepository, @Lazy BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.mailService = mailService;
        this.roleService = iRoleService;
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }



    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public List<User> getAll() {
        return this.userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    @Override
    public Page<User> getAllActive(Pageable pageable) {
        return userRepository.findByStatus(EUserStatus.ACTIVE, pageable);
    }

    @Override
    public Page<User> getAllRejected(Pageable pageable) {
        return userRepository.findByStatus(EUserStatus.REJECTED, pageable);
    }

    @Override
    public User findById(UUID id) {
        return this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString()));
    }

    @Override
    public User create(User user) {


        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new BadRequestException("Phone number already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new BadRequestException("User name already exists");
        }

        User savedUser = userRepository.save(user);

        mailService.sendAccountVerificationEmail(savedUser);

        return savedUser;
    }

    @Override
    @Transactional
    public void createManyUsers(String jsonData) {
        JSONArray jsonArray = new JSONArray(jsonData);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String firstName = jsonObject.getString("Firstname");
            String lastName = jsonObject.getString("Lastname");
            String phoneNumber = jsonObject.getString("Phone Number");
            String email = jsonObject.getString("Emails");
            String branchName = jsonObject.getString("Branch");
            String gender = jsonObject.getString("Gender");
            //convert gender to enum
            EGender eGender = EGender.valueOf(gender);
            String roleName = jsonObject.getString("Role");
            //convert roleName to ERole enum
            ERole eRole = ERole.valueOf(roleName.toUpperCase());


            User user = new User();

            String encodedPassword = bCryptPasswordEncoder.encode("SACCO@132");
            Role role = roleService.findByName(eRole);

            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setGender(eGender);
            user.setPhoneNumber(phoneNumber);
            user.setPassword(encodedPassword);
            user.setStatus(EUserStatus.WAIT_EMAIL_VERIFICATION);
            user.setRoles(Collections.singleton(role));
            user.setUserName(generateUserName(lastName, firstName));

            Branch branch = branchRepository.findByName(branchName).orElse(null);
            user.setBranch(branch);

            if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
                throw new BadRequestException(String.format("Phone number: %s already exists", user.getPhoneNumber()));
            }
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new BadRequestException("Email already exists");
            }
            if (userRepository.existsByUserName(user.getUserName())) {
                throw new BadRequestException("User name already exists");
            }

            User savedUser = userRepository.save(user);

            mailService.sendAccountVerificationEmail(savedUser);

        }
    }


    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(UUID id, User user, Branch branch) {
        User entity = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString()));

        boolean emailChanged = !entity.getEmail().equals(user.getEmail());
        boolean phoneChanged = !entity.getPhoneNumber().equals(user.getPhoneNumber());

        if (emailChanged) {
            Optional<User> userOptional = this.userRepository.findByEmail(user.getEmail());
            if (userOptional.isPresent())
                throw new BadRequestException(String.format("User with email '%s' already exists", user.getEmail()));
        }
        if (phoneChanged) {
            Optional<User> userOptional = this.userRepository.findByPhoneNumber(user.getPhoneNumber());
            if (userOptional.isPresent())
                throw new BadRequestException(String.format("User with phone number '%s' already exists", user.getPhoneNumber()));
        }

        entity.setEmail(user.getEmail());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setPhoneNumber(user.getPhoneNumber());
        entity.setGender(user.getGender());
        entity.setBranch(branch);

        return this.userRepository.save(entity);
    }

    @Override
    public boolean delete(UUID id) {
        User user = this.userRepository.findById(id).get();
        user.setStatus(EUserStatus.DEACTIVATED);
        this.userRepository.save(user);
        return true;
    }

    @Override
    public void reActivate(User user) {
        user.setStatus(EUserStatus.ACTIVE);
        this.userRepository.save(user);
    }

    @Override
    public boolean isNotUnique(User user) {
        Optional<User> userOptional = this.userRepository.findByEmailOrPhoneNumber(user.getEmail(), user.getPhoneNumber());
        return userOptional.isPresent();
    }

    @Override
    public boolean isNotUniqueInVerified(User user) {
        try {
            Optional<User> userOptional = this.userRepository.findByEmailOrPhoneNumberAndStatusNot(user.getEmail(), user.getPhoneNumber(), EUserStatus.WAIT_EMAIL_VERIFICATION);
            return userOptional.isPresent();
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void validateNewRegistration(User user) {
        // if (isNotUniqueInVerified(user)) {
        //     throw new BadRequestException(String.format("User with email '%s' or phone number '%s' already exists", user.getEmail(), user.getPhoneNumber() ));
        // }

        System.out.println("Bring this later");
    }

    @Override
    public List<User> getAllByRole(ERole role) {
        Role theRole = roleService.findByName(role);

        return this.userRepository.findByRolesContaining(theRole);
    }

    @Override
    public List<User> getAllByRoleAndActive(ERole roleName) {
        Role role = roleService.findByName(roleName);

        return this.userRepository.findByRolesContainingAndStatus(role, EUserStatus.ACTIVE);
    }

    @Override
    public Page<User> getAllByRole(Pageable pageable, ERole role) {
        Role theRole = roleService.findByName(role);

        return this.userRepository.findByRolesContaining(theRole, pageable);
    }

    @Override
    public List<User> searchUser(String searchKey) {
        return this.userRepository.searchUser(searchKey);
    }


    @Override
    public Page<User> searchUser(Pageable pageable, String searchKey) {
        return this.userRepository.searchUser(pageable, searchKey);
    }

    @Override
    public User getLoggedInUser() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == "anonymousUser")
            throw new BadRequestException("You are not logged in, try to log in");

        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        return userRepository.findByUserNameOrEmailOrPhoneNumber(email, email, email).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", email));
    }



    @Override
    public Profile getLoggedInProfile() {
        User theUser = getLoggedInUser();
        Object profile;
        Optional<Role> role = theUser.getRoles().stream().findFirst();
        if (role.isPresent()) {
            switch (role.get().getName()) {
                case ADMIN:
                    profile = theUser;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + role.get().getName());
            }

            return new Profile(profile);
        }

        return null;
    }

    @Override
    public User getByEmail(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    public User approve(User user) {
        if (user.getStatus() == EUserStatus.ACTIVE)
            throw new BadRequestException("User Already Approved  ");

        user.setStatus(EUserStatus.ACTIVE);

        mailService.sendWelcomeEmailMail(user);

        return userRepository.save(user);
    }

    @Override
    public void approveManyUsers(List<UUID> userIds) {
        List<User> users = new ArrayList<>();
        for (UUID id : userIds)
            users.add(findById(id));

        for (User user : users)
            approve(user);
    }

    @Override
    public User reject(User user, String rejectionMessage) {
        if (user.getStatus() == EUserStatus.REJECTED)
            throw new BadRequestException("User Already Rejected ");


        if (user.getStatus() == EUserStatus.ACTIVE)
            throw new BadRequestException("User was approved recently");

        user.setStatus(EUserStatus.REJECTED);
        user.setRejectionDescription(rejectionMessage);

        mailService.sendAccountRejectedMail(user);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void rejectManyUsers(List<UUID> userIds, String message) {
        for (UUID id : userIds) {
            reject(findById(id), message);
        }
    }

    @Override
    public User changeStatus(UUID id, EUserStatus status) {
        User entity = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString()));

        entity.setStatus(status);

        return this.userRepository.save(entity);
    }

    @Override
    public void verifyEmail(String email, String activationCode) {
        User user = getByEmail(email);

        if (!Objects.equals(user.getActivationCode(), activationCode))
            throw new BadRequestException("Invalid Activation Code ..");

        verifyEmail(user);
    }

    @Override
    public void verifyEmail(User user) {

        if (user.getStatus() != EUserStatus.WAIT_EMAIL_VERIFICATION)
            throw new BadRequestException("Your account is " + user.getStatus().toString().toLowerCase(Locale.ROOT));

        user.setStatus(EUserStatus.PENDING);

        userRepository.save(user);

        mailService.sendEmailVerifiedMail(user);
    }

    @Override
    public void deActivate(User user) {
        user.setStatus(EUserStatus.DEACTIVATED);
        userRepository.save(user);
    }

    @Override
    public User changeProfileImage(UUID id, File file) {
        User entity = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Document", "id", id.toString()));

        entity.setProfileImage(file);
        return this.userRepository.save(entity);

    }

    @Override
    public User changeSignatureImage(UUID id, File file) {
        User entity = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString()));

        entity.setSignature(file);
        return this.userRepository.save(entity);
    }

    @Override
    public Page<User> search(EUserStatus status, String name, EGender gender, Pageable pageable) {
        if (gender == null)
            return userRepository.findByStatusAndFirstNameContains(status, name, pageable);
        else
            return userRepository.findByStatusAndGenderAndSearchByFullName(status, gender, name, pageable);
    }

    @Override
    public Page<User> search(EUserStatus status, Branch branch, String name, EGender gender, Pageable pageable) {
        if (gender == null)
            return userRepository.findByStatusAndBranchAndFirstNameContains(status, branch, name, pageable);
        else
            return userRepository.findByStatusAndBranchAndGenderAndSearchByFullName(status, branch, gender, name, pageable);
    }

    @Override
    public Page<User> search(Role role, EUserStatus status, String name, EGender gender, Pageable pageable) {
        if (role != null) {
            if (gender == null) {
                return userRepository.findByRoleAndStatusAndFirstNameContains(role, status, name, pageable);
            } else {
                return userRepository.findByRoleAndStatusAndGenderAndSearchByFullName(role, status, gender, name, pageable);
            }
        } else {
            return search(status, name, gender, pageable);
        }
    }

    @Override
    public Page<User> search(Role role, Branch branch, EUserStatus status, String name, EGender gender, Pageable pageable) {
        if (role != null) {
            if (gender == null) {
                return userRepository.findByRoleAndBranchAndStatusAndFirstNameContains(role, branch, status, name, pageable);
            } else {
                return userRepository.findByRoleAndBranchAndStatusAndGenderAndSearchByFullName(role, branch, status, gender, name, pageable);
            }
        } else {
            return search(status, branch, name, gender, pageable);
        }

    }

    @Override
    public String generateUserName(String lastName, String firstName) {
        String userName = lastName;
        int i = 1;
        while (userRepository.existsByUserName(userName)) {
            userName = lastName + firstName.substring(0, i);
            i++;
        }
        return userName;
    }

    @Override
    public boolean isCodeValid(String email, String activationCode) {
        return userRepository.existsByActivationCodeAndEmail(activationCode, email);
    }

    @Override
    public Integer countAllActive() {
        return userRepository.countByStatus(EUserStatus.ACTIVE);
    }

    @Override
    public Integer countAllActiveByRole(Role role) {
        return userRepository.countByStatusAndRolesContaining(EUserStatus.ACTIVE, role);
    }

    @Override
    public void changePassword(ChangePasswordDTO dto) {
        User user = getLoggedInUser();

        if (Mapper.compare(user.getPassword(), dto.getCurrentPassword()))
            throw new BadRequestException("Invalid current password");

        user.setPassword(Mapper.encode(dto.getNewPassword()));

        userRepository.save(user);
    }
}
