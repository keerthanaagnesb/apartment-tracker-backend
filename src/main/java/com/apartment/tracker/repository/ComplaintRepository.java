package com.apartment.tracker.repository;

import com.apartment.tracker.model.Complaint;
import com.apartment.tracker.model.ComplaintStatus;
import com.apartment.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByCreatedByOrderByCreatedAtDesc(User createdBy);
    List<Complaint> findByAssignedToOrderByCreatedAtDesc(User assignedTo);
    List<Complaint> findAllByOrderByCreatedAtDesc();
    long countByStatus(ComplaintStatus status);
}
