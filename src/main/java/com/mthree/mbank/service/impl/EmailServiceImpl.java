package com.mthree.mbank.service.impl;

import com.mthree.mbank.entity.Transaction;
import com.mthree.mbank.service.EmailService;
import com.mthree.mbank.util.PdfGenerator;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class responsible for managing email notifications related to transactions.
 * This class handles sending transactional emails, including transaction details and receipts,
 * utilizing Thymeleaf templates and email attachments for PDF documents.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender; // Mail sender for sending emails
    private final PdfGenerator pdfGenerator; // Service for generating PDF receipts
    private final TemplateEngine templateEngine; // Thymeleaf template engine for processing email templates

    @Value("${recipient.email}")
    private String recipientEmail; // Recipient's email address from application properties

    @Value("${email.logo.path}")
    private String logoPath; // Path to the logo image used in the email

    public EmailServiceImpl(JavaMailSender mailSender, PdfGenerator pdfGenerator, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.pdfGenerator = pdfGenerator;
        this.templateEngine = templateEngine;
    }

    /**
     * Notifies the user of a successful transaction via email with a simple text message.
     *
     * @param transaction The transaction details to include in the notification.
     */
    public void notifyTransaction(Transaction transaction) {
        // Format the message content with transaction details
        String formattedMessage = String.format("Dear Customer,\n\nYour transaction was successful!\n\n" +
                        "Transaction Details:\n" +
                        "Amount: %.2f %s\n" +
                        "Date: %s\n\n" +
                        "Sender Information:\n" +
                        "Name: %s %s\n" +
                        "Account Balance: %.2f %s\n\n" +
                        "Receiver Information:\n" +
                        "Name: %s %s\n\n" +
                        "Thank you for using our service!\n\nBest Regards,\nBankMthree Team",
                transaction.getAmount(),
                transaction.getSenderAccount().getCurrency(),
                transaction.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                transaction.getSender().getProfile().getFirstName(),
                transaction.getSender().getProfile().getLastName(),
                transaction.getSenderAccount().getBalance(),
                transaction.getSenderAccount().getCurrency(),
                transaction.getReceiver().getProfile().getFirstName(),
                transaction.getReceiver().getProfile().getLastName());

        // Send the formatted email
        sendTransactionNotification(formattedMessage);
    }

    /**
     * Sends a simple transaction notification email.
     *
     * @param messageContent The content of the email message.
     */
    @Override
    public void sendTransactionNotification(String messageContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail); // Set recipient email address
        message.setSubject("Transaction Notification"); // Set email subject
        message.setText(messageContent); // Set email body content
        mailSender.send(message); // Send the email
    }

    /**
     * Sends an email with a transaction receipt using a Thymeleaf template and attaches a PDF receipt.
     *
     * @param transaction The transaction details to include in the email and PDF.
     */
    @Override
    public void sendTransactionEmail(Transaction transaction) {
        try {
            // Create a new MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail); // Set recipient email address
            helper.setSubject("Transaction Notification"); // Set email subject

            // Load and process the Thymeleaf template with transaction data
            String emailContent = buildEmailContent(transaction);
            helper.setText(emailContent, true); // Set email body content

            // Attach logo for email inline image
            FileSystemResource logo = new FileSystemResource(new File(logoPath));
            helper.addInline("logoImage", logo); // Add logo image to email

            // Attach PDF receipt
            DataSource pdfReceipt = pdfGenerator.generatePdfReceipt(transaction); // Generate PDF
            helper.addAttachment("TransactionReceipt.pdf", pdfReceipt); // Attach PDF receipt to email

            mailSender.send(message); // Send the email

        } catch (MessagingException | IOException e) {
            e.printStackTrace(); // Handle exceptions during email sending
        }
    }

    /**
     * Builds the email content by processing the Thymeleaf template.
     *
     * @param transaction The transaction details to include in the email.
     * @return The processed HTML email content.
     */
    private String buildEmailContent(Transaction transaction) {
        Context context = new Context();
        context.setVariable("transaction", transaction); // Add transaction to the template context
        String formattedTimestamp = transaction.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // Format timestamp
        context.setVariable("formattedTimestamp", formattedTimestamp); // Add formatted timestamp to the template context
        return templateEngine.process("transaction-receipt", context); // Process the template with context variables
    }

    /**
     * Masks the card number for security purposes, displaying only the last four digits.
     *
     * @param cardNumber The card number to be masked.
     * @return The masked card number.
     */
    private String maskCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4); // Masking the card number
    }

    /**
     * Prepares a model for the transaction email by populating the necessary details.
     *
     * @param transaction The transaction details to include in the email model.
     * @return A map containing transaction details and formatted timestamp.
     */
    public Map<String, Object> prepareTransactionModel(Transaction transaction) {
        Map<String, Object> model = new HashMap<>();
        model.put("transaction", transaction); // Add transaction to the model
        model.put("formattedTimestamp", transaction.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))); // Add formatted timestamp to the model
        return model; // Return the populated model
    }
}