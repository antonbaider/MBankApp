package com.mthree.mbank.exception.transaction;

public class InsufficientFundsException extends TransactionException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
