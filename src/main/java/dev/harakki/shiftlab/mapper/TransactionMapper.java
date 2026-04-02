package dev.harakki.shiftlab.mapper;

import dev.harakki.shiftlab.domain.Transaction;
import dev.harakki.shiftlab.dto.TransactionCreateDto;
import dev.harakki.shiftlab.dto.TransactionResponseDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {SellerMapper.class})
public interface TransactionMapper {

    // ### TransactionCreateDto ###

    Transaction toEntity(TransactionCreateDto transactionCreateDto);

    TransactionCreateDto toDto(Transaction transaction);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Transaction partialUpdate(TransactionCreateDto transactionCreateDto, @MappingTarget Transaction transaction);

    // ### TransactionResponseDto ###

    Transaction toEntity(TransactionResponseDto transactionResponseDto);

    TransactionResponseDto toDto1(Transaction transaction);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Transaction partialUpdate(TransactionResponseDto transactionResponseDto, @MappingTarget Transaction transaction);

}
