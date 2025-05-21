package com.example.bankcards.repository;


import com.example.bankcards.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findByCardIdOrderByTimestampDesc(UUID id, PageRequest pageRequest);



    Page<Transaction> findByCardUserIdOrderByTimestampDesc(UUID id, PageRequest pageRequest);

    Page<Transaction> findByCardUserUsernameOrderByTimestampDesc(String email, PageRequest pageRequest);

    Page<Transaction> findByCardUserUsernameAndCardIdOrderByTimestampDesc(String email, UUID id, PageRequest pageRequest);


}
