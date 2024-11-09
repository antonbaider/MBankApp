package com.mthree.mbank.exception.transaction;

public class InvalidCardNumberException extends TransactionException {
    public InvalidCardNumberException(String message) {
        super(message);
    }
}
