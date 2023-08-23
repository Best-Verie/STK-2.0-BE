package rw.gov.sacco.stockmis.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rw.gov.sacco.stockmis.v1.dtos.SignUpDTO;
import rw.gov.sacco.stockmis.v1.dtos.UpdateUserDTO;
import rw.gov.sacco.stockmis.v1.enums.EGender;
import rw.gov.sacco.stockmis.v1.enums.ERole;
import rw.gov.sacco.stockmis.v1.enums.ERoleAllowed;
import rw.gov.sacco.stockmis.v1.enums.EUserStatus;
import rw.gov.sacco.stockmis.v1.exceptions.ResourceNotFoundException;
import rw.gov.sacco.stockmis.v1.fileHandling.File;
import rw.gov.sacco.stockmis.v1.fileHandling.FileStorageService;
import rw.gov.sacco.stockmis.v1.models.Branch;
import rw.gov.sacco.stockmis.v1.models.Role;
import rw.gov.sacco.stockmis.v1.models.User;
import rw.gov.sacco.stockmis.v1.payload.ApiResponse;
import rw.gov.sacco.stockmis.v1.repositories.IBranchRepository;
import rw.gov.sacco.stockmis.v1.services.IBranchService;
import rw.gov.sacco.stockmis.v1.services.IFileService;
import rw.gov.sacco.stockmis.v1.services.IRoleService;
import rw.gov.sacco.stockmis.v1.services.IUserService;
import rw.gov.sacco.stockmis.v1.utils.Constants;
import rw.gov.sacco.stockmis.v1.utils.Mapper;
import org.springframework.http.MediaType;


import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {

    private final IUserService userService;
    private final IRoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FileStorageService fileStorageService;
    private final IFileService fileService;

    private final IBranchRepository branchRepository;
    private final IBranchService branchService;

    @Value("${uploads.directory.user_profiles}")
    private String directory;

    @Autowired
    public UserController(IUserService userService, IRoleService iRoleService, BCryptPasswordEncoder bCryptPasswordEncoder, FileStorageService fileStorageService, IFileService fileService, IBranchRepository branchRepository, @Lazy IBranchService branchService) {
        this.userService = userService;
        this.roleService = iRoleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.fileService = fileService;
        this.fileStorageService = fileStorageService;
        this.branchRepository = branchRepository;
        this.branchService = branchService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/count")
    public ResponseEntity<?> count() {
        return ResponseEntity.ok(ApiResponse.success(userService.count()));
    }

    @GetMapping(path = "/current-user")
    public ResponseEntity<ApiResponse> currentlyLoggedInUser() {
        return ResponseEntity.ok(new ApiResponse(true, userService.getLoggedInUser()));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers(@RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page, @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.ASC, "id");

        return ResponseEntity.ok(ApiResponse.success(this.userService.getAll(pageable)));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping(path = "/all")
    public ResponseEntity<ApiResponse> getAllPaginatedUsers(@RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page, @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.ASC, "id");

        return ResponseEntity.ok(ApiResponse.success(userService.getAll(pageable)));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/{id}")
    public ResponseEntity<User> getById(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(this.userService.findById(id));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> search(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "status") EUserStatus status,
            @RequestParam(value = "gender", required = false) EGender gender,
            @RequestParam(value = "role", required = false) ERoleAllowed role,
            @RequestParam(value = "branch", required = false) Long branchId

    ){
        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.ASC, "id");

        ERole eRole = role != null ? ERole.valueOf(role.name()) : null;

        if(role != null && branchId != null){
            Role roleSearch = roleService.findByName(eRole);
            Branch branch = branchService.findById(branchId);

            return ResponseEntity.ok(ApiResponse.success(userService.search(roleSearch, branch, status, name, gender, pageable)));
        } else if (role != null){
            Role roleSearch = roleService.findByName(eRole);
            return ResponseEntity.ok(ApiResponse.success(userService.search(roleSearch, status, name, gender, pageable)));
        }
        else{
            Branch branch = branchService.findById(branchId);
            return ResponseEntity.ok(ApiResponse.success(userService.search(status, branch, name, gender, pageable)));
        }

    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping(path = "/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid SignUpDTO dto) {

        User user = new User();

        String encodedPassword = bCryptPasswordEncoder.encode(dto.getPassword());
        Role role = roleService.findByName(dto.getRole());

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setPhoneNumber(dto.getMobile());
        user.setPassword(encodedPassword);
        user.setStatus(EUserStatus.WAIT_EMAIL_VERIFICATION);
        user.setRoles(Collections.singleton(role));
        user.setUserName(userService.generateUserName(dto.getLastName(), dto.getFirstName()));

        Branch branch = branchRepository.findById(dto.getBranchId()).orElse(null);
        user.setBranch(branch);


        User entity = this.userService.create(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, entity));
    }

//    @PostMapping(path = "/verify-email")
//    public ResponseEntity<ApiResponse> verifyEmail(@RequestBody @Valid VerifyUserEmailDTO dto) {
//        User user = userService.findById(dto.getUserId());
//        userService.verifyEmail(user);
//        return ResponseEntity.ok(new ApiResponse(true, "Email was successfully verified"));
//    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(path = "/{id}/upload-signature")
    public ResponseEntity<ApiResponse> uploadSignature(
            @PathVariable(value = "id") UUID userId,
            @RequestParam("file") MultipartFile document
    ) {
        this.userService.findById(userId);
        File file = this.fileService.create(document, directory);

        User updated = this.userService.changeSignatureImage(userId, file);

        return ResponseEntity.ok(new ApiResponse(true, "Signature saved successfully", updated));
    }




//    @PutMapping("/{id}/approve")
//    public ResponseEntity<ApiResponse> approveAUser(@PathVariable UUID id) {
//        User user = userService.findById(id);
//
//        userService.approve(user);
//
//        return ResponseEntity.ok(ApiResponse.success("Approved User Successfully"));
//    }

//    @PutMapping("/{id}/de-activate")
//    public ResponseEntity<ApiResponse> deActivateAnAccount(@PathVariable UUID id) {
//        User user = userService.findById(id);
//
//        userService.deActivate(user);
//
//        return ResponseEntity.ok(ApiResponse.success("De-Activate User Successfully"));
//    }
//
//    @PutMapping("/{id}/mark-as/PENDING")
//    public ResponseEntity<ApiResponse> markUserAsPending(@PathVariable UUID id) {
//        User user = userService.findById(id);
//
//        user.setStatus(EUserStatus.PENDING);
//
//        userService.save(user);
//
//        return ResponseEntity.ok(ApiResponse.success("Marked User as Pending Successfully"));
//    }
//
//    @PutMapping("/{id}/reject")
//    public ResponseEntity<ApiResponse> rejectAUser(@PathVariable UUID id, @Valid @RequestBody RejectionDTO dto) {
//        User user = userService.findById(id);
//
//        userService.reject(user, dto.getRejectionMessage());
//
//        return ResponseEntity.ok(ApiResponse.success("User Rejected Successfully"));
//    }

//    @PutMapping("/reject-many")
//    public ResponseEntity<ApiResponse> rejectManyUsers(@Valid @RequestBody RejectManyUsersDTO dto) {
//
//        userService.rejectManyUsers(dto.getUsersIds(), dto.getRejectionMessage());
//
//        return ResponseEntity.ok(ApiResponse.success("Rejected all users"));
//    }
//
//    @PutMapping("/approve-many")
//    public ResponseEntity<ApiResponse> approveManyUsers(@Valid @RequestBody ApproveManyUsersDTO dto) {
//
//        userService.approveManyUsers(dto.getUserIds());
//
//        return ResponseEntity.ok(ApiResponse.success("Approved all the Users"));
//    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@Valid @RequestBody UpdateUserDTO dto, @PathVariable UUID id){
        User theUser = Mapper.getUserFromDTO(dto);

        //get branch
        Branch branch = branchRepository.findById(dto.getBranchId()).orElseThrow(() -> new ResourceNotFoundException("Branch", "id", dto.getBranchId()));

        theUser = userService.update(id, theUser, branch);
        return ResponseEntity.accepted().body(ApiResponse.success(theUser));
    }

//    @PutMapping("/change-password")
//    public ResponseEntity<ApiResponse> changePassword(ChangePasswordDTO dto){
//        userService.changePassword(dto);
//
//        return Formatter.done();
//    }
//
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/load-file/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> loadSignature(@PathVariable String filename) {
        Resource file = this.fileStorageService.load(directory, filename);

        try {
            // Determine the content type based on the file extension
            String contentType = Files.probeContentType(file.getFile().toPath());

            // If the content type is not found, set it explicitly to "image/png" for PNG files
            if (contentType == null || !contentType.startsWith("image/")) {
                contentType = "image/png";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(file);
        } catch (IOException e) {
            throw new RuntimeException("Error while determining the content type: " + e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id){
        return ResponseEntity.ok(ApiResponse.success(userService.delete(id)));
    }

    //api to re-activate deactivated user
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}/re-activate")
    public ResponseEntity<ApiResponse> reActivateAnAccount(@PathVariable UUID id) {
        User user = userService.findById(id);

        userService.reActivate(user);

        return ResponseEntity.ok(ApiResponse.success("Re-Activate User Successfully"));
    }
}
