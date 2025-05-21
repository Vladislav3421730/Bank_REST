package com.example.bankcards.util.factory;

import com.example.bankcards.model.Card;
import com.example.bankcards.model.Transaction;
import com.example.bankcards.model.enums.OperationResult;
import com.example.bankcards.model.enums.OperationType;

import java.math.BigDecimal;

public class TransactionFactory {

    public static Transaction create(Card card,
                                     BigDecimal amount,
                                     OperationType operationType,
                                     OperationResult operationResult) {
        return Transaction.builder()
                .card(card)
                .operation(operationType)
                .operationResult(operationResult)
                .amount(amount)
                .build();
    }

    public static Transaction create(Card card,
                                     Card targetCard,
                                     BigDecimal amount,
                                     OperationType operationType,
                                     OperationResult operationResult) {
        return Transaction.builder()
                .card(card)
                .operation(operationType)
                .operationResult(operationResult)
                .targetCard(targetCard)
                .amount(amount)
                .build();
    }
}
