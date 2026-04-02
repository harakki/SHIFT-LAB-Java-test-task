package dev.harakki.shiftlab.service;

import dev.harakki.shiftlab.dto.BestSellingPeriodsResponseDto;
import dev.harakki.shiftlab.dto.PeriodBestSellerDto;
import dev.harakki.shiftlab.dto.SellerSummaryResponseDto;
import dev.harakki.shiftlab.exception.EntityNotFoundException;
import dev.harakki.shiftlab.mapper.SellerMapper;
import dev.harakki.shiftlab.repository.SellerRepository;
import dev.harakki.shiftlab.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AnalyticsService {

    private final SellerService sellerService;

    private final SellerRepository sellerRepository;
    private final TransactionRepository transactionRepository;

    private final SellerMapper sellerMapper;

    public List<PeriodBestSellerDto> getMostProductiveSeller() {
        LocalDateTime now = LocalDateTime.now();

        return List.of(
                new PeriodBestSellerDto(PeriodBestSellerDto.PeriodType.DAY, getBestSellerForPeriod(now.minusDays(1), now)),
                new PeriodBestSellerDto(PeriodBestSellerDto.PeriodType.MONTH, getBestSellerForPeriod(now.minusMonths(1), now)),
                new PeriodBestSellerDto(PeriodBestSellerDto.PeriodType.QUARTER, getBestSellerForPeriod(now.minusMonths(3), now)),
                new PeriodBestSellerDto(PeriodBestSellerDto.PeriodType.YEAR, getBestSellerForPeriod(now.minusYears(1), now))
        );
    }

    private SellerSummaryResponseDto getBestSellerForPeriod(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findFirstSellerInPeriodOrderByAmountDescSellerIdAsc(start, end)
                .map(sellerMapper::toSellerSummaryResponseDto)
                .orElse(null); // Если транзакций не было, значит возвращаем null
    }

    public Page<SellerSummaryResponseDto> getSellersWithSumLowerThanInPeriod(BigDecimal sum,
                                                                             LocalDateTime startDate,
                                                                             LocalDateTime endDate,
                                                                             Pageable pageable) {
        return sellerRepository.findSellersWithTotalAmountLessThanInPeriod(sum, startDate, endDate, pageable)
                .map(sellerMapper::toSellerSummaryResponseDto);
    }

    public BestSellingPeriodsResponseDto getMostProductiveTimeForSeller(Long sellerId) {
        return getBestSellingPeriodsForSeller(sellerId);
    }

    public BestSellingPeriodsResponseDto getBestSellingPeriodsForSeller(Long sellerId) {
        if (sellerService.findSellerById(sellerId).isEmpty()) {
            throw new EntityNotFoundException("Seller with id " + sellerId + " not found");
        }

        // Даты транзакций, отсортированные по ВРЕМЕНИ, чтобы захватить окно ВРЕМЕНИ правильно
        List<LocalDateTime> times = transactionRepository.findAllTransactionDatesBySellerIdOrderByTransactionDateAsc(sellerId);

        if (times.isEmpty()) {
            return new BestSellingPeriodsResponseDto(null, null, null);
        }

        // Так как наилучший период времени для продавца - это весь период работы с ним, значит просто считаем периоды
        // разных интервалов, хотя проще и правда посчитать количество всех транзакций продавца с первого дня работы с
        // ним до последнего

        var bestDay = calculateBestSellingPeriodWindow(times, 1);
        var bestWeek = calculateBestSellingPeriodWindow(times, 7);
        var bestMonth = calculateBestSellingPeriodWindow(times, 30);

        return new BestSellingPeriodsResponseDto(bestDay, bestWeek, bestMonth);
    }

    private BestSellingPeriodsResponseDto.PeriodData calculateBestSellingPeriodWindow(List<LocalDateTime> times, int daysWindow) {
        long maxCount = 0;

        var bestStart = times.getFirst();
        var bestEnd = times.getFirst();

        int leftWindowBorder = 0;

        // Алгоритм скользящего окна
        for (int rightWindowBorder = 0; rightWindowBorder < times.size(); rightWindowBorder++) {
            var currentRightBorderTime = times.get(rightWindowBorder);

            // Если окно больше установленного количества дней, то сдвигаем левый указатель вперед
            while (currentRightBorderTime.isAfter(times.get(leftWindowBorder).plusDays(daysWindow))) {
                leftWindowBorder++;
            }

            var currentWindowTransactionCount = rightWindowBorder - leftWindowBorder + 1;

            // Если диапазон более горячий по heatmap'у, то записываем как лучший период
            if (currentWindowTransactionCount > maxCount) {
                maxCount = currentWindowTransactionCount;
                bestStart = times.get(leftWindowBorder);
                bestEnd = currentRightBorderTime;
            }
        }

        return new BestSellingPeriodsResponseDto.PeriodData(bestStart, bestEnd, maxCount);
    }

}
