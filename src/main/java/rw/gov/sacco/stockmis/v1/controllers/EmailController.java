package rw.gov.sacco.stockmis.v1.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.gov.sacco.stockmis.v1.dtos.SendEmailDTO;
import rw.gov.sacco.stockmis.v1.payload.ApiResponse;
import rw.gov.sacco.stockmis.v1.services.MailService;


import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/emails")
public class EmailController {

    private final MailService mailService;


    @Autowired
    public EmailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody SendEmailDTO dto) {
        this.mailService.sendCustomEmail(dto);
        return ResponseEntity.ok(ApiResponse.success("Email sent successfully"));
    }

}

