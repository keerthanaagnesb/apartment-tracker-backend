package com.apartment.tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    private String password;

    private String phone;

    @Column(name = "flat_number")
    private String flatNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    /** Only used for TECHNICIAN accounts, e.g. "Electrical", "Plumbing / Water" */
    private String department;

    /** New residents start unapproved and cannot log in until an admin approves them. */
    @Column(nullable = false)
    private boolean approved = true;

    public User(String name, String email, String password, String phone, String flatNumber, Role role, String department) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.flatNumber = flatNumber;
        this.role = role;
        this.department = department;
    }
}
