package com.mthree.mbank.dto.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotBlank(message = "Sender account number is required")
    @Schema(description = "Sender's card number", example = "4000462191920440")
    private String senderCardNumber;

    @NotBlank(message = "Receiver account number is required")
    @Schema(description = "Receiver's card number", example = "4000292421000105")
    private String receiverCardNumber;

    @DecimalMin(value = "0.01", message = "Transfer amount must be greater than zero")
    @Schema(description = "Amount to be transferred", example = "100.00")
    private BigDecimal amount;
}