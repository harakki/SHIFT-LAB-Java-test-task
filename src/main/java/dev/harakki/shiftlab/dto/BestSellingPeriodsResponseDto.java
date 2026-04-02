package dev.harakki.shiftlab.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record BestSellingPeriodsResponseDto(
        PeriodData bestDay,
        PeriodData bestWeek,
        PeriodData bestMonth
) implements Serializable {

    public record PeriodData(
            LocalDateTime start,
            LocalDateTime end,
            Long transactionCount
    ) implements Serializable {
    }

}
