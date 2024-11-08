package com.mthree.mbank.listener;

import com.mthree.mbank.entity.Transaction;
import jakarta.persistence.PrePersist;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionListener {

    @PrePersist
    public void setTimestamp(Transaction transaction) {
        transaction.setTimestamp(LocalDateTime.now());
    }
}