package dev.harakki.shiftlab.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link dev.harakki.shiftlab.domain.Seller}
 */
public record SellerResponseDto(
        Long id,
        String name,
        String contactInfo,
        LocalDateTime registrationDate
) implements Serializable {
}
