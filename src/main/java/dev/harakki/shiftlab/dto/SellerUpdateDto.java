package dev.harakki.shiftlab.dto;

import java.io.Serializable;

/**
 * DTO for {@link dev.harakki.shiftlab.domain.Seller}
 */
public record SellerUpdateDto(
        String name,
        String contactInfo
) implements Serializable {
}
