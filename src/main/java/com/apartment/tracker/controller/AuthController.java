package com.apartment.tracker.controller;

import com.apartment.tracker.dto.Dtos.*;
import com.apartment.tracker.model.User;
import com.apartment.tracker.repository.UserRepository;
import com.apartment.tracker.security.JwtService;
import com.apartment.tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.apartment.tracker.dto.Dtos;

/** Public endpoints: login (returns a JWT) and resident self-registration. */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid email or password."));
        }
        if (!user.isApproved()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Your account is still awaiting admin approval."));
        }
        return ResponseEntity.ok(new AuthResponse(jwtService.generateToken(user), Dtos.user(user)));
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userService.emailTaken(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("An account with this email already exists."));
        }
        userService.registerResident(request.name().trim(), email, request.password(),
                request.phone().trim(), request.flatNumber().trim());
        messagingTemplate.convertAndSend("/topic/complaints", "updated"); // lets the admin's pending list refresh live
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Account created. An admin must approve it before you can sign in."));
    }

    @GetMapping("/me")
    public UserDto me(Authentication authentication) {
        return Dtos.user(userService.getCurrentUser(authentication));
    }
}
