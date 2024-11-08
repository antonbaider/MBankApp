package com.mthree.mbank.util;

import com.mthree.mbank.exception.account.UniqueCardNumberGenerationException;
import com.mthree.mbank.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Utility class responsible for generating unique and valid card numbers for user accounts.
 * Uses a cryptographically strong random number generator to ensure the unpredictability
 * and uniqueness of generated card numbers. Additionally, implement the Luhn algorithm
 * to validate the generated card numbers.
 */
@Component
@Slf4j
public class CardNumberGenerator {

    /**
     * SecureRandom instance for generating cryptographically strong random numbers.
     * Using SecureRandom instead of Random for enhanced security, ensuring that the
     * generated card numbers are hard to predict.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * The standard length for a card number.
     * Typically, card numbers are 16 digits long, adhering to common financial standards.
     */
    private final int cardNumberLength;

    /**
     * Prefix for the card number, typically indicating the card type or issuing a bank.
     * Example: "4000" for Visa cards.
     */
    private final String cardPrefix;

    /**
     * Maximum number of attempts to generate a unique card number.
     */
    private final int maxAttempts;

    /**
     * Repository for accessing account data to verify the uniqueness of generated card numbers.
     */
    private final AccountRepository accountRepository;

    /**
     * Constructor for CardNumberGenerator.
     * Uses constructor-based dependency injection to inject AccountRepository and configuration properties.
     *
     * @param accountRepository Repository interface for Account entity operations.
     * @param cardNumberLength  The desired length of the card number (default is 16).
     * @param cardPrefix        The prefix to be added to each card number (default is "4000").
     * @param maxAttempts       The maximum number of attempts to generate a unique card number (default is 5).
     */
    public CardNumberGenerator(
            AccountRepository accountRepository,
            @Value("${card.number.length:16}") int cardNumberLength,
            @Value("${card.number.prefix:4000}") String cardPrefix,
            @Value("${card.number.maxAttempts:5}") int maxAttempts
    ) {
        this.accountRepository = accountRepository;
        this.cardNumberLength = cardNumberLength;
        this.cardPrefix = cardPrefix;
        this.maxAttempts = maxAttempts;
    }

    /**
     * Generates a random card number consisting of the specified length.
     * Each digit, except the last one (check digit), is randomly selected between 0 and 9.
     * The last digit is calculated using the Luhn algorithm to ensure validity.
     *
     * @return A String representing the randomly generated valid card number.
     */
    private String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();

        // Append the prefix to the card number
        cardNumber.append(cardPrefix);

        // Calculate the number of random digits needed (excluding the prefix and check digit)
        int randomDigitsLength = cardNumberLength - cardPrefix.length() - 1;

        // Generate the random digits
        for (int i = 0; i < randomDigitsLength; i++) {
            int digit = RANDOM.nextInt(10); // Generates a digit between 0 and 9
            cardNumber.append(digit);
        }

        // Calculate the Luhn check digit and append it
        int checkDigit = calculateLuhnCheckDigit(cardNumber.toString());
        cardNumber.append(checkDigit);

        return cardNumber.toString();
    }

    /**
     * Attempts to generate a unique and valid card number by ensuring that the generated number
     * does not already exist in the system. It tries up to a maximum number of attempts
     * to find a unique number before throwing an exception.
     *
     * @return A String representing the uniquely generated valid card number.
     * @throws UniqueCardNumberGenerationException if a unique card number cannot be generated within the maximum attempts.
     */
    public String generateUniqueCardNumber() {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            String cardNumber = generateCardNumber();

            // Check if the generated card number already exists
            if (!accountRepository.existsByCardNumber(cardNumber)) {
                log.info("Unique card number generated on attempt {}: {}", attempt, "**** **** **** " + cardNumber.substring(cardNumber.length() - 4));
                return cardNumber;
            } else {
                log.warn("Card number collision detected on attempt {}: {}", attempt, cardNumber);
            }
        }

        // If a unique card number is not found within the maximum attempts, throw an exception
        String errorMessage = String.format("Unable to generate a unique card number after %d attempts", maxAttempts);
        log.error(errorMessage);
        throw new UniqueCardNumberGenerationException(errorMessage);
    }

    /**
     * Calculates the Luhn check digit for a given card number.
     * The Luhn algorithm is used to validate the card number's integrity.
     *
     * @param number The card number without the check digit.
     * @return The calculated check digit.
     */
    private int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = true;

        // Iterate over the number digits in reverse order
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(String.valueOf(number.charAt(i)));

            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }

            sum += n;
            alternate = !alternate;
        }

        // Calculate the check digit that makes the total sum a multiple of 10
        int mod = sum % 10;
        return (mod == 0) ? 0 : 10 - mod;
    }

    /**
     * Validates a given card number using the Luhn algorithm.
     * This method can be used to verify the integrity of card numbers during transactions or account verifications.
     *
     * @param cardNumber The card number to validate.
     * @return True if the card number is valid, false otherwise.
     */
    public boolean validateCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != cardNumberLength) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;

        // Iterate over the card number digits in reverse order
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            char c = cardNumber.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }

            int n = Character.getNumericValue(c);

            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }

            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }
}