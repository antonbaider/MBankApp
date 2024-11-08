package com.mthree.mbank.exception.user;

public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}