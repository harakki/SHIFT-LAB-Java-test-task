package dev.harakki.shiftlab.service;

import dev.harakki.shiftlab.domain.Seller;
import dev.harakki.shiftlab.dto.SellerCreateDto;
import dev.harakki.shiftlab.dto.SellerDetailResponseDto;
import dev.harakki.shiftlab.dto.SellerSummaryResponseDto;
import dev.harakki.shiftlab.dto.SellerUpdateDto;
import dev.harakki.shiftlab.exception.EntityNotFoundException;
import dev.harakki.shiftlab.mapper.SellerMapper;
import dev.harakki.shiftlab.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final SellerMapper sellerMapper;

    public Optional<Seller> findSellerById(Long sellerId) {
        return sellerRepository.findById(sellerId);
    }

    public Page<SellerSummaryResponseDto> getAll(Pageable pageable) {
        return sellerRepository.findAll(pageable).map(sellerMapper::toSellerSummaryResponseDto);
    }

    public SellerDetailResponseDto get(Long sellerId) {
        return findSellerById(sellerId)
                .map(sellerMapper::toSellerDetailResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Seller with id " + sellerId + " not found"));
    }

    @Transactional
    public SellerDetailResponseDto create(SellerCreateDto createDto) {
        var seller = sellerMapper.toEntity(createDto);
        var result = sellerRepository.save(seller);
        return sellerMapper.toSellerDetailResponseDto(result);
    }

    @Transactional
    public SellerDetailResponseDto update(Long sellerId, SellerUpdateDto request) {
        var seller = findSellerById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("Seller with id " + sellerId + " not found"));
        var updated = sellerMapper.partialUpdate(request, seller);
        var result = sellerRepository.save(updated);
        return sellerMapper.toSellerDetailResponseDto(result);
    }

    @Transactional
    public void delete(Long sellerId) {
        if (!sellerRepository.existsById(sellerId)) {
            throw new EntityNotFoundException("Seller with id " + sellerId + " not found");
        }
        sellerRepository.deleteById(sellerId);
    }

}
