package com.spammayo.spam.offerreply.mapper;

import com.spammayo.spam.offerreply.dto.OfferReplyDto;
import com.spammayo.spam.offerreply.entity.OfferReply;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OfferReplyMapper {

    OfferReply replyPostDtoToOfferReply(OfferReplyDto.PostDto requestBody);

    OfferReply replyPatchDtoToOfferReply(OfferReplyDto.PatchDto requestBody);

    default OfferReplyDto.ResponseDto offerReplyToOfferReplyResponseDto(OfferReply offerReply) {

        return OfferReplyDto.ResponseDto.builder()
                .replyId(offerReply.getReplyId())
                .userId(offerReply.getUser().getUserId())
                .userName(offerReply.getUser().getUserName())
                .profileUrl(offerReply.getUser().getProfileUrl())
                .createdAt(offerReply.getCreatedAt())
                .offerReply(offerReply.getOfferReply())
                .secret(offerReply.getSecret())
                .build();
    }

    List<OfferReplyDto.ResponseDto> offerRepliesToOfferReplyResponseDto(List<OfferReply> offerReplies);
}
