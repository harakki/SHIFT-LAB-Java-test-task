package dev.harakki.shiftlab.mapper;

import dev.harakki.shiftlab.domain.Transaction;
import dev.harakki.shiftlab.dto.TransactionCreateDto;
import dev.harakki.shiftlab.dto.TransactionDetailResponseDto;
import dev.harakki.shiftlab.dto.TransactionSummaryResponseDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {SellerMapper.class})
public interface TransactionMapper {

    // ### TransactionCreateDto ###

    @Mapping(target = "seller.id", source = "sellerId")
    Transaction toEntity(TransactionCreateDto transactionCreateDto);

    // ### TransactionDetailResponseDto ###

    @Mapping(target = "sellerId", source = "seller.id")
    TransactionDetailResponseDto toTransactionDetailResponseDto(Transaction transaction);

    // ### TransactionSummaryResponseDto ###

    @Mapping(target = "sellerId", source = "seller.id")
    TransactionSummaryResponseDto toTransactionSummaryResponseDto(Transaction transaction);

}
