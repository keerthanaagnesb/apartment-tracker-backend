package com.apartment.tracker.controller;

import com.apartment.tracker.dto.Dtos;
import com.apartment.tracker.dto.Dtos.ComplaintDto;
import com.apartment.tracker.dto.Dtos.StatusRequest;
import com.apartment.tracker.model.ComplaintStatus;
import com.apartment.tracker.model.User;
import com.apartment.tracker.service.ComplaintService;
import com.apartment.tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** TECHNICIAN only (enforced in SecurityConfig): see and update only the jobs assigned to you. */
@RestController
@RequestMapping("/api/technician")
@RequiredArgsConstructor
public class TechnicianController {

    private final ComplaintService complaintService;
    private final UserService userService;

    @GetMapping("/complaints")
    public List<ComplaintDto> assignedToMe(Authentication authentication) {
        User me = userService.getCurrentUser(authentication);
        return complaintService.getComplaintsAssignedTo(me).stream().map(Dtos::complaint).toList();
    }

    @PostMapping("/complaints/{id}/status")
    public ComplaintDto updateStatus(Authentication authentication, @PathVariable Long id,
                                     @Valid @RequestBody StatusRequest request) {
        User me = userService.getCurrentUser(authentication);
        boolean isMine = complaintService.getComplaintsAssignedTo(me).stream().anyMatch(c -> c.getId().equals(id));
        if (!isMine) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "That complaint isn't assigned to you.");
        }
        if (request.status() == ComplaintStatus.ASSIGNED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status can't be set back to Assigned.");
        }
        return Dtos.complaint(complaintService.updateStatus(id, request.status()));
    }
}
