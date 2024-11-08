package com.mthree.mbank;

import com.mthree.mbank.controller.AccountController;
import com.mthree.mbank.controller.ApiResponse;
import com.mthree.mbank.dto.account.AccountDTO;
import com.mthree.mbank.entity.Account;
import com.mthree.mbank.entity.User;
import com.mthree.mbank.entity.UserProfile;
import com.mthree.mbank.entity.enums.CurrencyType;
import com.mthree.mbank.mapper.UserMapper;
import com.mthree.mbank.service.AccountService;
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
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AccountController class.
 * Uses Mockito to mock dependencies and verify interactions.
 */
@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserDetails userDetails;

    private User user;
    private UserProfile userProfile;
    private Account account;

    @BeforeEach
    public void setUp() {
        // Initialize UserProfile
        userProfile = UserProfile.builder()
                .id(1L)
                .username("testUser")
                .password("encodedPassword") // Assume password is already encoded
                .firstName("Test")
                .lastName("User")
                .email("testuser@example.com")
                .phone("1234567890")
                .ssn("123-45-6789")
                .build();

        // Initialize User with UserProfile
        user = User.builder()
                .id(1L)
                .profile(userProfile)
                .role(com.mthree.mbank.entity.enums.Role.ROLE_USER)
                .status(com.mthree.mbank.entity.enums.Status.ACTIVE)
                .type(com.mthree.mbank.entity.enums.UserType.STANDARD)
                .build();

        // Initialize Account
        account = Account.builder()
                .id(1L)
                .cardNumber("1234567890123456")
                .balance(BigDecimal.ZERO)
                .currency(CurrencyType.USD)
                .user(user)
                .build();
    }

    @Test
    public void testCreateAccount() {
        // Prepare test data
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setCurrency(CurrencyType.USD);

        AccountDTO createdAccountDTO = new AccountDTO();
        createdAccountDTO.setCardNumber("1234567890123456");
        createdAccountDTO.setBalance(BigDecimal.ZERO);
        createdAccountDTO.setCurrency(CurrencyType.USD);

        // Define mock behaviors
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(user);
        when(accountService.createAccount(eq(user), eq(CurrencyType.USD))).thenReturn(account);
        when(userMapper.toAccountDTO(account)).thenReturn(createdAccountDTO);

        // Execute the controller method
        ResponseEntity<ApiResponse> response = accountController.createAccount(accountDTO, userDetails);

        // Assert the response
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Account created successfully!", response.getBody().getMessage());
        assertEquals(createdAccountDTO, response.getBody().getData());

        // Verify interactions
        verify(userDetails).getUsername();
        verify(userService).findByUsername("testUser");
        verify(accountService).createAccount(user, CurrencyType.USD);
        verify(userMapper).toAccountDTO(account);
    }

    @Test
    public void testGetUserAccounts() {
        // Prepare test data
        Set<AccountDTO> accountDTOs = new HashSet<>();
        AccountDTO accountDTO1 = new AccountDTO();
        accountDTO1.setCardNumber("1234567890123456");
        accountDTO1.setBalance(BigDecimal.ZERO);
        accountDTO1.setCurrency(CurrencyType.USD);

        AccountDTO accountDTO2 = new AccountDTO();
        accountDTO2.setCardNumber("6543210987654321");
        accountDTO2.setBalance(new BigDecimal("1000.00"));
        accountDTO2.setCurrency(CurrencyType.EUR);

        accountDTOs.add(accountDTO1);
        accountDTOs.add(accountDTO2);

        // Define mock behaviors
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(user);
        when(accountService.getUserAccounts(user)).thenReturn(accountDTOs);

        // Execute the controller method
        ResponseEntity<ApiResponse> response = accountController.getUserAccounts(userDetails);

        // Assert the response
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Retrieved accounts successfully", response.getBody().getMessage());
        assertEquals(accountDTOs, response.getBody().getData());

        // Verify interactions
        verify(userDetails).getUsername();
        verify(userService).findByUsername("testUser");
        verify(accountService).getUserAccounts(user);
    }

    @Test
    public void testCreateAccount_AlreadyExists() {
        // Prepare test data
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setCurrency(CurrencyType.USD);

        // Define mock behaviors
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(user);
        when(accountService.createAccount(eq(user), eq(CurrencyType.USD)))
                .thenThrow(new com.mthree.mbank.exception.account.AccountAlreadyExistsException("Account with currency USD already exists."));

        // Execute and assert exception
        com.mthree.mbank.exception.account.AccountAlreadyExistsException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        com.mthree.mbank.exception.account.AccountAlreadyExistsException.class,
                        () -> accountController.createAccount(accountDTO, userDetails)
                );

        assertEquals("Account with currency USD already exists.", exception.getMessage());

        // Verify interactions
        verify(userDetails).getUsername();
        verify(userService).findByUsername("testUser");
        verify(accountService).createAccount(user, CurrencyType.USD);
        verify(userMapper, never()).toAccountDTO(any());
    }
}