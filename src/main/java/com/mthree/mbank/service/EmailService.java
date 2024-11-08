package com.mthree.mbank.service;

import com.mthree.mbank.entity.Transaction;

/**
 * EmailService interface defines the operations related to sending emails
 * within the banking application. It provides methods for sending notifications
 * and transactional emails to users.
 */
public interface EmailService {

    /**
     * Sends a simple transaction notification email to the specified recipient.
     * This method accepts a message content which will be included in the email body.
     *
     * @param messageContent the content of the email message to be sent
     */
    void sendTransactionNotification(String messageContent);

    /**
     * Sends a detailed transaction email to the user, including transaction details
     * and an attached receipt in PDF format.
     *
     * @param transaction the Transaction object containing details about the transaction
     */
    void sendTransactionEmail(Transaction transaction);
}