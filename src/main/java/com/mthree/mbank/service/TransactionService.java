package com.mthree.mbank.service;

import com.mthree.mbank.dto.transaction.TransactionResponse;
import com.mthree.mbank.dto.transaction.TransferRequest;
import com.mthree.mbank.entity.Transaction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * TransactionService interface defines operations related to transaction management
 * within the banking application. It provides methods for transferring money between users
 * and retrieving transaction history.
 */
public interface TransactionService {

    /**
     * Transfers money between accounts using the provided card numbers.
     * This operation will create a new transaction record.
     *
     * @param transferRequest the request containing sender and receiver card numbers and amount
     * @param username        the username of the authenticated user initiating the transfer
     * @return Transaction object representing the completed transaction
     */
    @Transactional
    Transaction transferMoneyUsingCardNumbers(@Valid TransferRequest transferRequest, String username);

    /**
     * Transfers money between users identified by their user IDs.
     * This operation will create a new transaction record.
     *
     * @param senderUserId    the ID of the user sending the money
     * @param receiverUserId  the ID of the user receiving the money
     * @param amount          the amount to transfer
     * @param username        the username of the authenticated user initiating the transfer
     * @return Transaction object representing the completed transaction
     */
    @Transactional
    Transaction transferMoneyBetweenUsers(@Valid Long senderUserId, @Valid Long receiverUserId,
                                          @Valid @Positive BigDecimal amount, @NotBlank String username);

    /**
     * Retrieves the transaction history for a specific user.
     * This method returns a list of TransactionResponse objects representing
     * all transactions associated with the user.
     *
     * @param userId the ID of the user whose transaction history is to be retrieved
     * @return List of TransactionResponse representing the user's transaction history
     */
    @Transactional(readOnly = true)
    List<TransactionResponse> getTransactionHistory(Long userId);
}