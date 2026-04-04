package dev.harakki.shiftlab.controller;

import dev.harakki.shiftlab.dto.TransactionCreateDto;
import dev.harakki.shiftlab.dto.TransactionDetailResponseDto;
import dev.harakki.shiftlab.dto.TransactionSummaryResponseDto;
import dev.harakki.shiftlab.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Управление транзакциями
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Получить список всех транзакций
     *
     * @param pageable Настройки пагинации и сортировки
     * @return Страница с общей информацией о всех транзакциях ({@link TransactionSummaryResponseDto})
     */
    @GetMapping
    public Page<TransactionSummaryResponseDto> getAllTransactions(Pageable pageable) {
        return transactionService.getAll(pageable);
    }

    /**
     * Получить подробную информацию о конкретной транзакции
     *
     * @param transactionId Уникальный идентификатор транзакции
     * @return {@link TransactionDetailResponseDto} - детальная информация о найденной транзакции
     */
    @GetMapping("/{transactionId}")
    public TransactionDetailResponseDto getTransaction(@PathVariable Long transactionId) {
        return transactionService.get(transactionId);
    }

    /**
     * Создать новую транзакцию
     *
     * @param request {@link TransactionCreateDto} - DTO с данными для создания транзакции
     * @return {@link TransactionDetailResponseDto} - детальная информация о созданной транзакции
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDetailResponseDto createTransaction(@Valid @RequestBody TransactionCreateDto request) {
        return transactionService.create(request);
    }

    /**
     * Получить все транзакции продавца
     *
     * @param sellerId Уникальный идентификатор продавца
     * @param pageable Настройки пагинации и сортировки
     * @return Страница с общей информацией о всех транзакциях продавца ({@link TransactionSummaryResponseDto})
     */
    @GetMapping("/sellers/{sellerId}")
    public Page<TransactionSummaryResponseDto> getTransactionsBySeller(@PathVariable Long sellerId, Pageable pageable) {
        return transactionService.getBySeller(sellerId, pageable);
    }

}
