package rw.gov.sacco.stockmis.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rw.gov.sacco.stockmis.v1.dtos.*;
import rw.gov.sacco.stockmis.v1.enums.EUserStatus;
import rw.gov.sacco.stockmis.v1.exceptions.AppException;
import rw.gov.sacco.stockmis.v1.models.User;
import rw.gov.sacco.stockmis.v1.payload.ApiResponse;
import rw.gov.sacco.stockmis.v1.payload.JwtAuthenticationResponse;
import rw.gov.sacco.stockmis.v1.security.JwtTokenProvider;
import rw.gov.sacco.stockmis.v1.services.IUserService;
import rw.gov.sacco.stockmis.v1.services.MailService;
import rw.gov.sacco.stockmis.v1.utils.Utility;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthenticationController {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MailService mailService;

    @Autowired
    public AuthenticationController(IUserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, MailService mailService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailService = mailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @PostMapping(path = "/signin")
    public ResponseEntity<ApiResponse> signin(@Valid @RequestBody SignInDTO dto) {

        String jwt = null;

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        try {
            SecurityContextHolder.getContext().setAuthentication(authentication);

            jwt = jwtTokenProvider.generateToken(authentication);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println();

        return ResponseEntity.ok(ApiResponse.success(new JwtAuthenticationResponse(jwt)));
    }

    @PutMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@Valid @RequestBody VerifyEmailDTO dto) {
        userService.verifyEmail(dto.getEmail(), dto.getActivationCode());
        return ResponseEntity.ok(ApiResponse.success("Your email has been verified successfully"));
    }

    @PostMapping(path = "/initiate-reset-password")
    public ResponseEntity<ApiResponse> initiateResetPassword(@RequestBody @Valid InitiatePasswordDTO dto) {
        User user = this.userService.getByEmail(dto.getEmail());
        user.setActivationCode(Utility.randomUUID(6, 0, 'N'));

        this.userService.save(user);

        mailService.sendResetPasswordMail(user);

        return ResponseEntity.ok(new ApiResponse(true, "Please check your mail and activate account"));
    }

    @PostMapping("/check-code")
    public ResponseEntity<ApiResponse> checkIfActivationCodeIsValid(@Valid @RequestBody CheckActivationCode dto) {
        return ResponseEntity.ok(ApiResponse.success(userService.isCodeValid(dto.getEmail(), dto.getActivationCode())));
    }

   @PostMapping(path = "/reset-password")
   public ResponseEntity<ApiResponse> resetPassword(@RequestBody @Valid ResetPasswordDTO dto) {
       User user = this.userService.getByEmail(dto.getEmail());

       if (Utility.isCodeValid(user.getActivationCode(), dto.getActivationCode())) {
           user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
           user.setActivationCode(Utility.randomUUID(6, 0, 'N'));
           user.setStatus(EUserStatus.ACTIVE);
           this.userService.save(user);
       } else {
           throw new AppException("Invalid code");
       }
       return ResponseEntity.ok(new ApiResponse(true, "Password successfully reset"));
   }

//
//    @GetMapping("/profile")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<ApiResponse> getProfile() {
//        Profile profile = userService.getLoggedInProfile();
//
//        return ResponseEntity.ok(ApiResponse.success(profile));
//    }
//
//    @PutMapping("/update-profile")
//    public ResponseEntity<ApiResponse> updateProfile(@Valid @RequestBody UpdateUserDTO dto) {
//
//        User user = Mapper.getUserFromDTO(dto);
//        User theUser = userService.getLoggedInUser();
//        userService.update(theUser.getId(), user);
//
//        return ResponseEntity.ok(ApiResponse.success("Successfully updated profile "));
//    }
//
//    @PutMapping("/change-password")
//    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
//        User user = userService.getLoggedInUser();
//
//        if (!bCryptPasswordEncoder.matches(dto.getCurrentPassword(), user.getPassword()))
//            return ResponseEntity.badRequest().body(ApiResponse.fail("Invalid current password"));
//
//        user.setPassword(bCryptPasswordEncoder.encode(dto.getNewPassword()));
//
//        userService.save(user);
//
//        return ResponseEntity.ok(ApiResponse.success("Successfully updated password"));
//    }
//
//    @ExceptionHandler
//    public ResponseEntity<ApiResponse> handleBadRequestException(BadRequestException badRequestException) {
//        return ResponseEntity.badRequest().body(ApiResponse.fail(badRequestException.getMessage()));
//    }
}