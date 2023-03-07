package com.spammayo.spam.offer.mapper;

import com.spammayo.spam.offer.dto.OfferDto;
import com.spammayo.spam.offer.entity.Offer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    Offer postDtoToOffer(OfferDto.PostDto postDto);

    Offer patchDtoToOffer(OfferDto.PatchDto patchDto);

    OfferDto.SimpleResponseDto offerToSimpleResponseDto(Offer offer);

    OfferDto.ResponseDto offerToResponseDto(Offer offer);

}
