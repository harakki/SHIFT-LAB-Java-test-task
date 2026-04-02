package dev.harakki.shiftlab.controller;

import dev.harakki.shiftlab.dto.SellerCreateDto;
import dev.harakki.shiftlab.dto.SellerDetailResponseDto;
import dev.harakki.shiftlab.dto.SellerSummaryResponseDto;
import dev.harakki.shiftlab.dto.SellerUpdateDto;
import dev.harakki.shiftlab.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/sellers")
public class SellerController {

    private final SellerService sellerService;

    // Список всех продавцов
    @GetMapping
    public List<SellerSummaryResponseDto> getAllSellers() {
        return sellerService.getAll();
    }

    // Инфо о конкретном продавце
    @GetMapping("/{sellerId}")
    public SellerDetailResponseDto getSeller(@PathVariable Long sellerId) {
        return sellerService.get(sellerId);
    }

    // Создать нового продавца
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SellerDetailResponseDto createSeller(@RequestBody SellerCreateDto request) {
        return sellerService.create(request);
    }

    // Обновить инфо о продавце
    @PatchMapping("/{sellerId}")
    public SellerDetailResponseDto updateSeller(@PathVariable Long sellerId, @RequestBody SellerUpdateDto request) {
        return sellerService.update(sellerId, request);
    }

    // Удалить продавца
    @DeleteMapping("/{sellerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeller(@PathVariable Long sellerId) {
        sellerService.delete(sellerId);
    }

}
