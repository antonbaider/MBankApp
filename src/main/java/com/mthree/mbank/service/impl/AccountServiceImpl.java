package com.mthree.mbank.service.impl;

import com.mthree.mbank.constants.MessageConstants;
import com.mthree.mbank.dto.account.AccountDTO;
import com.mthree.mbank.entity.Account;
import com.mthree.mbank.entity.User;
import com.mthree.mbank.entity.enums.CurrencyType;
import com.mthree.mbank.exception.account.AccountAlreadyExistsException;
import com.mthree.mbank.exception.account.AccountBalanceNotZeroException;
import com.mthree.mbank.exception.account.AccountsNotFoundException;
import com.mthree.mbank.exception.account.UniqueCardNumberGenerationException;
import com.mthree.mbank.mapper.UserMapper;
import com.mthree.mbank.repository.AccountRepository;
import com.mthree.mbank.service.AccountService;
import com.mthree.mbank.util.CardNumberGenerator;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing user accounts.
 * Handles operations such as creating, retrieving, and closing accounts.
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CardNumberGenerator cardNumberGenerator;
    private final UserMapper userMapper;
    private final int maxAttempts;

    public AccountServiceImpl(AccountRepository accountRepository,
                              CardNumberGenerator cardNumberGenerator,
                              UserMapper userMapper,
                              @Value("${card.number.maxAttempts}") int maxAttempts) {
        this.accountRepository = accountRepository;
        this.cardNumberGenerator = cardNumberGenerator;
        this.userMapper = userMapper;
        this.maxAttempts = maxAttempts;
    }

    /**
     * Creates and initializes a new Account for the specified user and currency.
     * Generates a unique card number and sets initial balance.
     *
     * @param currency The currency type for the new account.
     * @param user     The User entity for whom the account is being created.
     * @throws UniqueCardNumberGenerationException if a unique card number cannot be generated.
     */
    @Transactional
    @Override
    public Account createAndInitializeAccount(CurrencyType currency, User user) {
        log.info(MessageConstants.Logs.CREATING_NEW_ACCOUNT, user.getUsername(), currency);
        String cardNumber = generateUniqueCardNumber();

        // Initialize and save the new account
        Account account = Account.builder()
                .cardNumber(cardNumber)
                .currency(currency)
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();

        accountRepository.save(account);
        return account;
    }

    /**
     * Creates a new account for the specified user and currency, ensuring no duplicate accounts.
     *
     * @param user     The User entity for whom the account is being created.
     * @param currency The currency type for the new account.
     * @throws AccountAlreadyExistsException if an account with the specified currency already exists.
     */
    @Transactional
    @CachePut(value = "accounts", key = "#user.username + '-' + #currency")
    @Override
    public Account createAccount(User user, CurrencyType currency) {
        checkForExistingAccount(user, currency); // Check for an existing account
        return createAndInitializeAccount(currency, user);
    }

    /**
     * Retrieves all accounts associated with the specified user.
     *
     * @param user The User entity whose accounts are to be retrieved.
     * @return A set of AccountDTOs representing the user's accounts.
     */
    @Transactional(readOnly = true)
    @CacheEvict(value = "userAccounts", key = "#user.username")
    @Override
    public Set<AccountDTO> getUserAccounts(User user) {
        log.info("Retrieving accounts for user: {}", user.getUsername());

        // Fetch accounts directly from the account repository associated with the user
        Set<Account> accounts = accountRepository.findByUser(user);

        // Log the number of accounts retrieved for debugging purposes
        log.info("Number of accounts retrieved for user {}: {}", user.getUsername(), accounts.size());

        // Convert the set of Account entities to a set of AccountDTOs
        return accounts.stream()
                .map(userMapper::toAccountDTO)
                .collect(Collectors.toSet());
    }

    /**
     * Closes the specified account for the user, ensuring the balance is zero.
     *
     * @param cardNumber The card number of the account to be closed.
     * @param username   The username of the user attempting to close the account.
     * @throws AccountsNotFoundException      if the account is not found for the user.
     * @throws AccountBalanceNotZeroException if the account balance is not zero.
     */
    @Transactional
    @CacheEvict(value = "userAccounts", key = "#username")
    @Override
    public void closeAccount(String cardNumber, String username) {
        log.info(MessageConstants.Logs.CLOSING_ACCOUNT, cardNumber, username);

        Account account = accountRepository.findByCardNumberAndUser_Profile_Username(cardNumber, username)
                .orElseThrow(() -> new AccountsNotFoundException(MessageConstants.Exceptions.ACCOUNT_NOT_FOUND));

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            log.warn(MessageConstants.Logs.ATTEMPTED_TO_CLOSE_NON_ZERO_BALANCE, cardNumber);
            throw new AccountBalanceNotZeroException(MessageConstants.Exceptions.ACCOUNT_BALANCE_NON_ZERO);
        }

        accountRepository.delete(account);
        log.info(MessageConstants.Logs.ACCOUNT_CLOSED_SUCCESSFULLY, cardNumber);
    }

    /**
     * Retrieves an account by its card number and the username of the associated user.
     *
     * @param cardNumber The card number of the account.
     * @param username   The username of the user associated with the account.
     * @return The Account entity associated with the provided card number and username.
     * @throws AccountsNotFoundException if no account is found with the given card number and username.
     */
    @Transactional(readOnly = true)
    public Account findAccountByCardNumber(@NotBlank(message = "MessageConstants.Card number is required") String cardNumber, String username) {
        return accountRepository.findByCardNumberAndUser_Profile_Username(cardNumber, username)
                .orElseThrow(() -> new AccountsNotFoundException(MessageConstants.Exceptions.ACCOUNT_NOT_FOUND));
    }

    /**
     * Checks if the user already has an account with the specified currency.
     * Throws AccountAlreadyExistsException if an account with the same currency exists.
     *
     * @param user     The User entity for which to check the existing account.
     * @param currency The currency type to check for existing accounts.
     * @throws AccountAlreadyExistsException if an account with the specified currency exists.
     */
    private void checkForExistingAccount(User user, CurrencyType currency) {
        if (accountRepository.existsByUserAndCurrency(user, currency)) {
            String exceptionMsg = String.format(MessageConstants.Exceptions.ACCOUNT_ALREADY_EXISTS, currency);
            log.warn(exceptionMsg);
            throw new AccountAlreadyExistsException(exceptionMsg);
        }
    }

    /**
     * Generates a unique card number, handling the potential for failure.
     *
     * @return The generated unique card number.
     * @throws UniqueCardNumberGenerationException if a unique card number cannot be generated.
     */
    private String generateUniqueCardNumber() {
        try {
            return cardNumberGenerator.generateUniqueCardNumber();
        } catch (UniqueCardNumberGenerationException ex) {
            log.error(MessageConstants.Logs.UNABLE_TO_GENERATE_CARD_NUMBER, maxAttempts);
            throw new UniqueCardNumberGenerationException(String.format(MessageConstants.Logs.UNABLE_TO_GENERATE_CARD_NUMBER, maxAttempts));
        }
    }
}