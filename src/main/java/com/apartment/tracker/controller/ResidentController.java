package com.apartment.tracker.controller;

import com.apartment.tracker.dto.Dtos;
import com.apartment.tracker.dto.Dtos.ComplaintDto;
import com.apartment.tracker.dto.Dtos.ComplaintRequest;
import com.apartment.tracker.model.User;
import com.apartment.tracker.service.ComplaintService;
import com.apartment.tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** RESIDENT only (enforced in SecurityConfig): raise and view your own complaints. */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ResidentController {

    private final ComplaintService complaintService;
    private final UserService userService;

    @GetMapping("/complaints")
    public List<ComplaintDto> myComplaints(Authentication authentication) {
        User resident = userService.getCurrentUser(authentication);
        return complaintService.getComplaintsForResident(resident).stream().map(Dtos::complaint).toList();
    }

    @PostMapping("/complaints")
    public ResponseEntity<ComplaintDto> raise(Authentication authentication, @Valid @RequestBody ComplaintRequest request) {
        User resident = userService.getCurrentUser(authentication);
        var saved = complaintService.raiseComplaint(resident, request.title().trim(), request.issueType(),
                request.description().trim(), request.location().trim());
        return ResponseEntity.status(HttpStatus.CREATED).body(Dtos.complaint(saved));
    }
}
