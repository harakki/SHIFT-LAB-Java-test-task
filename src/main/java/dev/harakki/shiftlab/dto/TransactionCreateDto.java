package dev.harakki.shiftlab.dto;

import dev.harakki.shiftlab.domain.PaymentType;
import dev.harakki.shiftlab.domain.Seller;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link dev.harakki.shiftlab.domain.Transaction}
 */
public record TransactionCreateDto(
        Seller seller,
        BigDecimal amount,
        PaymentType paymentType
) implements Serializable {
}
