package com.mthree.mbank.exception;

import com.mthree.mbank.constants.MessageConstants;
import com.mthree.mbank.exception.account.*;
import com.mthree.mbank.exception.transaction.InsufficientFundsException;
import com.mthree.mbank.exception.transaction.InvalidCardNumberException;
import com.mthree.mbank.exception.transaction.UnauthorizedTransferException;
import com.mthree.mbank.exception.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler to manage exceptions uniformly across the application.
 * This class intercepts various exceptions thrown by the application and maps them
 * to standardized HTTP responses, ensuring consistent error handling and messaging.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Builds a standardized error response encapsulating details about the exception.
     *
     * @param message   The main error message describing the exception.
     * @param errorCode A specific error code representing the type of error.
     * @param status    The HTTP status to be returned.
     * @param request   The web request during which the exception was thrown.
     * @param details   Additional details providing more context about the error.
     * @return A ResponseEntity containing the CustomErrorResponse object and the corresponding HTTP status.
     */
    private ResponseEntity<CustomErrorResponse> buildErrorResponse(String message, String errorCode, HttpStatus status, WebRequest request, Map<String, String> details) {
        // Log the error details before building the response
        log.error("Error occurred: {} (Code: {}, Status: {}) - Details: {}", message, errorCode, status, details);

        // Create a new CustomErrorResponse object with all necessary fields
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                status.value(), // HTTP status code (e.g., 400)
                status.getReasonPhrase(), // HTTP status reason (e.g., "Bad Request")
                message, // Detailed error message
                errorCode, // Specific error code
                LocalDateTime.now(), // Timestamp of the error
                request.getDescription(false).replace("uri=", "") // URI path of the request
        );
        errorResponse.setDetails(details); // Additional error details
        // Return the response entity with the error response and HTTP status
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles validation exceptions thrown when @Valid fails on request bodies.
     * This method captures all field validation errors and returns a consolidated response.
     *
     * @param ex      The MethodArgumentNotValidException containing validation errors.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse detailing the validation failures.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        // Extract field errors and map them to a field-specific error message
        Map<String, String> details = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField, // Key: Field name
                        FieldError::getDefaultMessage, // Value: Error message
                        (existing, replacement) -> existing // Handle duplicate keys by keeping the first occurrence
                ));

        // Define a generic validation failed message
        String message = MessageConstants.Exceptions.VALIDATION_FAILED;
        // Build and return the error response with HTTP 400 Bad Request
        return buildErrorResponse(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST, request, details);
    }

    /**
     * Handles exceptions when attempting to create a user that already exists.
     * This typically occurs when a user tries to register with a username or email that's already taken.
     *
     * @param ex      The UserAlreadyExistsException containing details about the conflict.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating the user already exists.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<CustomErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, WebRequest request) {
        // Define additional error details, such as the cause of the exception
        Map<String, String> details = Map.of("cause", ex.getMessage());
        // Build and return the error response with HTTP 409 Conflict
        return buildErrorResponse(ex.getMessage(), "USER_EXISTS", HttpStatus.CONFLICT, request, details);
    }

    /**
     * Handles exceptions when an account is not found.
     * This can occur when a user attempts to access or manipulate an account that doesn't exist.
     *
     * @param ex      The AccountNotFoundException indicating the missing account.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating the account was not found.
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleAccountNotFound(AccountNotFoundException ex, WebRequest request) {
        // Define additional error details, such as the resolution or next steps
        Map<String, String> details = Map.of("cause", MessageConstants.Exceptions.ACCOUNT_NOT_FOUND);
        // Build and return the error response with HTTP 404 Not Found
        return buildErrorResponse(ex.getMessage(), "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND, request, details);
    }

    /**
     * Handles exceptions when account balance is not zero during account closure.
     * Users must have a zero balance to close their accounts.
     *
     * @param ex      The AccountBalanceNotZeroException indicating the balance issue.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating the balance must be zero.
     */
    @ExceptionHandler(AccountBalanceNotZeroException.class)
    public ResponseEntity<CustomErrorResponse> handleAccountBalanceNotZero(AccountBalanceNotZeroException ex, WebRequest request) {
        // Provide a resolution or guidance on how to fix the error
        Map<String, String> details = Map.of("resolution", MessageConstants.Exceptions.ACCOUNT_BALANCE_NON_ZERO);
        // Build and return the error response with HTTP 400 Bad Request
        return buildErrorResponse(ex.getMessage(), "ACCOUNT_BALANCE_NON_ZERO", HttpStatus.BAD_REQUEST, request, details);
    }

    /**
     * Handles exceptions when attempting to create an account that already exists.
     * This typically occurs when a user tries to create multiple accounts with the same currency.
     *
     * @param ex      The AccountAlreadyExistsException indicating the account conflict.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating the account already exists.
     */
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<CustomErrorResponse> handleAccountAlreadyExists(AccountAlreadyExistsException ex, WebRequest request) {
        // Define additional error details, such as the cause of the exception
        Map<String, String> details = Map.of("cause", ex.getMessage());
        // Build and return the error response with HTTP 409 Conflict
        return buildErrorResponse(ex.getMessage(), "ACCOUNT_ALREADY_EXISTS", HttpStatus.CONFLICT, request, details);
    }

    /**
     * Handles illegal argument exceptions.
     * This can occur when method arguments passed to a controller are invalid or malformed.
     *
     * @param ex      The IllegalArgumentException indicating the invalid argument.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating the invalid argument.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        // Provide a general resolution message to guide the user
        Map<String, String> details = Map.of("resolution", MessageConstants.Exceptions.GENERAL_ERROR);
        // Build and return the error response with HTTP 400 Bad Request
        return buildErrorResponse(ex.getMessage(), "INVALID_ARGUMENT", HttpStatus.BAD_REQUEST, request, details);
    }

    /**
     * Handles exceptions related to the generation of unique card numbers.
     * This exception is thrown when the application fails to generate a unique card number
     * after the specified number of attempts, which may occur due to database constraints or
     * other issues related to card number uniqueness.
     *
     * @param ex      The UniqueCardNumberGenerationException thrown during the process.
     * @param request The web request during which the exception was thrown, used to provide context.
     * @return A ResponseEntity with a CustomErrorResponse indicating the error related to card number generation.
     */
    @ExceptionHandler(UniqueCardNumberGenerationException.class)
    public ResponseEntity<CustomErrorResponse> handleUniqueCardNumberGenerationException(UniqueCardNumberGenerationException ex, WebRequest request) {
        // Prepare additional error details, such as the cause of the exception
        Map<String, String> details = Map.of("cause", ex.getMessage());

        // Build and return a standardized error response
        return buildErrorResponse(ex.getMessage(), "GENERAL_ERROR", HttpStatus.CONFLICT, request, details);
    }

    /**
     * Handles exceptions when the provided phone number already exists.
     * This typically occurs when a user attempts to register with a phone number that's already in use.
     *
     * @param ex      The UserPhoneAlreadyExistsException indicating the phone number conflict.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating the phone number already exists.
     */
    @ExceptionHandler(UserPhoneAlreadyExistsException.class)
    public ResponseEntity<CustomErrorResponse> handlePhoneAlreadyExists(UserPhoneAlreadyExistsException ex, WebRequest request) {
        // Define additional error details, such as the cause of the exception
        Map<String, String> details = Map.of("cause", ex.getMessage());
        // Build and return the error response with HTTP 409 Conflict
        return buildErrorResponse(ex.getMessage(), "PHONE_EXISTS", HttpStatus.CONFLICT, request, details);
    }

    /**
     * Handles exceptions when the provided SSN already exists.
     * This typically occurs when a user attempts to register with an SSN that's already in use.
     *
     * @param ex      The UserSsnAlreadyExistsException indicating the SSN conflict.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating the SSN already exists.
     */
    @ExceptionHandler(UserSsnAlreadyExistsException.class)
    public ResponseEntity<CustomErrorResponse> handleSsnAlreadyExists(UserSsnAlreadyExistsException ex, WebRequest request) {
        // Define additional error details, such as the cause of the exception
        Map<String, String> details = Map.of("cause", ex.getMessage());
        // Build and return the error response with HTTP 409 Conflict
        return buildErrorResponse(ex.getMessage(), "SSN_EXISTS", HttpStatus.CONFLICT, request, details);
    }

    /**
     * Handles exceptions when a user is not found.
     * This can occur when a user attempts to access or modify a user account that doesn't exist.
     *
     * @param ex      The UserNotFoundException indicating the missing user.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating the user was not found.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        // Provide additional details to guide the user on possible resolutions
        Map<String, String> details = Map.of("resolution", "Verify the user ID or username exists in the system.");
        // Build and return the error response with HTTP 404 Not Found
        return buildErrorResponse(ex.getMessage(), "USER_NOT_FOUND", HttpStatus.NOT_FOUND, request, details);
    }

    /**
     * Handles exceptions when accounts are not found.
     * This can occur when a user attempts to access or manipulate accounts that don't exist.
     *
     * @param ex      The AccountsNotFoundException indicating the missing accounts.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating the accounts were not found.
     */
    @ExceptionHandler(AccountsNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleAccountsNotFoundException(AccountsNotFoundException ex, WebRequest request) {
        // Provide additional details to guide the user on possible resolutions
        Map<String, String> details = Map.of("resolution", MessageConstants.Exceptions.ACCOUNT_NOT_FOUND);
        // Build and return the error response with HTTP 404 Not Found
        return buildErrorResponse(ex.getMessage(), "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND, request, details);
    }

    /**
     * Handles InsufficientFundsException, which is thrown when a user attempts
     * to perform a transaction that exceeds their account balance.
     *
     * @param ex      the InsufficientFundsException thrown
     * @param request the current web request, used to provide additional context in the response
     * @return a ResponseEntity containing the error details and HTTP status
     */
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<CustomErrorResponse> handleInsufficientFundsException(InsufficientFundsException ex, WebRequest request) {
        // Prepare additional error details to be included in the response
        Map<String, String> details = Map.of("cause", ex.getMessage());

        // Build and return a standardized error response using the existing method
        return buildErrorResponse(
                ex.getMessage(),                     // The error message to be displayed
                "INSUFFICIENT_FUNDS",               // A unique error code to identify the error type
                HttpStatus.BAD_REQUEST,              // HTTP status code indicating a client-side error
                request,                             // The current web request for additional context
                details                              // Additional details about the error
        );
    }

    /**
     * Handles exceptions when the provided password is invalid.
     * This can occur during authentication or when setting/updating a password.
     *
     * @param ex      The InvalidPasswordException indicating the password issue.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating the invalid password.
     */
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<CustomErrorResponse> handleInvalidPasswordException(InvalidPasswordException ex, WebRequest request) {
        // Define additional error details, such as the cause of the exception
        Map<String, String> details = Map.of("cause", ex.getMessage());
        // Build and return the error response with HTTP 400 Bad Request
        return buildErrorResponse(ex.getMessage(), "INVALID_PASSWORD", HttpStatus.BAD_REQUEST, request, details);
    }

    /**
     * Handles exceptions when the HTTP message is not readable, such as invalid enum values or malformed JSON.
     *
     * @param ex      The HttpMessageNotReadableException indicating the parsing issue.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating the message was not readable.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        // Default error message for invalid enum values
        String message = MessageConstants.Exceptions.INVALID_ENUM_VALUE;
        Map<String, String> details = new HashMap<>();

        // Check if the exception message contains details about deserialization issues
        if (ex.getLocalizedMessage().contains("Cannot deserialize value of type")) {
            String[] parts = ex.getLocalizedMessage().split(": ");
            if (parts.length > 1) {
                // Extract the problematic value from the exception message
                message = parts[1].split(";")[0];
                // Provide a resolution to guide the user on correct enum values
                details.put("resolution", "Provide a valid value for the enum field, such as EUR, GBP, USD, PLN, INR.");
            }
        }

        // Build and return the error response with HTTP 400 Bad Request
        return buildErrorResponse(message, "INVALID_ENUM_VALUE", HttpStatus.BAD_REQUEST, request, details);
    }

    /**
     * Handles InvalidCardNumberException.
     *
     * @param ex      The InvalidCardNumberException thrown.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating the invalid card number.
     */
    @ExceptionHandler(InvalidCardNumberException.class)
    public ResponseEntity<CustomErrorResponse> handleInvalidCardNumberException(InvalidCardNumberException ex, WebRequest request) {
        // Log a warning instead of an error for client-side issues
        log.warn("Invalid card number format: {}", ex.getMessage());

        // Prepare error details without exposing sensitive information
        Map<String, String> details = Map.of("cause", ex.getMessage());

        // Build and return the error response with HTTP 400 Bad Request
        return buildErrorResponse(ex.getMessage(), "INVALID_CARD_NUMBER", HttpStatus.BAD_REQUEST, request, details);
    }

    /**
     * Handles exceptions when the provided username is not found.
     * This can occur when a user attempts to log in or access a resource with a non-existent username.
     *
     * @param ex      The UsernameNotFoundException indicating the missing username.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating the username was not found.
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        // Prepare additional error details to be included in the response
        Map<String, String> details = Map.of("cause", ex.getMessage());

        // Build and return a standardized error response using the existing method
        return buildErrorResponse(ex.getMessage(), "USERNAME_NOT_FOUND", HttpStatus.NOT_FOUND, request, details);
    }

    /**
     * Handles exceptions when a user is unauthorized to perform a specific action.
     * This can occur when a user attempts to access resources or perform actions
     * that they do not have permission for.
     *
     * @param ex      The UserUnauthorizedException indicating the unauthorized action.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating the user is unauthorized.
     */
    @ExceptionHandler(UserUnauthorizedException.class)
    public ResponseEntity<CustomErrorResponse> handleUserUnauthorizedException(UserUnauthorizedException ex, WebRequest request) {
        // Prepare additional error details, such as the cause of the exception
        Map<String, String> details = Map.of("cause", ex.getMessage());

        // Build and return a standardized error response using the existing method
        return buildErrorResponse(ex.getMessage(), "USER_UNAUTHORIZED", HttpStatus.FORBIDDEN, request, details);
    }

    /**
     * Handles all other exceptions not explicitly handled by other methods.
     * This serves as a catch-all to ensure that unexpected errors are gracefully managed.
     *
     * @param ex      The Exception indicating an unexpected error.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity with a CustomErrorResponse indicating an internal server error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleAllOtherExceptions(Exception ex, WebRequest request) {
        // Log a concise error message without stack traces
        log.error("Unhandled exception occurred: {}", ex.getMessage());

        // Provide a generic cause to avoid leaking sensitive details
        Map<String, String> details = Map.of("cause", "An unexpected error occurred.");

        // Build and return the error response with HTTP 500 Internal Server Error
        return buildErrorResponse(MessageConstants.Exceptions.GENERAL_ERROR, "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, request, details);
    }
}