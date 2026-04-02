package dev.harakki.shiftlab.dto;

import dev.harakki.shiftlab.domain.PaymentType;
import dev.harakki.shiftlab.domain.Seller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link dev.harakki.shiftlab.domain.Transaction}
 */
public record TransactionSummaryResponseDto(
        Long id,
        Long sellerId,
        BigDecimal amount,
        LocalDateTime transactionDate
) implements Serializable {
}
