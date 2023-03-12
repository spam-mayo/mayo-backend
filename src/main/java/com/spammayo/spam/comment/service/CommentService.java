package com.spammayo.spam.comment.service;

import com.spammayo.spam.comment.entity.Comment;
import com.spammayo.spam.comment.repository.CommentRepository;
import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.offer.entity.Offer;
import com.spammayo.spam.offer.service.OfferService;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final OfferService offerService;

    public Comment createComment(Comment comment,
                                 Long offerId) {

        Offer offer = offerService.findOffer(offerId);
        User user = userService.getLoginUser();

        offer.addComment(comment);
        comment.setUser(user);
        comment.setOffer(offer);

        return commentRepository.save(comment);
    }

    public Comment updateComment(Comment comment) {

        Comment findComment = verifiedComment(comment.getCommentId());

        // TODO : 스터디 방장 권한 추가
        if (!findComment.getUser().getEmail().equals(userService.getLoginUser().getEmail())) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }

        Optional.ofNullable(comment.getComment())
                .ifPresent(findComment::setComment);

        Optional.ofNullable(comment.getSecret())
                .ifPresent(findComment::setSecret);

        return commentRepository.save(findComment);
    }

    public Comment findComment(Long commentId) {
        return verifiedComment(commentId);
    }

    public List<Comment> findComments() {
        return commentRepository.findAll();
    }

    public void deleteComment(Long commentId) {

        Comment findComment = verifiedComment(commentId);

        // TODO : 스터디 방장 권한 추가
        if (!findComment.getUser().getEmail().equals(userService.getLoginUser().getEmail())) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_FORBIDDEN);
        }

        commentRepository.delete(findComment);
    }
    // 댓글 존재 여부
    private Comment verifiedComment(Long commentId) {

        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
    }
}
