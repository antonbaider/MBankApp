package com.mthree.mbank.exception.account;

public class AccountBalanceNotZeroException extends AccountException {
    public AccountBalanceNotZeroException(String message) {
        super(message);
    }
}
