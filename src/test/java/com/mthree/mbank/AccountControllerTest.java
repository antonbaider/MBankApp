package com.mthree.mbank;

import com.mthree.bankmthree.dto.account.AccountDTO;
import com.mthree.bankmthree.dto.account.CloseAccountRequest;
import com.mthree.bankmthree.entity.Account;
import com.mthree.bankmthree.entity.User;
import com.mthree.bankmthree.entity.enums.CurrencyType;
import com.mthree.bankmthree.mapper.UserMapper;
import com.mthree.bankmthree.service.UserService;
import com.mthree.bankmthree.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountServiceImpl accountService;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserDetails userDetails;

    private User user;
    private Account account;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(); // Initialize a user object as needed
        user.getProfile().setUsername("testUser");

        account = new Account();
        account.setCardNumber("123456789");
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);
    }

    @Test
    public void testCreateAccount() {
        AccountDTO accountDTO = new AccountDTO(); // Fill with necessary data
        accountDTO.setCurrency(CurrencyType.USD);

        when(userDetails.getUsername()).thenReturn("testUser");
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(accountService.createAccount(any(), any())).thenReturn(account);
        when(userMapper.toAccountDTO(any())).thenReturn(accountDTO);

        ResponseEntity<ApiResponse> response = accountController.createAccount(accountDTO, userDetails);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Account created successfully!", response.getBody().getMessage());
        assertEquals(accountDTO, response.getBody().getData());
        verify(accountService).createAccount(user, accountDTO.getCurrency());
    }

    @Test
    public void testGetUserAccounts() {
        Set<AccountDTO> accountDTOs = new HashSet<>();
        accountDTOs.add(new AccountDTO()); // Add mock accounts as needed

        when(userDetails.getUsername()).thenReturn("testUser");
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(accountService.getUserAccounts(any())).thenReturn(accountDTOs);

        ResponseEntity<ApiResponse> response = accountController.getUserAccounts(userDetails);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Retrieved accounts successfully", response.getBody().getMessage());
        assertEquals(accountDTOs, response.getBody().getData());
        verify(accountService).getUserAccounts(user);
    }

    @Test
    public void testCloseAccount() {
        CloseAccountRequest request = new CloseAccountRequest();
        request.setCardNumber("123456789");

        when(userDetails.getUsername()).thenReturn("testUser");
        when(accountService.findAccountByCardNumber(anyString(), anyString())).thenReturn(account);
        when(userMapper.toAccountDTO(any())).thenReturn(new AccountDTO());

        ResponseEntity<ApiResponse> response = accountController.closeAccount(request, userDetails);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Account closed successfully!", response.getBody().getMessage());
        verify(accountService).closeAccount(request.getCardNumber(), "testUser");
    }
}