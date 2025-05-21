package com.example.bankcards.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "firstName must be not blank")
    private String firstName;

    @Column(name = "last_name",nullable = false)
    @NotBlank(message = "lastName must be not blank")
    private String lastName;

    @Column(name = "username", unique = true)
    @NotBlank(message = "Username must not be blank")
    @Size(min = 6, message = "Username must be at least 6 characters long.")
    private String username;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "password must be not blank")
    private String password;

    @Column(name = "is_ban", nullable = false)
    private Boolean isBan;

    @PrePersist
    public void init() {
        isBan = false;
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private final Set<Role> roles = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<Card> cards = new ArrayList<>();
}
