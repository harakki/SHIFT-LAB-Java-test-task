package dev.harakki.shiftlab.mapper;

import dev.harakki.shiftlab.domain.Seller;
import dev.harakki.shiftlab.dto.SellerCreateDto;
import dev.harakki.shiftlab.dto.SellerResponseDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SellerMapper {

    // ### SellerCreateDto ###

    Seller toEntity(SellerCreateDto sellerCreateDto);

    SellerCreateDto toDto(Seller seller);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Seller partialUpdate(SellerCreateDto sellerCreateDto, @MappingTarget Seller seller);

    // ### SellerResponseDto ###

    Seller toEntity(SellerResponseDto sellerResponseDto);

    SellerResponseDto toDto1(Seller seller);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Seller partialUpdate(SellerResponseDto sellerResponseDto, @MappingTarget Seller seller);

}
