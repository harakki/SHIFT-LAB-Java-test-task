package dev.harakki.shiftlab.controller;

import dev.harakki.shiftlab.dto.BestSellingPeriodsResponseDto;
import dev.harakki.shiftlab.dto.PeriodBestSellerDto;
import dev.harakki.shiftlab.dto.SellerSummaryResponseDto;
import dev.harakki.shiftlab.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // Получить самого продуктивного продавца
    @GetMapping("/sellers/most-productive")
    public List<PeriodBestSellerDto> getMostProductiveSeller() {
        return analyticsService.getMostProductiveSeller();
    }

    // Получить список продавцов с суммой меньше указанной
    @GetMapping("/sellers/sum-lower-than")
    public Page<SellerSummaryResponseDto> getSellersWithSumLowerThan(
            @RequestParam BigDecimal sum,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable
    ) {
        return analyticsService.getSellersWithSumLowerThanInPeriod(sum, startDate, endDate, pageable);
    }

    // * - Получить самое продуктивное время продавца
    @GetMapping("/sellers/{sellerId}/most-productive-time")
    public BestSellingPeriodsResponseDto getMostProductiveTimeForSeller(@PathVariable Long sellerId) {
        return analyticsService.getMostProductiveTimeForSeller(sellerId);
    }

}
