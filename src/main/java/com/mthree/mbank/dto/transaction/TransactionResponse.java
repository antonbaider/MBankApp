package com.mthree.mbank.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Long transactionId;
    private Long senderAccountId;
    private String senderCardNumber;
    private Long receiverAccountId;
    private String receiverCardNumber;
    private BigDecimal amount;
    private String currency;
    private BigDecimal balanceAfter;
    private LocalDateTime timestamp;
}