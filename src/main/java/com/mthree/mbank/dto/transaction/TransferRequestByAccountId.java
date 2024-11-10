package com.mthree.mbank.dto.transaction;

import com.mthree.mbank.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Setter
@Getter
public class TransferRequestByAccountId {
    @NotNull(message = "Sender account ID is required")
    @Schema(description = "ID of the sender's account", example = "1")
    private Long senderAccountId;

    @NotNull(message = "Receiver account ID is required")
    @Schema(description = "ID of the receiver's account", example = "2")
    private Long receiverAccountId;

    @NotNull(message = "Amount is required")
    @Schema(description = "Amount to be transferred", example = "100.00")
    private BigDecimal amount;

    @Schema(description = "Sender user details", hidden = true)
    private User sender;

    @Schema(description = "Receiver user details", hidden = true)
    private User receiver;
}