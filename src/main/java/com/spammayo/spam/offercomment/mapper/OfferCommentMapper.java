package com.spammayo.spam.offercomment.mapper;

import com.spammayo.spam.offer.entity.Offer;
import com.spammayo.spam.offercomment.dto.OfferCommentDto;
import com.spammayo.spam.offercomment.entity.OfferComment;
import com.spammayo.spam.user.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

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

        User user = comment.getUser();
        Offer offer = comment.getOffer();

        return OfferCommentDto.ResponseDto.builder()
                .offerCommentId(comment.getOfferCommentId())
                .offerId(offer.getOfferId())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .profileUrl(user.getProfileUrl())
                .comment(comment.getComment())
                .secret(comment.getSecret())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();
    }

    List<OfferCommentDto.ResponseDto> commentsToCommentResponseDto(List<OfferComment> comments);
}
