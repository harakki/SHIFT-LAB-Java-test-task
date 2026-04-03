package dev.harakki.shiftlab.service;

import dev.harakki.shiftlab.domain.Seller;
import dev.harakki.shiftlab.dto.PeriodBestSellerDto;
import dev.harakki.shiftlab.dto.SellerSummaryResponseDto;
import dev.harakki.shiftlab.exception.EntityNotFoundException;
import dev.harakki.shiftlab.mapper.SellerMapper;
import dev.harakki.shiftlab.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private SellerService sellerService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SellerMapper sellerMapper;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void getMostProductiveSeller_shouldReturnFourPeriods_andHandleMissingPeriods() {
        Seller daySeller = mock(Seller.class);
        Seller quarterSeller = mock(Seller.class);
        Seller yearSeller = mock(Seller.class);

        when(transactionRepository.findFirstSellerInPeriodOrderByAmountDescSellerIdAsc(any(), any()))
                .thenReturn(
                        Optional.of(daySeller),
                        Optional.empty(),
                        Optional.of(quarterSeller),
                        Optional.of(yearSeller)
                );

        when(sellerMapper.toSellerSummaryResponseDto(daySeller))
                .thenReturn(new SellerSummaryResponseDto(1L, "Day seller"));
        when(sellerMapper.toSellerSummaryResponseDto(quarterSeller))
                .thenReturn(new SellerSummaryResponseDto(3L, "Quarter seller"));
        when(sellerMapper.toSellerSummaryResponseDto(yearSeller))
                .thenReturn(new SellerSummaryResponseDto(4L, "Year seller"));

        List<PeriodBestSellerDto> result = analyticsService.getMostProductiveSeller();

        assertEquals(4, result.size());
        assertEquals(PeriodBestSellerDto.PeriodType.DAY, result.get(0).period());
        assertEquals("Day seller", result.get(0).bestSeller().name());

        assertEquals(PeriodBestSellerDto.PeriodType.MONTH, result.get(1).period());
        assertNull(result.get(1).bestSeller());

        assertEquals(PeriodBestSellerDto.PeriodType.QUARTER, result.get(2).period());
        assertEquals("Quarter seller", result.get(2).bestSeller().name());

        assertEquals(PeriodBestSellerDto.PeriodType.YEAR, result.get(3).period());
        assertEquals("Year seller", result.get(3).bestSeller().name());

        verify(transactionRepository, times(4))
                .findFirstSellerInPeriodOrderByAmountDescSellerIdAsc(any(), any());
    }

    @Test
    void getMostProductiveSeller_shouldReturnNulls_whenNoTransactionsInAnyPeriod() {
        when(transactionRepository.findFirstSellerInPeriodOrderByAmountDescSellerIdAsc(any(), any()))
                .thenReturn(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

        List<PeriodBestSellerDto> result = analyticsService.getMostProductiveSeller();

        assertEquals(4, result.size());
        assertTrue(result.stream().allMatch(dto -> dto.bestSeller() == null));
        verify(transactionRepository, times(4))
                .findFirstSellerInPeriodOrderByAmountDescSellerIdAsc(any(), any());
        verifyNoInteractions(sellerMapper);
    }



    @Test
    void getBestSellingPeriodsForSeller_shouldCalculateWindowsCorrectly() {
        Long sellerId = 1L;
        var mockSeller = new Seller();
        mockSeller.setId(sellerId);

        var startTime = LocalDateTime.of(2026, 1, 1, 12, 0);
        List<LocalDateTime> transactionDates = List.of(startTime.minusDays(100), // День -99

                startTime.minusDays(1), // День 0

                startTime, // День 1
                startTime.plusHours(2), // День 1

                startTime.plusDays(3), // День 4

                startTime.plusDays(4), // День 5

                startTime.plusDays(5), // День 6

                startTime.plusDays(6), // День 7
                startTime.plusHours(1), // День 7
                startTime.plusHours(2), // День 7

                startTime.plusDays(20), // День 21

                startTime.plusDays(100) // день 101
        );

        when(sellerService.findSellerById(sellerId)).thenReturn(Optional.of(mockSeller));
        when(transactionRepository.findAllTransactionDatesBySellerIdOrderByTransactionDateAsc(sellerId)).thenReturn(transactionDates);

        var result = analyticsService.getBestSellingPeriodsForSeller(sellerId);

        assertNotNull(result);

        assertEquals(4L, result.bestDay().transactionCount());
        assertEquals(9L, result.bestWeek().transactionCount());
        assertEquals(10L, result.bestMonth().transactionCount());
    }

    @Test
    void getBestSellingPeriodsForSeller_shouldThrow_whenSellerNotFound() {
        Mockito.when(sellerService.findSellerById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> analyticsService.getBestSellingPeriodsForSeller(1L));
    }

    @Test
    void getBestSellingPeriodsForSeller_shouldReturnEmpty_whenNoTransactions() {
        Mockito.when(sellerService.findSellerById(1L))
                .thenReturn(Optional.of(mock(Seller.class)));
        Mockito.when(transactionRepository.findAllTransactionDatesBySellerIdOrderByTransactionDateAsc(1L))
                .thenReturn(List.of());

        var result = analyticsService.getBestSellingPeriodsForSeller(1L);

        assertNull(result.bestDay());
        assertNull(result.bestWeek());
        assertNull(result.bestMonth());
    }

}
