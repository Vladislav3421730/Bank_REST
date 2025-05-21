package com.example.bankcards.repository;

import com.example.bankcards.model.BlockRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BlockRequestRepository extends JpaRepository<BlockRequest, UUID> {

    Page<BlockRequest> findByCardId(UUID id, PageRequest pageRequest);

    Page<BlockRequest> findByUserId(UUID id, PageRequest pageRequest);

    Optional<BlockRequest> findFirstByCardIdOrderByCreatedAtDesc(UUID id);
}
