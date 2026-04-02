package dev.harakki.shiftlab.controller;

import dev.harakki.shiftlab.dto.TransactionCreateDto;
import dev.harakki.shiftlab.dto.TransactionDetailResponseDto;
import dev.harakki.shiftlab.dto.TransactionSummaryResponseDto;
import dev.harakki.shiftlab.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    // Получить список всех транзакций
    @GetMapping
    public List<TransactionSummaryResponseDto> getAllTransactions() {
        return transactionService.getAll();
    }

    // Получить информацию о конкретной транзакции
    @GetMapping("/{transactionId}")
    public TransactionDetailResponseDto getTransaction(@PathVariable Long transactionId) {
        return transactionService.get(transactionId);
    }

    // Создать новую транзакцию
    @PostMapping
    public TransactionDetailResponseDto createTransaction(@RequestBody TransactionCreateDto request) {
        return transactionService.create(request);
    }

    // Получить все транзакции продавца
    @GetMapping("/sellers/{sellerId}")
    public List<TransactionSummaryResponseDto> getTransactionsBySeller(@PathVariable Long sellerId) {
        return transactionService.getBySeller(sellerId);
    }

}
