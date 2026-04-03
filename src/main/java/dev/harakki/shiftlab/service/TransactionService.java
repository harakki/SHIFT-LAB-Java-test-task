package dev.harakki.shiftlab.service;

import dev.harakki.shiftlab.domain.Transaction;
import dev.harakki.shiftlab.dto.TransactionCreateDto;
import dev.harakki.shiftlab.dto.TransactionDetailResponseDto;
import dev.harakki.shiftlab.dto.TransactionSummaryResponseDto;
import dev.harakki.shiftlab.exception.EntityNotFoundException;
import dev.harakki.shiftlab.mapper.TransactionMapper;
import dev.harakki.shiftlab.repository.SellerRepository;
import dev.harakki.shiftlab.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;

    private final TransactionMapper transactionMapper;

    public Page<TransactionSummaryResponseDto> getAll(Pageable pageable) {
        return transactionRepository.findAll(pageable).map(transactionMapper::toTransactionSummaryResponseDto);
    }

    public TransactionDetailResponseDto get(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .map(transactionMapper::toTransactionDetailResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with id " + transactionId + " not found"));
    }

    @Transactional
    public TransactionDetailResponseDto create(TransactionCreateDto request) {
        var seller = sellerRepository.findById(request.sellerId())
                .orElseThrow(() -> new EntityNotFoundException("Seller with id " + request.sellerId() + " not found"));

        var transaction = transactionMapper.toEntity(request);

        var result = transactionRepository.save(transaction);
        return transactionMapper.toTransactionDetailResponseDto(result);
    }

    public Page<TransactionSummaryResponseDto> getBySeller(Long sellerId, Pageable pageable) {
        return transactionRepository.findBySellerId(sellerId, pageable)
                .map(transactionMapper::toTransactionSummaryResponseDto);
    }

}
