package com.mthree.mbank.mapper;

import com.mthree.mbank.dto.transaction.TransactionResponse;
import com.mthree.mbank.entity.Account;
import com.mthree.mbank.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    // Mapping properties with explicit source paths for 'id' properties
    @Mapping(source = "transaction.id", target = "transactionId") // Mapping from Transaction entity
    @Mapping(source = "transaction.senderAccount.id", target = "senderAccountId") // Mapping senderAccount's id
    @Mapping(source = "transaction.receiverAccount.id", target = "receiverAccountId") // Mapping receiverAccount's id
    @Mapping(expression = "java(transaction.getSenderAccount() != null ? transaction.getSenderAccount().getCardNumber() : null)", target = "senderCardNumber")
    @Mapping(expression = "java(transaction.getReceiverAccount() != null ? transaction.getReceiverAccount().getCardNumber() : null)", target = "receiverCardNumber")
    @Mapping(source = "transaction.amount", target = "amount")
    @Mapping(expression = "java(transaction.getSenderAccount() != null ? transaction.getSenderAccount().getCurrency().toString() : null)", target = "currency")
    @Mapping(expression = "java(transaction.getSenderAccount() != null ? transaction.getSenderAccount().getBalance() : null)", target = "balanceAfter")
    @Mapping(source = "transaction.timestamp", target = "timestamp")
    TransactionResponse toResponse(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "sender", target = "senderAccount")
    @Mapping(source = "receiver", target = "receiverAccount")
    @Mapping(source = "sender.user", target = "sender") // Nested User in sender Account
    @Mapping(source = "receiver.user", target = "receiver") // Nested User in receiver Account
    Transaction toTransactionById(Account sender, Account receiver, BigDecimal amount);
}