package com.example.bankcards.repository;

import com.example.bankcards.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = {"cards"})
    Optional<User> findByUsername(String username);

    @Override
    @EntityGraph(attributePaths = {"cards"})
    Optional<User> findById(UUID uuid);

    @Override
    @EntityGraph(attributePaths = {"cards"})
    Page<User> findAll(Pageable pageable);
}
