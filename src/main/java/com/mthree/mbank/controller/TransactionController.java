package com.mthree.mbank.controller;

import com.mthree.mbank.dto.transaction.TransactionResponse;
import com.mthree.mbank.dto.transaction.TransferRequest;
import com.mthree.mbank.dto.transaction.TransferRequestByUserId;
import com.mthree.mbank.entity.Transaction;
import com.mthree.mbank.mapper.TransactionMapper;
import com.mthree.mbank.service.TransactionService;
import com.mthree.mbank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for handling transaction-related operations.
 * This includes transferring money between users and retrieving transaction history.
 */
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions Controller", description = "Endpoints for user transactions")
public class TransactionController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionController(UserService userService, TransactionService transactionService, TransactionMapper transactionMapper) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }

    /**
     * Endpoint to transfer money between users identified by their account IDs.
     * Only accessible to users with 'USER' or 'ADMIN' roles.
     *
     * @param transferRequest the transfer request containing sender and receiver account IDs and amount
     * @param userDetails     the details of the authenticated user
     * @return ResponseEntity containing a success message and transaction details
     */
    @Operation(summary = "Transfer money between users by account_id")
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> transferMoney(
            @Valid @RequestBody TransferRequestByUserId transferRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Perform the transfer operation and get the transaction response
        TransactionResponse response = getTransactionResponse(transferRequest, userDetails);

        // Return a success response encapsulated in ApiResponse
        return ResponseEntity.ok(new ApiResponse("Transfer successful", response));
    }

    /**
     * Endpoint to transfer money between accounts using account numbers.
     * Only accessible to users with 'USER' or 'ADMIN' roles.
     *
     * @param transferRequest the transfer request containing account numbers and amount
     * @param userDetails     the details of the authenticated user
     * @return ResponseEntity containing a success message and transaction details
     */
    @Operation(summary = "Transfer money between accounts using account numbers")
    @PostMapping("/transferByCard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> transferMoneyByCard(
            @Valid @RequestBody TransferRequest transferRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Perform the transfer operation and get the transaction response
        TransactionResponse response = getTransactionResponse(transferRequest, userDetails);

        // Return a success response encapsulated in ApiResponse
        return ResponseEntity.ok(new ApiResponse("Transfer by card successful", response));
    }

    /**
     * Endpoint to get the transaction history for the authenticated user.
     * Only accessible to users with 'USER' or 'ADMIN' roles.
     *
     * @param userDetails the details of the authenticated user
     * @return ResponseEntity containing a list of transaction history
     */
    @Operation(summary = "Get transaction history for the authenticated user")
    @GetMapping("/history")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getTransactionHistory(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        List<TransactionResponse> history = transactionService.getTransactionHistory(userId);
        if (history.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse("Transaction history retrieved successfully", "No transactions found"));
        }
        // Return the transaction history in a success response encapsulated in ApiResponse
        return ResponseEntity.ok(new ApiResponse("Transaction history retrieved successfully", history));
    }

    // Private helper methods for transaction response handling
    private TransactionResponse getTransactionResponse(TransferRequestByUserId transferRequest, UserDetails userDetails) {
        Transaction transaction = transactionService.transferMoneyBetweenUsers(
                transferRequest.getSenderAccountId(),
                transferRequest.getReceiverAccountId(),
                transferRequest.getAmount(),
                userDetails.getUsername());

        return transactionMapper.toResponse(transaction);
    }

    private TransactionResponse getTransactionResponse(TransferRequest transferRequest, UserDetails userDetails) {
        Transaction transaction = transactionService.transferMoneyUsingCardNumbers(transferRequest, userDetails.getUsername());

        return transactionMapper.toResponse(transaction);
    }
}