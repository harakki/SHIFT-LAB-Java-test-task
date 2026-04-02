package dev.harakki.shiftlab.controller;

import dev.harakki.shiftlab.dto.TransactionCreateDto;
import dev.harakki.shiftlab.dto.TransactionDetailResponseDto;
import dev.harakki.shiftlab.dto.TransactionSummaryResponseDto;
import dev.harakki.shiftlab.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    // Получить список всех транзакций
    @GetMapping
    public Page<TransactionSummaryResponseDto> getAllTransactions(Pageable pageable) {
        return transactionService.getAll(pageable);
    }

    // Получить информацию о конкретной транзакции
    @GetMapping("/{transactionId}")
    public TransactionDetailResponseDto getTransaction(@PathVariable Long transactionId) {
        return transactionService.get(transactionId);
    }

    // Создать новую транзакцию
    @PostMapping
    public TransactionDetailResponseDto createTransaction(@Valid @RequestBody TransactionCreateDto request) {
        return transactionService.create(request);
    }

    // Получить все транзакции продавца
    @GetMapping("/sellers/{sellerId}")
    public Page<TransactionSummaryResponseDto> getTransactionsBySeller(@PathVariable Long sellerId, Pageable pageable) {
        return transactionService.getBySeller(sellerId, pageable);
    }

}
