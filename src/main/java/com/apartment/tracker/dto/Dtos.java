package com.apartment.tracker.dto;

import com.apartment.tracker.model.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Data Transfer Objects: the exact JSON shapes the frontend sends and receives.
 * Entities are never sent directly, so the password hash can never leak.
 */
public final class Dtos {

    private Dtos() {}

    // ---------- responses ----------
    public record UserDto(Long id, String name, String email, String phone, String flatNumber,
                          Role role, String department, boolean approved) {}

    public record PersonRef(Long id, String name, String flatNumber, String department) {}

    public record ComplaintDto(Long id, String complaintCode, String title,
                               IssueType issueType, String issueTypeLabel,
                               String description, String location,
                               ComplaintStatus status, String statusLabel,
                               PersonRef createdBy, PersonRef assignedTo,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {}

    public record AuthResponse(String token, UserDto user) {}

    public record MessageResponse(String message) {}

    // ---------- requests ----------
    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    public record RegisterRequest(@NotBlank String name,
                                  @NotBlank @Email String email,
                                  @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") String password,
                                  @NotBlank String phone,
                                  @NotBlank String flatNumber) {}

    public record ComplaintRequest(@NotBlank String title, @NotNull IssueType issueType,
                                   @NotBlank String description, @NotBlank String location) {}

    public record AssignRequest(@NotNull Long technicianId) {}

    public record StatusRequest(@NotNull ComplaintStatus status) {}

    // ---------- mappers ----------
    public static UserDto user(User u) {
        return new UserDto(u.getId(), u.getName(), u.getEmail(), u.getPhone(), u.getFlatNumber(),
                u.getRole(), u.getDepartment(), u.isApproved());
    }

    private static PersonRef person(User u) {
        return u == null ? null : new PersonRef(u.getId(), u.getName(), u.getFlatNumber(), u.getDepartment());
    }

    public static ComplaintDto complaint(Complaint c) {
        return new ComplaintDto(c.getId(), c.getComplaintCode(), c.getTitle(),
                c.getIssueType(), c.getIssueType() == null ? null : c.getIssueType().getLabel(),
                c.getDescription(), c.getLocation(),
                c.getStatus(), c.getStatus() == null ? null : c.getStatus().getLabel(),
                person(c.getCreatedBy()), person(c.getAssignedTo()),
                c.getCreatedAt(), c.getUpdatedAt());
    }
}
