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

/**
 * Аналитические данные CRM
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Получить самого продуктивного продавца
     *
     * @return Список с самыми продуктивными продавцами за последний день, месяц, квартал и год
     * ({@link PeriodBestSellerDto})
     */
    @GetMapping("/sellers/most-productive")
    public List<PeriodBestSellerDto> getMostProductiveSeller() {
        return analyticsService.getMostProductiveSeller();
    }

    /**
     * Получить продавцов с суммой транзакций меньше указанной за определенный период
     *
     * @param sum       Максимальная пороговая сумма
     * @param startDate Начало периода для фильтрации транзакций
     * @param endDate   Конец периода для фильтрации транзакций
     * @param pageable  Настройки пагинации и сортировки
     * @return Страница с краткой информацией по продавцам ({@link SellerSummaryResponseDto})
     */
    @GetMapping("/sellers/sum-lower-than")
    public Page<SellerSummaryResponseDto> getSellersWithSumLowerThan(
            @RequestParam BigDecimal sum,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable
    ) {
        return analyticsService.getSellersWithSumLowerThanInPeriod(sum, startDate, endDate, pageable);
    }

    /**
     * Получить самое продуктивное время продавца
     *
     * @param sellerId Идентификатор продавца
     * @return {@link BestSellingPeriodsResponseDto} - информация о наилучших периодах продаж для определенного продавца
     * за день, неделю и месяц
     */
    @GetMapping("/sellers/{sellerId}/most-productive-time")
    public BestSellingPeriodsResponseDto getMostProductiveTimeForSeller(@PathVariable Long sellerId) {
        return analyticsService.getMostProductiveTimeForSeller(sellerId);
    }

}
