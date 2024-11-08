package com.mthree.mbank.util;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.mthree.mbank.entity.Transaction;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGenerator {

    @Value("${pdf.logo.path}")
    private String logoPath;

    @Value("${pdf.footerImage.path}")
    private String footerImagePath;

    /**
     * Generates a PDF receipt for a transaction.
     *
     * @param transaction The transaction details.
     * @return The PDF receipt as a DataSource.
     * @throws IOException if an error occurs during PDF generation.
     */
    public DataSource generatePdfReceipt(Transaction transaction) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Create PDF writer and document
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Add logo image if available
        addLogo(document);

        // Add transaction details to PDF
        addTransactionDetails(document, transaction);

        // Add footer image at the bottom of the page
        addFooterImage(document);

        // Close the document
        document.close();

        // Return PDF as ByteArrayDataSource for email attachment
        return new ByteArrayDataSource(outputStream.toByteArray(), "application/pdf");
    }

    private void addLogo(Document document) throws IOException {
        if (logoPath != null && !logoPath.isEmpty()) {
            File logoFile = new File(logoPath);
            if (logoFile.exists()) {
                ImageData logoData = ImageDataFactory.create(logoPath);
                Image logo = new Image(logoData);
                logo.setWidth(100);
                logo.setTextAlignment(TextAlignment.CENTER);
                document.add(logo);
            }
        }
    }

    private void addTransactionDetails(Document document, Transaction transaction) {
        document.add(new Paragraph("Transaction Receipt")
                .setBold()
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10f));

        // Transaction Details
        document.add(new Paragraph("Transaction Details:").setBold().setMarginTop(20f));
        Table transactionTable = new Table(UnitValue.createPercentArray(new float[]{2, 3})).useAllAvailableWidth();
        transactionTable.addCell("Amount:");
        transactionTable.addCell(String.format("%.2f %s", transaction.getAmount(), transaction.getSenderAccount().getCurrency()));
        transactionTable.addCell("Date:");
        transactionTable.addCell(transaction.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        transactionTable.addCell("Transaction ID:");
        transactionTable.addCell(String.valueOf(transaction.getId()));
        document.add(transactionTable);

        // Sender Information
        document.add(new Paragraph("Sender Information:").setBold().setMarginTop(20f));
        Table senderTable = new Table(UnitValue.createPercentArray(new float[]{2, 3})).useAllAvailableWidth();
        senderTable.addCell("Name:");
        senderTable.addCell(transaction.getSender().getProfile().getFirstName() + " " + transaction.getSender().getProfile().getLastName());
        senderTable.addCell("Card Number:");
        senderTable.addCell(transaction.getSenderAccount().getCardNumber());
        document.add(senderTable);

        // Receiver Information
        document.add(new Paragraph("Receiver Information:").setBold().setMarginTop(20f));
        Table receiverTable = new Table(UnitValue.createPercentArray(new float[]{2, 3})).useAllAvailableWidth();
        receiverTable.addCell("Name:");
        receiverTable.addCell(transaction.getReceiver().getProfile().getFirstName() + " " + transaction.getReceiver().getProfile().getLastName());
        receiverTable.addCell("Card Number:");
        receiverTable.addCell(transaction.getReceiverAccount().getCardNumber());
        document.add(receiverTable);

        // Footer Text
        document.add(new Paragraph("Thank you for using our service!")
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20f));
        document.add(new Paragraph("Best Regards,\nBankMthree Team")
                .setTextAlignment(TextAlignment.CENTER));
    }

    private void addFooterImage(Document document) throws IOException {
        if (footerImagePath != null && !footerImagePath.isEmpty()) {
            File footerImageFile = new File(footerImagePath);
            if (footerImageFile.exists()) {
                ImageData footerImageData = ImageDataFactory.create(footerImagePath);
                Image footerImage = new Image(footerImageData);
                footerImage.setWidth(UnitValue.createPercentValue(115));
//                footerImage.setHeight(190);  // Set height as requested
                footerImage.setTextAlignment(TextAlignment.CENTER);
                footerImage.setFixedPosition(0, 0);  // Place the footer at the bottom
                document.add(footerImage);
            }
        }
    }
}