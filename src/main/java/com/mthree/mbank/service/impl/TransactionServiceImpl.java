package com.mthree.mbank.service.impl;

import com.mthree.mbank.constants.MessageConstants;
import com.mthree.mbank.dto.transaction.TransactionResponse;
import com.mthree.mbank.dto.transaction.TransferRequest;
import com.mthree.mbank.exception.transaction.InvalidCardNumberException;
import com.mthree.mbank.service.TransactionService;
import com.mthree.mbank.entity.Account;
import com.mthree.mbank.entity.Transaction;
import com.mthree.mbank.entity.User;
import com.mthree.mbank.entity.enums.Role;
import com.mthree.mbank.exception.account.AccountsNotFoundException;
import com.mthree.mbank.exception.account.ReceiverAccountNotFoundException;
import com.mthree.mbank.exception.transaction.UnauthorizedTransferException;
import com.mthree.mbank.mapper.TransactionMapper;
import com.mthree.mbank.repository.AccountRepository;
import com.mthree.mbank.repository.TransactionRepository;
import com.mthree.mbank.service.EmailService;
import com.mthree.mbank.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing transactions.
 */
@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final TransactionMapper transactionMapper;
    private final EmailService emailService;

    @Autowired
    public TransactionServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository, UserService userService, TransactionMapper transactionMapper, EmailService emailService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.transactionMapper = transactionMapper;
        this.emailService = emailService;
    }

    /**
     * Transfers money between two accounts using card numbers.
     *
     * @param transferRequest the transfer request containing sender and receiver card numbers and amount
     * @param username        the username initiating the transfer
     * @return the created transaction
     */
    @Transactional
    @CacheEvict(value = "transactionHistory", allEntries = true) // Clear cache for transaction history
    @Override
    public Transaction transferMoneyUsingCardNumbers(@Valid TransferRequest transferRequest, String username) {
            // Logging the start of the transfer
            log.info(MessageConstants.Logs.TRANSFER_STARTED,
                    maskCardNumber(transferRequest.getSenderCardNumber()),
                    maskCardNumber(transferRequest.getReceiverCardNumber()));

            // Validating card number formats
            validateCardNumberFormat(transferRequest.getSenderCardNumber());
            validateCardNumberFormat(transferRequest.getReceiverCardNumber());

            // Validating same-account transfer
            validateTransferRequest(transferRequest);

            // Fetching sender and receiver accounts
            Account sender = accountRepository.findByCardNumber(transferRequest.getSenderCardNumber())
                    .orElseThrow(() -> new AccountsNotFoundException(MessageConstants.Exceptions.SENDER_ACCOUNT_NOT_FOUND));
            Account receiver = accountRepository.findByCardNumber(transferRequest.getReceiverCardNumber())
                    .orElseThrow(() -> new AccountsNotFoundException(MessageConstants.Exceptions.RECEIVER_ACCOUNT_NOT_FOUND));

            // Validating the transfer details
            validateTransfer(sender, receiver, transferRequest.getAmount(), username);

            // Performing the transfer
            sender.setBalance(sender.getBalance().subtract(transferRequest.getAmount()));
            receiver.setBalance(receiver.getBalance().add(transferRequest.getAmount()));

            accountRepository.save(sender);
            accountRepository.save(receiver);

            // Creating the transaction record
            Transaction transaction = new Transaction();
            transaction.setAmount(transferRequest.getAmount());
            transaction.setSenderAccount(sender);
            transaction.setReceiverAccount(receiver);
            transaction.setSender(sender.getUser());
            transaction.setReceiver(receiver.getUser());

            Transaction completedTransaction = transactionRepository.save(transaction);

            // Logging the successful transfer
            log.info(MessageConstants.Logs.TRANSFER_COMPLETED,
                    maskCardNumber(transferRequest.getSenderCardNumber()),
                    maskCardNumber(transferRequest.getReceiverCardNumber()));

            // Sending transaction email notification
            emailService.sendTransactionEmail(completedTransaction);

            return completedTransaction; // Return the completed transaction
    }

    /**
     * Transfers money between two users.
     *
     * @param senderUserId   the sender's user ID
     * @param receiverUserId the receiver's user ID
     * @param amount         the amount to transfer
     * @param username       the username initiating the transfer
     * @return the created transaction
     */
    @Transactional
    @CacheEvict(value = "transactionHistory", allEntries = true) // Clear cache for transaction history
    @Override
    public Transaction transferMoneyBetweenUsers(@Valid Long senderUserId, @Valid Long receiverUserId, @Valid @Positive BigDecimal amount, @NotBlank String username) {
            // Fetching sender and receiver accounts by user IDs
            Account sender = accountRepository.findById(senderUserId)
                    .orElseThrow(() -> new AccountsNotFoundException(MessageConstants.Exceptions.SENDER_ACCOUNT_NOT_FOUND));

            Account receiver = accountRepository.findById(receiverUserId)
                    .orElseThrow(() -> new ReceiverAccountNotFoundException(MessageConstants.Exceptions.RECEIVER_ACCOUNT_NOT_FOUND));

            // Validation: Prevent transferring to the same account
            UserTransferRequestValidation(sender, receiver);

            // Validating the transfer details
            validateTransfer(sender, receiver, amount, username);

            // Performing the transfer
            sender.setBalance(sender.getBalance().subtract(amount));
            receiver.setBalance(receiver.getBalance().add(amount));

            // Saving updated accounts
            accountRepository.save(sender);
            accountRepository.save(receiver);

            // Map to Transaction using the mapper
            Transaction mapperTransactionById = transactionMapper.toTransactionById(sender, receiver, amount);

            // Logging the successful transfer between users
            log.info(MessageConstants.Logs.TRANSFER_BETWEEN_USERS_COMPLETED, amount, sender.getUser().getUsername(), receiver.getUser().getUsername());

            // Saving the transaction and sending email notification
            Transaction completedTransaction = transactionRepository.save(mapperTransactionById);
            emailService.sendTransactionEmail(completedTransaction);

            return completedTransaction; // Return the completed transaction
    }

    // Validation of transfer request
    private void UserTransferRequestValidation(Account sender, Account receiver) {
        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException(MessageConstants.Exceptions.SAME_ACCOUNT_TRANSFER);
        }
    }

    /**
     * Retrieves the transaction history for a user.
     *
     * @param userId the user ID
     * @return a list of TransactionResponse objects representing the transaction history
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "transactionHistory", key = "#userId")
    @Override
    public List<TransactionResponse> getTransactionHistory(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTimestampDesc(userId);
        return transactions.stream().map(transactionMapper::toResponse).collect(Collectors.toList());
    }

    /**
     * Validates the transfer request by checking card number formats and preventing same-account transfers.
     *
     * @param transferRequest the transfer request containing sender and receiver card numbers
     */
    private void validateTransferRequest(TransferRequest transferRequest) {
        // Prevent transferring to the same account
        if (transferRequest.getSenderCardNumber().equals(transferRequest.getReceiverCardNumber())) {
            throw new IllegalArgumentException(MessageConstants.Exceptions.SAME_ACCOUNT_TRANSFER);
        }
    }

    /**
     * Validates the transfer details to ensure compliance with business rules.
     *
     * @param sender   the sender's account
     * @param receiver the receiver's account
     * @param amount   the amount to transfer
     * @param username the username initiating the transfer
     */
    private void validateTransfer(Account sender, Account receiver, BigDecimal amount, String username) {
        // Logging the validation process
        log.info(MessageConstants.Logs.VALIDATING_TRANSFER, username);

        User user = userService.findByUsername(username);
        boolean isAdmin = user.getRole().equals(Role.ROLE_ADMIN);

        // Check for authorization
        if (!isAdmin && !sender.getUser().getUsername().equals(username)) {
            throw new UnauthorizedTransferException(MessageConstants.Exceptions.UNAUTHORIZED_TRANSFER);
        }

        // Check for currency mismatch
        if (!sender.getCurrency().equals(receiver.getCurrency())) {
            throw new IllegalArgumentException(MessageConstants.Exceptions.CURRENCY_MISMATCH);
        }

        // Check for valid transfer amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(MessageConstants.Exceptions.INVALID_TRANSFER_AMOUNT);
        }

        // Check for sufficient funds
        if (!isAdmin && sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException(MessageConstants.Exceptions.INSUFFICIENT_BALANCE);
        }
    }

    /**
     * Validates the format of the card number using length check and Luhn algorithm.
     *
     * @param cardNumber the card number to validate
     */
    private void validateCardNumberFormat(String cardNumber) {
        if (cardNumber == null || !cardNumber.matches("\\d{13,19}")) { // 13 to 19 digits
            throw new InvalidCardNumberException(MessageConstants.Exceptions.INVALID_CARD_NUMBER_FORMAT);
        }

        if (!isValidLuhn(cardNumber)) {
            throw new InvalidCardNumberException(MessageConstants.Exceptions.INVALID_CARD_NUMBER_FORMAT);
        }
    }

    /**
     * Implements the Luhn algorithm to validate the card number.
     *
     * @param cardNumber the card number to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }


    /**
     * Masks the card number for security purposes.
     *
     * @param cardNumber the original card number
     * @return the masked card number
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}