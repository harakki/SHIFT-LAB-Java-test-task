package dev.harakki.shiftlab.service;

import dev.harakki.shiftlab.domain.PaymentType;
import dev.harakki.shiftlab.domain.Seller;
import dev.harakki.shiftlab.domain.Transaction;
import dev.harakki.shiftlab.dto.TransactionCreateDto;
import dev.harakki.shiftlab.dto.TransactionDetailResponseDto;
import dev.harakki.shiftlab.dto.TransactionSummaryResponseDto;
import dev.harakki.shiftlab.exception.EntityNotFoundException;
import dev.harakki.shiftlab.mapper.TransactionMapper;
import dev.harakki.shiftlab.repository.SellerRepository;
import dev.harakki.shiftlab.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void getAll_shouldMapPageToDto() {
        Transaction tx1 = mock(Transaction.class);
        Transaction tx2 = mock(Transaction.class);

        Pageable pageable = PageRequest.of(0, 10);

        when(transactionRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(tx1, tx2)));

        when(transactionMapper.toTransactionSummaryResponseDto(tx1))
                .thenReturn(new TransactionSummaryResponseDto(1L, 10L, new BigDecimal("100.00"), LocalDateTime.now()));
        when(transactionMapper.toTransactionSummaryResponseDto(tx2))
                .thenReturn(new TransactionSummaryResponseDto(2L, 20L, new BigDecimal("200.00"), LocalDateTime.now()));

        var result = transactionService.getAll(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).id());
        assertEquals(20L, result.getContent().get(1).sellerId());
        verify(transactionRepository).findAll(pageable);
    }

    @Test
    void getAll_shouldReturnEmptyPage_whenNoTransactions() {
        Pageable pageable = PageRequest.of(0, 10);
        when(transactionRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        var result = transactionService.getAll(pageable);

        assertTrue(result.isEmpty());
        verify(transactionRepository).findAll(pageable);
    }

    @Test
    void get_shouldReturnDetail_whenTransactionExists() {
        Transaction tx = mock(Transaction.class);
        var dto = new TransactionDetailResponseDto(
                1L, 10L, new BigDecimal("123.45"), PaymentType.CASH, LocalDateTime.now()
        );

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));
        when(transactionMapper.toTransactionDetailResponseDto(tx)).thenReturn(dto);

        var result = transactionService.get(1L);

        assertEquals(1L, result.id());
        assertEquals(PaymentType.CASH, result.paymentType());
        verify(transactionRepository).findById(1L);
        verify(transactionMapper).toTransactionDetailResponseDto(tx);
    }

    @Test
    void get_shouldThrow_whenTransactionMissing() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.get(1L));

        verify(transactionRepository).findById(1L);
        verifyNoInteractions(transactionMapper);
    }

    @Test
    void create_shouldSaveTransaction_whenSellerExists() {
        var request = new TransactionCreateDto(10L, new BigDecimal("99.99"), PaymentType.CARD);
        Seller seller = mock(Seller.class);
        Transaction mapped = mock(Transaction.class);
        Transaction saved = mock(Transaction.class);
        var dto = new TransactionDetailResponseDto(
                1L, 10L, new BigDecimal("99.99"), PaymentType.CARD, LocalDateTime.now()
        );

        when(sellerRepository.findById(10L)).thenReturn(Optional.of(seller));
        when(transactionMapper.toEntity(request)).thenReturn(mapped);
        when(transactionRepository.save(mapped)).thenReturn(saved);
        when(transactionMapper.toTransactionDetailResponseDto(saved)).thenReturn(dto);

        var result = transactionService.create(request);

        assertEquals(10L, result.sellerId());
        assertEquals(PaymentType.CARD, result.paymentType());
        verify(sellerRepository).findById(10L);
        verify(transactionRepository).save(mapped);
        verify(transactionMapper).toEntity(request);
        verify(transactionMapper).toTransactionDetailResponseDto(saved);
    }

    @Test
    void create_shouldThrow_whenSellerMissing() {
        var request = new TransactionCreateDto(10L, new BigDecimal("99.99"), PaymentType.CARD);
        when(sellerRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.create(request));

        verify(sellerRepository).findById(10L);
        verifyNoInteractions(transactionMapper);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getBySeller_shouldMapPageToDto() {
        Transaction tx1 = mock(Transaction.class);
        Transaction tx2 = mock(Transaction.class);
        Pageable pageable = PageRequest.of(0, 10);

        when(transactionRepository.findBySellerId(5L, pageable))
                .thenReturn(new PageImpl<>(List.of(tx1, tx2)));

        when(transactionMapper.toTransactionSummaryResponseDto(tx1))
                .thenReturn(new TransactionSummaryResponseDto(1L, 5L, new BigDecimal("50.00"), LocalDateTime.now()));
        when(transactionMapper.toTransactionSummaryResponseDto(tx2))
                .thenReturn(new TransactionSummaryResponseDto(2L, 5L, new BigDecimal("75.00"), LocalDateTime.now()));

        var result = transactionService.getBySeller(5L, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(5L, result.getContent().get(0).sellerId());
        assertEquals(5L, result.getContent().get(1).sellerId());
        verify(transactionRepository).findBySellerId(5L, pageable);
    }

    @Test
    void getBySeller_shouldReturnEmptyPage_whenSellerHasNoTransactions() {
        Pageable pageable = PageRequest.of(0, 10);
        when(transactionRepository.findBySellerId(5L, pageable)).thenReturn(new PageImpl<>(List.of()));

        var result = transactionService.getBySeller(5L, pageable);

        assertTrue(result.isEmpty());
        verify(transactionRepository).findBySellerId(5L, pageable);
    }

}
