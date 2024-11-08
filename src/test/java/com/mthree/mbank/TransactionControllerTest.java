package com.mthree.mbank;

import com.mthree.bankmthree.dto.transaction.TransferRequestByUserId;
import com.mthree.bankmthree.entity.Account;
import com.mthree.bankmthree.entity.Transaction;
import com.mthree.bankmthree.entity.enums.CurrencyType;
import com.mthree.bankmthree.service.TransactionService;
import com.mthree.bankmthree.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTransferMoney() {
        // Arrange
        TransferRequestByUserId transferRequest = new TransferRequestByUserId();
        transferRequest.setSenderAccountId(1L); // Assume valid account ID
        transferRequest.setReceiverAccountId(2L); // Assume valid account ID
        transferRequest.setAmount(BigDecimal.valueOf(100)); // Set the transfer amount

        // Create mock accounts
        Account senderAccount = new Account();
        senderAccount.setId(1L); // Set sender account ID
        senderAccount.setCurrency(CurrencyType.USD); // Set the currency for the sender account

        Account receiverAccount = new Account();
        receiverAccount.setId(2L); // Set receiver account ID
        receiverAccount.setCurrency(CurrencyType.USD); // Set the currency for the receiver account

        // Create a transaction and set the accounts
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setSenderAccount(senderAccount); // Set sender account
        transaction.setReceiverAccount(receiverAccount); // Set receiver account

        // Set up mocks
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(transactionService.transferMoneyBetweenUsers(
                transferRequest.getSenderAccountId(),
                transferRequest.getReceiverAccountId(),
                transferRequest.getAmount(),
                userDetails.getUsername())).thenReturn(transaction);

        // Act
        ResponseEntity<ApiResponse> response = transactionController.transferMoney(transferRequest, userDetails);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Transfer successful", response.getBody().getMessage());
        //  assertEquals(transaction.getId(), response.getBody().getData().getTransactionId());
    }
}