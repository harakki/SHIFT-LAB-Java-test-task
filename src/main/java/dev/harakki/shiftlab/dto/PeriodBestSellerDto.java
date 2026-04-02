package dev.harakki.shiftlab.dto;

import java.io.Serializable;

public record PeriodBestSellerDto(
        PeriodType period,
        SellerSummaryResponseDto bestSeller
) implements Serializable {

    public enum PeriodType {
        DAY,
        MONTH,
        QUARTER,
        YEAR
    }

}
