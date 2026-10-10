package com.apartment.tracker.controller;

import com.apartment.tracker.dto.Dtos;
import com.apartment.tracker.dto.Dtos.*;
import com.apartment.tracker.model.Complaint;
import com.apartment.tracker.model.ComplaintStatus;
import com.apartment.tracker.service.ComplaintService;
import com.apartment.tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** ADMIN only (enforced in SecurityConfig). */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ComplaintService complaintService;
    private final UserService userService;

    @GetMapping("/complaints")
    public List<ComplaintDto> allComplaints() {
        return complaintService.getAllComplaints().stream().map(Dtos::complaint).toList();
    }

    @GetMapping("/residents")
    public List<UserDto> residents() {
        return userService.getAllResidents().stream().map(Dtos::user).toList();
    }

    @GetMapping("/technicians")
    public List<UserDto> technicians() {
        return userService.getAllTechnicians().stream().map(Dtos::user).toList();
    }

    @PostMapping("/residents/{id}/approve")
    public MessageResponse approve(@PathVariable Long id) {
        userService.approveResident(id);
        return new MessageResponse("Resident approved.");
    }

    @PostMapping("/complaints/{id}/assign")
    public ComplaintDto assign(@PathVariable Long id, @Valid @RequestBody AssignRequest request) {
        return Dtos.complaint(complaintService.assignTechnician(id, request.technicianId()));
    }

    @PostMapping("/complaints/{id}/status")
    public ComplaintDto updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        if (request.status() == ComplaintStatus.ASSIGNED) {
            // "Assigned" is only ever set automatically by the assign action
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use the Assign action to mark a complaint as Assigned.");
        }
        Complaint target = complaintService.getAllComplaints().stream()
                .filter(c -> c.getId().equals(id)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found."));
        if (target.getAssignedTo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assign a technician before updating the status.");
        }
        return Dtos.complaint(complaintService.updateStatus(id, request.status()));
    }
}
