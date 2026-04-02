package dev.harakki.shiftlab.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * DTO for {@link dev.harakki.shiftlab.domain.Seller}
 */
public record SellerUpdateDto(
        @NotBlank(message = "Name cannot be empty")
        String name,
        String contactInfo
) implements Serializable {
}
