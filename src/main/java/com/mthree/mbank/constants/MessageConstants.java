package com.mthree.mbank.constants;

/**
 * Centralized class for all string messages and error codes used in the application.
 * Organized into nested static classes for better categorization.
 */
public final class MessageConstants {

    private MessageConstants() {
    }

    /**
     * Contains all exception-related messages.
     */
    public static final class Exceptions {
        // **UserService Specific Exception Messages**
        public static final String USER_ALREADY_EXISTS = "Username already exists.";
        public static final String USER_EMAIL_EXISTS = "Email already exists.";
        public static final String USER_SSN_EXISTS = "SSN already exists.";
        public static final String USER_PHONE_EXISTS = "Phone number already exists.";
        public static final String USER_NOT_FOUND = "User not found.";
        public static final String FAMILY_MEMBER_NOT_FOUND = "Family member not found.";
        public static final String USER_PROVIDED_DETAILS_EXIST = "A user with the provided details already exists.";
        public static final String USER_PASSWORD_RESTRICTION = "Password must be at least 8 characters long and include letters, numbers, and special characters.";

        // **AccountService Specific Exception Messages**
        public static final String ACCOUNT_BALANCE_NON_ZERO = "Account balance must be zero before closing.";
        public static final String ACCOUNT_NOT_FOUND = "Account not found.";
        public static final String ACCOUNT_ALREADY_EXISTS = "Account with currency %s already exists.";
        public static final String ACCOUNT_EXISTS = "Account with provided currency already exists.";
        // **TransferService Specific Exception Messages**
        public static final String UNAUTHORIZED_TRANSFER = "You do not own the sender account.";
        public static final String RECEIVER_ACCOUNT_NOT_FOUND = "Receiver account not found.";
        public static final String SENDER_ACCOUNT_NOT_FOUND = "Sender account not found.";
        public static final String SAME_ACCOUNT_TRANSFER = "Cannot transfer money to the same account.";
        public static final String CURRENCY_MISMATCH = "Currency mismatch between accounts";
        public static final String INVALID_TRANSFER_AMOUNT = "Transfer amount must be positive";
        public static final String INSUFFICIENT_BALANCE = "Insufficient balance in sender's account";
        public static final String INVALID_CARD_NUMBER_FORMAT = "Invalid card number format";

        // **Validation Exception Messages**
        public static final String VALIDATION_FAILED = "Validation failed for one or more fields.";
        public static final String INVALID_ENUM_VALUE = "Invalid enum value provided in the request body.";

        // **General Exception Messages**
        public static final String GENERAL_ERROR = "An unexpected error occurred. Please try again later.";
    }

    /**
     * Contains all log-related messages.
     */
    public static final class Logs {
        // **UserService Specific Log Messages**
        public static final String FETCHING_USER_DTO = "Fetching UserDTO for user: {}";
        public static final String CREATING_USER = "Creating user with username: {}";
        public static final String USER_CREATED_SUCCESSFULLY = "User {} created successfully.";
        public static final String FAILED_TO_SAVE_USER = "Failed to save user: {}";
        public static final String USER_UPDATED_SUCCESSFULLY = "User {} updated successfully.";
        public static final String ADDING_FAMILY_MEMBER = "Adding family member with ID {} to user with ID {}";
        public static final String FETCHING_FAMILY_MEMBERS = "Fetching family members for user ID {}";
        public static final String FINDING_USER_BY_USERNAME = "Finding user by username: {}";
        public static final String CONVERTING_USERS_TO_DTO = "Converting users to DTOs.";

        // **AccountService Log Messages**
        public static final String CREATING_NEW_ACCOUNT = "Creating a new account for user {} with currency {}";
        public static final String UNABLE_TO_GENERATE_CARD_NUMBER = "Unable to generate a unique card number after {} attempts";
        public static final String ACCOUNT_CREATED_SUCCESSFULLY = "Account created successfully for user {}";
        public static final String CLOSING_ACCOUNT = "Closing account {} for user {}";
        public static final String ATTEMPTED_TO_CLOSE_NON_ZERO_BALANCE = "Attempted to close account {} with non-zero balance";
        public static final String ACCOUNT_CLOSED_SUCCESSFULLY = "Account {} closed successfully";
        public static final String CLEAR_ALL_CACHES = "All caches for accounts have been cleared.";
        public static final String ACCOUNT_ALREADY_EXISTS = "Account with currency {} already exists.";
        // **TransactionService Log Messages**
        public static final String TRANSFER_STARTED = "Starting transfer from {} to {}";
        public static final String TRANSFER_COMPLETED = "Transfer from {} to {} completed successfully";
        public static final String TRANSFER_BETWEEN_USERS_COMPLETED = "Transfer of {} from user {} to user {} completed successfully";
        public static final String VALIDATING_TRANSFER = "Validating transfer for user {}";
        public static final String UNAUTHORIZED_TRANSFER_LOG = "Unauthorized transfer attempt by user {}";
        public static final String CURRENCY_MISMATCH_LOG = "Currency mismatch between sender and receiver accounts";
        public static final String INVALID_TRANSFER_AMOUNT = "Invalid transfer amount: {}";
        public static final String INSUFFICIENT_FUNDS = "Insufficient funds for user {} on account {}";
        public static final String INVALID_CARD_NUMBER_FORMAT_LOG = "Invalid card number format: {}";
        public static final String INVALID_CARD_NUMBER_LUHN_LOG = "Invalid card number LUHN format: {}";

        public static final String CLEARING_CACHE_FOR_USER = "Clearing cache for user ID {}";
        public static final String SAME_ACCOUNT_TRANSFER_LOG = "Cannot transfer money to the same account.";

        // **General Log Messages**
        public static final String GENERAL_OPERATION_SUCCESS = "Operation {} completed successfully.";
        public static final String GENERAL_OPERATION_FAILED = "Operation {} failed: {}.";
    }

    public static final class Email {
        public static final String TRANSFER_SUCCESS = "Transfer of %s %s from %s to %s successfully completed";
    }

    /**
     * Contains all error codes used in the application.
     */
    public static final class ErrorCodes {
        public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
        public static final String USER_EXISTS = "USER_EXISTS";
        public static final String ACCOUNT_BALANCE_NON_ZERO = "ACCOUNT_BALANCE_NON_ZERO";
        public static final String INVALID_ARGUMENT = "INVALID_ARGUMENT";
        public static final String PHONE_EXISTS = "PHONE_EXISTS";
        public static final String SSN_EXISTS = "SSN_EXISTS";
        public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
        public static final String ACCOUNT_NOT_FOUND = "ACCOUNT_NOT_FOUND";
        public static final String UNAUTHORIZED_TRANSFER = "UNAUTHORIZED_TRANSFER";
        public static final String RECEIVER_ACCOUNT_NOT_FOUND = "RECEIVER_ACCOUNT_NOT_FOUND";
        public static final String INVALID_PASSWORD = "INVALID_PASSWORD";
        public static final String INVALID_ENUM_VALUE = "INVALID_ENUM_VALUE";
        public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
        public static final String ACCOUNT_EXISTS = "ACCOUNT_EXISTS";
    }
}