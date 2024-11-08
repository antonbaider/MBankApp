package com.mthree.mbank.controller;

import com.mthree.mbank.dto.account.AccountDTO;
import com.mthree.mbank.dto.account.CloseAccountRequest;
import com.mthree.mbank.entity.Account;
import com.mthree.mbank.mapper.UserMapper;
import com.mthree.mbank.service.AccountService;
import com.mthree.mbank.service.UserService;
import com.mthree.mbank.service.impl.AccountServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Controller class responsible for handling HTTP requests related to user bank accounts.
 * Provides endpoints for creating accounts, retrieving user accounts, and closing accounts.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Account Controller", description = "Endpoints for user bank accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public AccountController(UserService userService, AccountService accountService, UserMapper userMapper) {
        this.userService = userService;
        this.accountService = accountService;
        this.userMapper = userMapper;
    }

    /**
     * Endpoint to create a new bank account for the authenticated user in a specified currency.
     *
     * @param accountDTO  The data transfer object containing account creation details, such as currency.
     * @param userDetails The details of the currently authenticated user, automatically injected by Spring Security.
     * @return A ResponseEntity containing a detailed success message upon successful account creation.
     */
    @Operation(summary = "Create an account in a specific currency")
    @PostMapping("/accounts")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createAccount(@Valid @RequestBody AccountDTO accountDTO, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        var user = userService.findByUsername(username);
        Account account = accountService.createAccount(user, accountDTO.getCurrency());
        AccountDTO createdAccountDTO = userMapper.toAccountDTO(account);

        return ResponseEntity.ok(new ApiResponse("Account created successfully!", createdAccountDTO));
    }

    /**
     * Endpoint to retrieve all bank accounts associated with the authenticated user.
     *
     * @param userDetails The details of the currently authenticated user, automatically injected by Spring Security.
     * @return A ResponseEntity containing a set of AccountDTOs representing the user's accounts.
     */
    @Operation(summary = "Get all accounts of the current user")
    @GetMapping("/accounts")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getUserAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        var user = userService.findByUsername(username);
        Set<AccountDTO> accountDTOs = accountService.getUserAccounts(user);
        return ResponseEntity.ok(new ApiResponse("Retrieved accounts successfully", accountDTOs));
    }

    /**
     * Endpoint to close a bank account for the authenticated user, provided the account balance is zero.
     *
     * @param request     The request payload containing the card number of the account to be closed.
     * @param userDetails The details of the currently authenticated user, automatically injected by Spring Security.
     * @return A ResponseEntity containing a success message upon successful account closure.
     */
    @Operation(summary = "Close an account if the balance is zero")
    @DeleteMapping("/accounts/close")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> closeAccount(@RequestBody CloseAccountRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        // Fetch the account before closing it
        Account accountToClose = accountService.findAccountByCardNumber(request.getCardNumber(), username);

        // Map the account to DTO for response
        AccountDTO closedAccountDTO = userMapper.toAccountDTO(accountToClose);

        // Close the account
        accountService.closeAccount(request.getCardNumber(), username);

        return ResponseEntity.ok(new ApiResponse("Account closed successfully!", closedAccountDTO));
    }
}