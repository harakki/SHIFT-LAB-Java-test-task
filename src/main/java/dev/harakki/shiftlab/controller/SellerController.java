package dev.harakki.shiftlab.controller;

import dev.harakki.shiftlab.dto.SellerCreateDto;
import dev.harakki.shiftlab.dto.SellerDetailResponseDto;
import dev.harakki.shiftlab.dto.SellerSummaryResponseDto;
import dev.harakki.shiftlab.dto.SellerUpdateDto;
import dev.harakki.shiftlab.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Управление продавцами
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/sellers")
public class SellerController {

    private final SellerService sellerService;

    /**
     * Получить список всех продавцов
     *
     * @param pageable Настройки пагинации и сортировки
     * @return Страница с общей информацией о всех продавцах ({@link SellerSummaryResponseDto})
     */
    @GetMapping
    public Page<SellerSummaryResponseDto> getAllSellers(Pageable pageable) {
        return sellerService.getAll(pageable);
    }

    /**
     * Получить подробную информацию о конкретном продавце
     *
     * @param sellerId Уникальный идентификатор продавца
     * @return {@link SellerDetailResponseDto} - детальная информация о найденном продавце
     */
    @GetMapping("/{sellerId}")
    public SellerDetailResponseDto getSeller(@PathVariable Long sellerId) {
        return sellerService.get(sellerId);
    }

    /**
     * Создать нового продавца
     *
     * @param request {@link SellerCreateDto} - DTO с данными для создания продавца
     * @return {@link SellerDetailResponseDto} - детальная информация о созданном продавце
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SellerDetailResponseDto createSeller(@Valid @RequestBody SellerCreateDto request) {
        return sellerService.create(request);
    }

    /**
     * Обновить информацию о продавце
     *
     * @param sellerId Уникальный идентификатор обновляемого продавца
     * @param request  {@link SellerUpdateDto} - DTO с данными для обновления продавца
     * @return {@link SellerDetailResponseDto} - детальная информация о созданном продавце
     */
    @PatchMapping("/{sellerId}")
    public SellerDetailResponseDto updateSeller(@PathVariable Long sellerId, @Valid @RequestBody SellerUpdateDto request) {
        return sellerService.update(sellerId, request);
    }

    /**
     * Удалить продавца
     *
     * @param sellerId Уникальный идентификатор удаляемого продавца
     */
    @DeleteMapping("/{sellerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeller(@PathVariable Long sellerId) {
        sellerService.delete(sellerId);
    }

}
