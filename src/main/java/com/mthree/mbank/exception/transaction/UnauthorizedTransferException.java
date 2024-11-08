package com.mthree.mbank.exception.transaction;

public class UnauthorizedTransferException extends TransactionException {
    public UnauthorizedTransferException(String message) {
        super(message);
    }
}
