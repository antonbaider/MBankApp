package com.mthree.mbank.listener;

import com.mthree.mbank.entity.Account;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.PrePersist;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Entity listener for the Account entity.
 * Responsible for setting audit fields such as creationDate and expirationDate.
 */
@Component
@Slf4j
public class AccountEntityListener {

    /**
     * Method invoked before the Account entity is persisted.
     * Sets the creationDate and calculates the expirationDate.
     *
     * @param account The Account entity being persisted.
     */
    @PrePersist
    public void prePersist(Account account) {
        LocalDate creationDate = LocalDate.now();
        account.setCreationDate(creationDate);
        account.setExpirationDate(creationDate.plusYears(5)); // Account validity period

        log.info("Setting creationDate: {} and expirationDate: {} for Account ID: {}",
                creationDate, account.getExpirationDate(), account.getId());
    }
}