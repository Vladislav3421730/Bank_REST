package com.example.bankcards.service.impl;

import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.dto.TransactionFilterDto;
import com.example.bankcards.util.Constants;
import com.example.bankcards.util.mapper.TransactionMapper;
import com.example.bankcards.model.Transaction;
import com.example.bankcards.model.enums.OperationResult;
import com.example.bankcards.model.enums.OperationType;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.util.EnumValidation;
import com.example.bankcards.util.PredicateFactory;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;


    public Page<TransactionDto> findAll(PageRequest pageRequest, TransactionFilterDto transactionFilterDto) {

        OperationType operationTypeEnum = EnumValidation.safeParseEnum(OperationType.class, transactionFilterDto.operation());
        OperationResult operationResultEnum = EnumValidation.safeParseEnum(OperationResult.class, transactionFilterDto.operationResult());

        Specification<Transaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = PredicateFactory.formPredicates(cb, root,
                    transactionFilterDto,
                    operationTypeEnum,
                    operationResultEnum);
            query.orderBy(cb.desc(root.get(Constants.TIMESTAMP_COLUMN)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return  transactionRepository.findAll(spec, pageRequest)
                .map(transactionMapper::toDto);
    }

    @Override
    public Page<TransactionDto> findAllByCardId(UUID id, PageRequest pageRequest) {
        return transactionRepository.findByCardIdOrderByTimestampDesc(id, pageRequest)
                .map(transactionMapper::toDto);
    }

    @Override
    public Page<TransactionDto> findAllByUserId(UUID id, PageRequest pageRequest) {
        return transactionRepository.findByCardUserIdOrderByTimestampDesc(id, pageRequest)
                .map(transactionMapper::toDto);
    }

    @Override
    public Page<TransactionDto> findAllByUsername(String username, PageRequest pageRequest) {
        return transactionRepository.findByCardUserUsernameOrderByTimestampDesc(username, pageRequest)
                .map(transactionMapper::toDto);
    }

    @Override
    public Page<TransactionDto> findAllByUsernameAndCardId(String username, UUID id, PageRequest pageRequest) {
        return transactionRepository.findByCardUserUsernameAndCardIdOrderByTimestampDesc(username, id, pageRequest)
                .map(transactionMapper::toDto);
    }
}
