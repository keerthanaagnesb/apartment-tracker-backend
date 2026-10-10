package com.apartment.tracker.config;

import com.apartment.tracker.model.*;
import com.apartment.tracker.repository.ChatMessageRepository;
import com.apartment.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates the demo accounts the first time the app starts against an EMPTY database.
 * Works on any database (unlike a data.sql file) and is safe to run on every restart:
 * if users already exist it does nothing.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        String pw = passwordEncoder.encode("password123");

        User admin = userRepository.save(new User("Ravi Kumar (Admin)", "admin@apartment.com", pw, "9000000001", "Office", Role.ADMIN, null));
        userRepository.save(new User("Suresh (Electrician)", "tech.electric@apartment.com", pw, "9000000002", "Maint. Block", Role.TECHNICIAN, "Electrical"));
        userRepository.save(new User("Mahesh (Plumber)", "tech.plumber@apartment.com", pw, "9000000003", "Maint. Block", Role.TECHNICIAN, "Plumbing / Water"));
        userRepository.save(new User("Agnes", "resident1@apartment.com", pw, "9000000004", "A-101", Role.RESIDENT, null));
        userRepository.save(new User("Vikram Singh", "resident2@apartment.com", pw, "9000000005", "B-202", Role.RESIDENT, null));

        chatMessageRepository.save(new ChatMessage(admin,
                "Welcome to the community chat! Use this space for announcements and discussions."));
    }
}
