package com.example.bankcards.util;

import com.example.bankcards.dto.FilterCardDto;
import com.example.bankcards.dto.TransactionFilterDto;
import com.example.bankcards.model.Card;
import com.example.bankcards.model.Transaction;
import com.example.bankcards.model.enums.OperationResult;
import com.example.bankcards.model.enums.OperationType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PredicateFactory {

    private static final String FIELD_AMOUNT = "amount";
    private static final String FIELD_OPERATION = "operation";
    private static final String FIELD_OPERATION_RESULT = "operationResult";
    private static final String FIELD_BALANCE = "balance";
    private static final String FIELD_EXPIRATION_DATE = "expirationDate";

    public List<Predicate> formPredicates(CriteriaBuilder cb,
                                          Root<Transaction> root,
                                          TransactionFilterDto transactionFilterDto,
                                          OperationType operation,
                                          OperationResult operationResult) {

        List<Predicate> predicates = new ArrayList<>();

        if (transactionFilterDto.minAmount() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(FIELD_AMOUNT), transactionFilterDto.minAmount()));
        }
        if (transactionFilterDto.maxAmount() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(FIELD_AMOUNT), transactionFilterDto.maxAmount()));
        }
        if (operation != null) {
            predicates.add(cb.equal(root.get(FIELD_OPERATION), operation));
        }
        if (operationResult != null) {
            predicates.add(cb.equal(root.get(FIELD_OPERATION_RESULT), operationResult));
        }
        return predicates;
    }

    public List<Predicate> formPredicates(CriteriaBuilder cb, Root<Card> root, FilterCardDto filterCardDto) {

        List<Predicate> predicates = new ArrayList<>();

        if (filterCardDto.minBalance() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(FIELD_BALANCE), filterCardDto.minBalance()));
        }
        if (filterCardDto.maxBalance() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(FIELD_BALANCE), filterCardDto.maxBalance()));
        }
        if (filterCardDto.expiredBefore() != null) {
            predicates.add(cb.lessThan(root.get(FIELD_EXPIRATION_DATE), filterCardDto.expiredBefore()));
        }
        if (filterCardDto.expiredAfter() != null) {
            predicates.add(cb.greaterThan(root.get(FIELD_EXPIRATION_DATE), filterCardDto.expiredAfter()));
        }
        return predicates;
    }
}
