package com.mthree.mbank.repository;

import com.mthree.mbank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Original method to find transactions by sender or receiver IDs
    List<Transaction> findBySenderIdOrReceiverIdOrderByTimestampDesc(Long senderId, Long receiverId);

    // New method to find transactions by user ID (sender or receiver)
    @Query("SELECT t FROM Transaction t WHERE t.sender.id = :userId OR t.receiver.id = :userId ORDER BY t.timestamp DESC")
    List<Transaction> findByUserIdOrderByTimestampDesc(@Param("userId") Long userId);
}