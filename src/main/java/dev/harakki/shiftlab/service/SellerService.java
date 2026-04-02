package dev.harakki.shiftlab.service;

import dev.harakki.shiftlab.domain.Seller;
import dev.harakki.shiftlab.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
class SellerService {

    private final SellerRepository sellerRepository;

    public Optional<Seller> findSellerById(Long sellerId) {
        return sellerRepository.findSellerById(sellerId);
    }

}
