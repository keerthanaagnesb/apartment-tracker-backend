package com.apartment.tracker.service;

import com.apartment.tracker.model.Role;
import com.apartment.tracker.model.User;
import com.apartment.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean emailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerResident(String name, String email, String rawPassword, String phone, String flatNumber) {
        User user = new User(name, email, passwordEncoder.encode(rawPassword), phone, flatNumber, Role.RESIDENT, null);
        user.setApproved(false); // must be approved by an admin before they can log in
        return userRepository.save(user);
    }

    public List<User> getPendingResidents() {
        return userRepository.findByRoleAndApprovedFalse(Role.RESIDENT);
    }

    public void approveResident(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setApproved(true);
        userRepository.save(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("No user found for " + email));
    }

    public User getCurrentUser(Authentication authentication) {
        return getByEmail(authentication.getName());
    }

    public List<User> getAllTechnicians() {
        return userRepository.findByRole(Role.TECHNICIAN);
    }

    public List<User> getAllResidents() {
        return userRepository.findByRole(Role.RESIDENT);
    }
}
