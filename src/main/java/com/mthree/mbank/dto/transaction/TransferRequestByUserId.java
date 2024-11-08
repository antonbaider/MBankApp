package com.mthree.mbank.dto.transaction;

import com.mthree.mbank.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class TransferRequestByUserId {
    @NotNull(message = "Sender account ID is required")
    private Long senderAccountId;

    @NotNull(message = "Receiver account ID is required")
    private Long receiverAccountId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private User sender;

    private User receiver;
}