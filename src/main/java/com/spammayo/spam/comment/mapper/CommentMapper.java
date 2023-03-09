package com.spammayo.spam.comment.mapper;

import com.spammayo.spam.comment.dto.CommentDto;
import com.spammayo.spam.comment.entity.Comment;
import com.spammayo.spam.offer.entity.Offer;
import com.spammayo.spam.user.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    default Comment commentPostDtoToComment(CommentDto.PostDto requestBody) {
        Offer offer = new Offer();
        offer.setOfferId(requestBody.getOfferId());

        Comment comment = new Comment();
        comment.setOffer(offer);
        comment.setComment(requestBody.getComment());
        comment.setSecret(requestBody.getSecret());

        return comment;
    }

    default Comment commentPatchDtoToComment(CommentDto.PatchDto requestBody) {
        Offer offer = new Offer();
        offer.setOfferId(requestBody.getOfferId());

        Comment comment = new Comment();
        comment.setOffer(offer);
        comment.setComment(requestBody.getComment());
        comment.setSecret(requestBody.getSecret());

        return comment;
    }

    default CommentDto.ResponseDto commentToCommentResponseDto(Comment comment) {

        User user = comment.getUser();
        Offer offer = comment.getOffer();

        return CommentDto.ResponseDto.builder()
                .commentId(comment.getCommentId())
                .offerId(offer.getOfferId())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .profileUrl(user.getProfileUrl())
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();
    }

    List<CommentDto.ResponseDto> commentsToCommentResponseDto(List<Comment> comments);
}
