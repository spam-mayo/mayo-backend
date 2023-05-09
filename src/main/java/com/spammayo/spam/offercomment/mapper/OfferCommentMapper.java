package com.spammayo.spam.offercomment.mapper;

import com.spammayo.spam.offer.entity.Offer;
import com.spammayo.spam.offercomment.dto.OfferCommentDto;
import com.spammayo.spam.offercomment.entity.OfferComment;
import com.spammayo.spam.offerreply.dto.OfferReplyDto;
import com.spammayo.spam.offerreply.entity.OfferReply;
import com.spammayo.spam.offerreply.repository.OfferReplyRepository;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OfferCommentMapper {

    default OfferComment commentPostDtoToComment(OfferCommentDto.PostDto requestBody) {
        Offer offer = new Offer();
        offer.setOfferId(requestBody.getOfferId());

        OfferComment comment = new OfferComment();
        comment.setOffer(offer);
        comment.setComment(requestBody.getComment());
        comment.setSecret(requestBody.getSecret());

        return comment;
    }

    default OfferComment commentPatchDtoToComment(OfferCommentDto.PatchDto requestBody) {
        Offer offer = new Offer();
        offer.setOfferId(requestBody.getOfferId());

        OfferComment comment = new OfferComment();
        comment.setOffer(offer);
        comment.setComment(requestBody.getComment());
        comment.setSecret(requestBody.getSecret());

        return comment;
    }

    default OfferCommentDto.ResponseDto commentToCommentResponseDto(OfferComment comment) {

        return OfferCommentDto.ResponseDto.builder()
                .offerCommentId(comment.getOfferCommentId())
                .comment(comment.getComment())
                .secret(comment.getSecret())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();
    }

    default List<OfferCommentDto.AllResponseDto> commentsToCommentAllResponseDto(List<OfferComment> comment,
                                                                                 OfferReplyRepository offerReplyRepository) {

        return comment.stream()
                .map(offerComment -> OfferCommentDto.AllResponseDto.builder()
                        .userId(offerComment.getUser().getUserId())
                        .userName(offerComment.getUser().getUserName())
                        .profileUrl(offerComment.getUser().getProfileUrl())
                        .createdAt(offerComment.getCreatedAt())
                        .offerCommentId(offerComment.getOfferCommentId())
                        .comment(offerComment.getComment())
                        .secret(offerComment.getSecret())
                        .replies(offerRepliesToOfferReplyResponseDto(offerReplyRepository.findAllByOfferComment_OfferCommentId(offerComment.getOfferCommentId())))
                        .build())
                .collect(Collectors.toList());
    }

    default List<OfferReplyDto.ResponseDto> offerRepliesToOfferReplyResponseDto(List<OfferReply> offerReplies) {

        return offerReplies.stream()
                .map(reply -> OfferReplyDto.ResponseDto.builder()
                        .replyId(reply.getReplyId())
                        .userId(reply.getUser().getUserId())
                        .userName(reply.getUser().getUserName())
                        .profileUrl(reply.getUser().getProfileUrl())
                        .createdAt(reply.getCreatedAt())
                        .offerReply(reply.getOfferReply())
                        .secret(reply.getSecret())
                        .build())
                .collect(Collectors.toList());
    }
}
