package dev.harakki.shiftlab.controller;

import dev.harakki.shiftlab.domain.PaymentType;
import dev.harakki.shiftlab.dto.TransactionCreateDto;
import dev.harakki.shiftlab.dto.TransactionDetailResponseDto;
import dev.harakki.shiftlab.dto.TransactionSummaryResponseDto;
import dev.harakki.shiftlab.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllTransactions_shouldReturnPage() throws Exception {
        var tx = new TransactionSummaryResponseDto(
                1L,
                10L,
                new BigDecimal("100.00"),
                LocalDateTime.now()
        );
        var page = new PageImpl<>(List.of(tx));

        Mockito.when(transactionService.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/transactions")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].sellerId").value(10))
                .andExpect(jsonPath("$.content[0].amount").value(100.00));
    }

    @Test
    void getTransaction_shouldReturnDetail() throws Exception {
        var response = new TransactionDetailResponseDto(
                1L,
                10L,
                new BigDecimal("100.00"),
                PaymentType.CASH,
                LocalDateTime.now()
        );

        Mockito.when(transactionService.get(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sellerId").value(10))
                .andExpect(jsonPath("$.paymentType").value("CASH"));
    }

    @Test
    void createTransaction_shouldReturnCreatedObject() throws Exception {
        var request = new TransactionCreateDto(
                10L,
                new BigDecimal("200.00"),
                PaymentType.CARD
        );

        var response = new TransactionDetailResponseDto(
                1L,
                10L,
                new BigDecimal("200.00"),
                PaymentType.CARD,
                LocalDateTime.now()
        );

        Mockito.when(transactionService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.paymentType").value("CARD"));

        Mockito.verify(transactionService).create(any());
    }

    @Test
    void createTransaction_shouldFail_whenAmountNegative() throws Exception {
        var request = new TransactionCreateDto(
                10L,
                new BigDecimal("-10"),
                PaymentType.CASH
        );

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTransaction_shouldFail_whenSellerIdNull() throws Exception {
        var json = """
                {
                  "amount": 100,
                  "paymentType": "CASH"
                }
                """;

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTransactionsBySeller_shouldReturnPage() throws Exception {
        var tx = new TransactionSummaryResponseDto(
                1L,
                5L,
                new BigDecimal("50.00"),
                LocalDateTime.now()
        );

        var page = new PageImpl<>(List.of(tx));

        Mockito.when(transactionService.getBySeller(eq(5L), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/transactions/sellers/5")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].sellerId").value(5));

        Mockito.verify(transactionService).getBySeller(eq(5L), any());
    }

}