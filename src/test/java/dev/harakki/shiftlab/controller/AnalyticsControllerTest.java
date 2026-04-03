package dev.harakki.shiftlab.controller;

import dev.harakki.shiftlab.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import dev.harakki.shiftlab.dto.BestSellingPeriodsResponseDto;
import dev.harakki.shiftlab.dto.PeriodBestSellerDto;
import dev.harakki.shiftlab.dto.SellerSummaryResponseDto;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsService analyticsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getMostProductiveSeller_shouldReturnList() throws Exception {
        var seller = new SellerSummaryResponseDto(1L, "John Doe");
        var response = List.of(new PeriodBestSellerDto(PeriodBestSellerDto.PeriodType.DAY, seller));

        Mockito.when(analyticsService.getMostProductiveSeller()).thenReturn(response);

        mockMvc.perform(get("/api/v1/analytics/sellers/most-productive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].period").value("DAY"))
                .andExpect(jsonPath("$[0].bestSeller.id").value(1))
                .andExpect(jsonPath("$[0].bestSeller.name").value("John Doe"));
    }

    @Test
    void getSellersWithSumLowerThan_shouldReturnPage() throws Exception {
        var seller = new SellerSummaryResponseDto(2L, "Jane Doe");
        var page = new PageImpl<>(List.of(seller));

        Mockito.when(analyticsService.getSellersWithSumLowerThanInPeriod(any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/analytics/sellers/sum-lower-than")
                        .param("sum", "6767.67")
                        .param("startDate", "2026-01-01T00:00:00")
                        .param("endDate", "2026-12-31T23:59:59")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Jane Doe"));

        Mockito.verify(analyticsService).getSellersWithSumLowerThanInPeriod(
                eq(new BigDecimal("6767.67")),
                eq(LocalDateTime.parse("2026-01-01T00:00:00")),
                eq(LocalDateTime.parse("2026-12-31T23:59:59")),
                any()
        );
    }

    @Test
    void getSellersWithSumLowerThan_withoutDates_itMustWork() throws Exception {
        var page = new PageImpl<SellerSummaryResponseDto>(List.of());

        Mockito.when(analyticsService.getSellersWithSumLowerThanInPeriod(any(), isNull(), isNull(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/analytics/sellers/sum-lower-than")
                        .param("sum", "67")
                )
                .andExpect(status().isOk());

        Mockito.verify(analyticsService).getSellersWithSumLowerThanInPeriod(
                eq(new BigDecimal("67")),
                isNull(),
                isNull(),
                any()
        );
    }

    @Test
    void getMostProductiveTimeForSeller_shouldReturnDto() throws Exception {
        var now = LocalDateTime.now();

        var response = new BestSellingPeriodsResponseDto(
                new BestSellingPeriodsResponseDto.PeriodData(now.minusDays(1), now, 10L),
                new BestSellingPeriodsResponseDto.PeriodData(now.minusWeeks(1), now, 50L),
                new BestSellingPeriodsResponseDto.PeriodData(now.minusMonths(1), now, 100L)
        );

        Mockito.when(analyticsService.getMostProductiveTimeForSeller(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/analytics/sellers/1/most-productive-time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bestDay.transactionCount").value(10))
                .andExpect(jsonPath("$.bestWeek.transactionCount").value(50))
                .andExpect(jsonPath("$.bestMonth.transactionCount").value(100));

        Mockito.verify(analyticsService).getMostProductiveTimeForSeller(1L);
    }

}
