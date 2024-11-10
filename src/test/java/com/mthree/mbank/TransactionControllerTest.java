package com.mthree.mbank;

import com.mthree.mbank.controller.ApiResponse;
import com.mthree.mbank.controller.TransactionController;
import com.mthree.mbank.dto.transaction.TransactionResponse;
import com.mthree.mbank.dto.transaction.TransferRequest;
import com.mthree.mbank.dto.transaction.TransferRequestByAccountId;
import com.mthree.mbank.entity.Account;
import com.mthree.mbank.entity.Transaction;
import com.mthree.mbank.entity.User;
import com.mthree.mbank.entity.enums.CurrencyType;
import com.mthree.mbank.exception.transaction.InsufficientFundsException;
import com.mthree.mbank.mapper.TransactionMapper;
import com.mthree.mbank.service.TransactionService;
import com.mthree.mbank.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the TransactionController class.
 * Uses Mockito to mock dependencies and verify interactions.
 */
@ExtendWith(MockitoExtension.class) // Use Mockito Extension for JUnit 5
public class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionMapper transactionMapper; // Mocked TransactionMapper

    @Mock
    private UserDetails userDetails;

    private Account senderAccount;
    private Account receiverAccount;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        // Initialize mock accounts
        senderAccount = new Account();
        senderAccount.setId(1L);
        senderAccount.setCurrency(CurrencyType.USD);

        receiverAccount = new Account();
        receiverAccount.setId(2L);
        receiverAccount.setCurrency(CurrencyType.USD);

        // Initialize mock transaction
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setSenderAccount(senderAccount);
        transaction.setReceiverAccount(receiverAccount);
    }

    /**
     * Test the transferMoney method of TransactionController.
     */
    @Test
    public void testTransferMoney() {
        // Arrange
        TransferRequestByAccountId transferRequest = new TransferRequestByAccountId();
        transferRequest.setSenderAccountId(1L);
        transferRequest.setReceiverAccountId(2L);
        transferRequest.setAmount(BigDecimal.valueOf(100));

        // Define mock TransactionResponse
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setTransactionId(transaction.getId());
        transactionResponse.setAmount(transaction.getAmount());
        transactionResponse.setTimestamp(transaction.getTimestamp());
        // Set other fields as necessary

        // Define mock behaviors
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(transactionService.transferMoneyBetweenUsers(
                transferRequest.getSenderAccountId(),
                transferRequest.getReceiverAccountId(),
                transferRequest.getAmount(),
                "user@example.com"
        )).thenReturn(transaction);

        when(transactionMapper.toResponse(transaction)).thenReturn(transactionResponse);

        // Act
        ResponseEntity<ApiResponse> response = transactionController.transferMoney(transferRequest, userDetails);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Transfer successful", response.getBody().getMessage());
        assertEquals(transactionResponse, response.getBody().getData());

        // Verify interactions
        verify(userDetails, times(1)).getUsername();
        verify(transactionService, times(1)).transferMoneyBetweenUsers(
                transferRequest.getSenderAccountId(),
                transferRequest.getReceiverAccountId(),
                transferRequest.getAmount(),
                "user@example.com"
        );
        verify(transactionMapper, times(1)).toResponse(transaction);
    }

    /**
     * Test the transferMoneyByCard method of TransactionController.
     */
    @Test
    public void testTransferMoneyByCard() {
        // Arrange
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSenderCardNumber("1234567890123456");
        transferRequest.setReceiverCardNumber("6543210987654321");
        transferRequest.setAmount(BigDecimal.valueOf(100));

        // Define mock TransactionResponse
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setTransactionId(2L);
        transactionResponse.setAmount(transferRequest.getAmount());
        transactionResponse.setTimestamp(LocalDateTime.now());
        // Set other fields as necessary

        // Define mock behaviors
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(transactionService.transferMoneyUsingCardNumbers(
                transferRequest,
                "user@example.com"
        )).thenReturn(transaction);
        when(transactionMapper.toResponse(transaction)).thenReturn(transactionResponse);

        // Act
        ResponseEntity<ApiResponse> response = transactionController.transferMoneyByCard(transferRequest, userDetails);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Transfer by card successful", response.getBody().getMessage());
        assertEquals(transactionResponse, response.getBody().getData());

        // Verify interactions
        verify(userDetails, times(1)).getUsername();
        verify(transactionService, times(1)).transferMoneyUsingCardNumbers(
                transferRequest,
                "user@example.com"
        );
        verify(transactionMapper, times(1)).toResponse(transaction);
    }

    /**
     * Test the getTransactionHistory method of TransactionController when no transactions exist.
     */
    @Test
    public void testGetTransactionHistory_NoTransactions() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L); // Ensure the user has a valid ID
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(userService.findByUsername("user@example.com")).thenReturn(mockUser);

        when(transactionService.getTransactionHistory(anyLong())).thenReturn(List.of());

        // Act
        ResponseEntity<ApiResponse> response = transactionController.getTransactionHistory(userDetails);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Transaction history retrieved successfully", response.getBody().getMessage());
        assertEquals("No transactions found", response.getBody().getData());

        // Verify interactions
        verify(userDetails, times(1)).getUsername();
        verify(userService, times(1)).findByUsername("user@example.com");
        verify(transactionService, times(1)).getTransactionHistory(anyLong());
        verifyNoInteractions(transactionMapper); // Since no transactions, mapper shouldn't be called
    }

    /**
     * Test the getTransactionHistory method of TransactionController when transactions exist.
     */
    @Test
    public void testGetTransactionHistory_WithTransactions() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L); // Ensure the user has a valid ID
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(userService.findByUsername("user@example.com")).thenReturn(mockUser);

        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setTransactionId(1L);
        transactionResponse.setAmount(BigDecimal.valueOf(100));
        transactionResponse.setTimestamp(LocalDateTime.now());
        // Set other fields as necessary

        when(transactionService.getTransactionHistory(anyLong())).thenReturn(List.of(transactionResponse));

        // Act
        ResponseEntity<ApiResponse> response = transactionController.getTransactionHistory(userDetails);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Transaction history retrieved successfully", response.getBody().getMessage());
        assertEquals(List.of(transactionResponse), response.getBody().getData());

        // Verify interactions
        verify(userDetails, times(1)).getUsername();
        verify(userService, times(1)).findByUsername("user@example.com");
        verify(transactionService, times(1)).getTransactionHistory(anyLong());
        verifyNoInteractions(transactionMapper); // Assuming TransactionMapper is not used in getTransactionHistory
    }

    /**
     * Test transferring money by card with insufficient funds.
     * Expects an InsufficientFundsException to be thrown.
     */
    @Test
    public void testTransferMoneyByCard_InsufficientFunds() {
        // Arrange
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSenderCardNumber("1234567890123456");
        transferRequest.setReceiverCardNumber("6543210987654321");
        transferRequest.setAmount(BigDecimal.valueOf(1000)); // Assume sender has insufficient funds

        // Define mock behaviors
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(transactionService.transferMoneyUsingCardNumbers(
                transferRequest,
                "user@example.com"
        )).thenThrow(new InsufficientFundsException("Insufficient funds for transfer."));

        // Act & Assert
        InsufficientFundsException exception = org.junit.jupiter.api.Assertions.assertThrows(
                InsufficientFundsException.class,
                () -> transactionController.transferMoneyByCard(transferRequest, userDetails)
        );

        assertEquals("Insufficient funds for transfer.", exception.getMessage());

        // Verify interactions
        verify(userDetails, times(1)).getUsername();
        verify(transactionService, times(1)).transferMoneyUsingCardNumbers(
                transferRequest,
                "user@example.com"
        );
        verifyNoInteractions(transactionMapper); // Since exception was thrown before mapping
    }
}