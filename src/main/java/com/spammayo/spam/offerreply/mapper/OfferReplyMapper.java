package com.spammayo.spam.offerreply.mapper;

import com.spammayo.spam.offerreply.dto.OfferReplyDto;
import com.spammayo.spam.offerreply.entity.OfferReply;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OfferReplyMapper {

    OfferReply replyPostDtoToOfferReply(OfferReplyDto.PostDto requestBody);

    OfferReply replyPatchDtoToOfferReply(OfferReplyDto.PatchDto requestBody);

    OfferReplyDto.ResponseDto offerReplyToOfferReplyResponseDto(OfferReply offerReply);

    List<OfferReplyDto.ResponseDto> offerRepliesToOfferReplyResponseDto(List<OfferReply> offerReplies);
}
