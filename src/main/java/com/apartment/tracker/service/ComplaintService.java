package com.apartment.tracker.service;

import com.apartment.tracker.model.*;
import com.apartment.tracker.repository.ComplaintRepository;
import com.apartment.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /** Pushes a tiny broadcast so every open dashboard (any user, any tab) live-refreshes itself. */
    private void notifyComplaintsChanged() {
        messagingTemplate.convertAndSend("/topic/complaints", "updated");
    }

    public Complaint raiseComplaint(User resident, String title, IssueType issueType, String description, String location) {
        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setIssueType(issueType);
        complaint.setDescription(description);
        complaint.setLocation(location);
        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setCreatedBy(resident);

        Complaint saved = complaintRepository.save(complaint);
        saved.setComplaintCode(String.format("APT-%04d", saved.getId()));
        Complaint result = complaintRepository.save(saved);
        notifyComplaintsChanged();
        return result;
    }

    public List<Complaint> getComplaintsForResident(User resident) {
        return complaintRepository.findByCreatedByOrderByCreatedAtDesc(resident);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Complaint> getComplaintsAssignedTo(User technician) {
        return complaintRepository.findByAssignedToOrderByCreatedAtDesc(technician);
    }

    public Complaint assignTechnician(Long complaintId, Long technicianId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found"));

        complaint.setAssignedTo(technician);
        if (complaint.getStatus() == ComplaintStatus.PENDING) {
            complaint.setStatus(ComplaintStatus.ASSIGNED);
        }
        Complaint result = complaintRepository.save(complaint);
        notifyComplaintsChanged();
        return result;
    }

    public Complaint updateStatus(Long complaintId, ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
        complaint.setStatus(status);
        Complaint result = complaintRepository.save(complaint);
        notifyComplaintsChanged();
        return result;
    }

    public long countByStatus(ComplaintStatus status) {
        return complaintRepository.countByStatus(status);
    }

    public long countAll() {
        return complaintRepository.count();
    }
}
