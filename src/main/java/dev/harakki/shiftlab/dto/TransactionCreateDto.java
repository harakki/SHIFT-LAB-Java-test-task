package dev.harakki.shiftlab.dto;

import dev.harakki.shiftlab.domain.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link dev.harakki.shiftlab.domain.Transaction}
 */
public record TransactionCreateDto(
        @NotNull(message = "Transaction cannot be without seller ID")
        Long sellerId,
        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be greater than 0")
        BigDecimal amount,
        @NotNull(message = "Payment type is required")
        PaymentType paymentType
) implements Serializable {
}
