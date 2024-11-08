package com.mthree.mbank.dto.account;

import com.mthree.mbank.entity.enums.CurrencyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {
    private String cardNumber;
    @NotNull(message = "Currency type is required")
    @Schema(description = "Currency type for the account", example = "USD")
    private CurrencyType currency;
    private BigDecimal balance;
    private LocalDate expirationDate;
}