package com.mthree.mbank.mapper;

import com.mthree.mbank.dto.transaction.TransactionResponse;
import com.mthree.mbank.entity.Account;
import com.mthree.mbank.entity.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-08T23:20:57+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
@Component
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public TransactionResponse toResponse(Transaction transaction) {
        if ( transaction == null ) {
            return null;
        }

        Long transactionId = null;
        Long senderAccountId = null;
        Long receiverAccountId = null;
        BigDecimal amount = null;
        LocalDateTime timestamp = null;

        transactionId = transaction.getId();
        senderAccountId = transactionSenderAccountId( transaction );
        receiverAccountId = transactionReceiverAccountId( transaction );
        amount = transaction.getAmount();
        timestamp = transaction.getTimestamp();

        String senderCardNumber = transaction.getSenderAccount() != null ? transaction.getSenderAccount().getCardNumber() : null;
        String receiverCardNumber = transaction.getReceiverAccount() != null ? transaction.getReceiverAccount().getCardNumber() : null;
        String currency = transaction.getSenderAccount() != null ? transaction.getSenderAccount().getCurrency().toString() : null;
        BigDecimal balanceAfter = transaction.getSenderAccount() != null ? transaction.getSenderAccount().getBalance() : null;

        TransactionResponse transactionResponse = new TransactionResponse( transactionId, senderAccountId, senderCardNumber, receiverAccountId, receiverCardNumber, amount, currency, balanceAfter, timestamp );

        return transactionResponse;
    }

    @Override
    public Transaction toTransactionById(Account sender, Account receiver, BigDecimal amount) {
        if ( sender == null && receiver == null && amount == null ) {
            return null;
        }

        Transaction transaction = new Transaction();

        if ( sender != null ) {
            transaction.setSenderAccount( sender );
            transaction.setSender( sender.getUser() );
        }
        if ( receiver != null ) {
            transaction.setReceiverAccount( receiver );
            transaction.setReceiver( receiver.getUser() );
        }
        transaction.setAmount( amount );

        return transaction;
    }

    private Long transactionSenderAccountId(Transaction transaction) {
        if ( transaction == null ) {
            return null;
        }
        Account senderAccount = transaction.getSenderAccount();
        if ( senderAccount == null ) {
            return null;
        }
        Long id = senderAccount.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long transactionReceiverAccountId(Transaction transaction) {
        if ( transaction == null ) {
            return null;
        }
        Account receiverAccount = transaction.getReceiverAccount();
        if ( receiverAccount == null ) {
            return null;
        }
        Long id = receiverAccount.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
