package dev.harakki.shiftlab.mapper;

import dev.harakki.shiftlab.domain.Seller;
import dev.harakki.shiftlab.dto.SellerCreateDto;
import dev.harakki.shiftlab.dto.SellerDetailResponseDto;
import dev.harakki.shiftlab.dto.SellerSummaryResponseDto;
import dev.harakki.shiftlab.dto.SellerUpdateDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SellerMapper {

    // ### SellerCreateDto ###

    Seller toEntity(SellerCreateDto sellerCreateDto);

    // ### SellerDetailResponseDto ###

    SellerDetailResponseDto toSellerDetailResponseDto(Seller seller);

    // ### SellerSummaryResponseDto ###

    SellerSummaryResponseDto toSellerSummaryResponseDto(Seller seller);

    // ### SellerUpdateDto ###

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Seller partialUpdate(SellerUpdateDto sellerUpdateDto, @MappingTarget Seller seller);

}
